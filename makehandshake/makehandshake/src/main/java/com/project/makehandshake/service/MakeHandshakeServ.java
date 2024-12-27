package com.project.makehandshake.service;

import com.project.makehandshake.model.HandShake;
import com.project.makehandshake.model.HandShakeErrorResponse;
import com.project.makehandshake.model.HandShakeResponse;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MakeHandshakeServ {
    KafkaPublisherService kafkaPublisherService;
    KafkaConsumerService kafkaConsumerService;

    public MakeHandshakeServ(KafkaPublisherService kafkaPublisherService, KafkaConsumerService kafkaConsumerService) {
        this.kafkaPublisherService = kafkaPublisherService;
        this.kafkaConsumerService = kafkaConsumerService;
    }
    public Mono<HandShakeResponse> makeHandshakeService(HandShake handShake) {
        if(StringUtil.isNullOrEmpty(handShake.getName()) || StringUtil.isNullOrEmpty(handShake.getEmail())) {
            return Mono.error(new HandShakeErrorResponse("Handshake request error: " + handShake));
        } else {
            return kafkaPublisherService.sendMessage("djdhar", handShake)
                    .then(Mono.just(new HandShakeResponse(handShake + "request triggered!")));
        }
    }

    public Flux<String> streamHandshakeMessages() {
        return kafkaConsumerService.getMessageStream()
                .buffer(2)// Group elements into lists of size 2
                .flatMap(duo ->
                        HandShake.isValidHandShake(duo.get(0)) && HandShake.isValidHandShake(duo.get(1)) ?
                                Flux.just(duo.get(0) + " handshaked with " + duo.get(1)) : Flux.empty()
                );
    }
}
