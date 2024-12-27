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
    EmailService emailService;
    private static final String HANDSHAKE_SUBJECT = "Hi! You got a new handshake.";

    public MakeHandshakeServ(KafkaPublisherService kafkaPublisherService,
                             KafkaConsumerService kafkaConsumerService,
                             EmailService emailService) {
        this.kafkaPublisherService = kafkaPublisherService;
        this.kafkaConsumerService = kafkaConsumerService;
        this.emailService = emailService;
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
                .flatMap(duo -> {
                            if(HandShake.isValidHandShake(duo.get(0)) && HandShake.isValidHandShake(duo.get(1))) {
                                try {
                                    String handShakeMail = duo.get(0).getName() + " handshaked with " + duo.get(1).getName();
                                    System.out.println(handShakeMail);
                                    emailService.sendEmail(duo.get(0).getEmail(), HANDSHAKE_SUBJECT, handShakeMail);
                                    emailService.sendEmail(duo.get(1).getEmail(), HANDSHAKE_SUBJECT, handShakeMail);
                                    return Flux.just(handShakeMail);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                    return Flux.empty();
                                }
                            } else {
                                return Flux.empty();
                             }
                        }
                );
    }
}
