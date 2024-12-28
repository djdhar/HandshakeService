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
        return kafkaConsumerService.getAdminUnreadMessageStream()
                .flatMap(duo -> {
                            if(duo.size()==2 && HandShake.isValidHandShake(duo.get(0)) && HandShake.isValidHandShake(duo.get(1))) {
                                try {
                                    String handShakeMail = duo.get(0).getName() + " handshaked with " + duo.get(1).getName();
                                    System.out.println(handShakeMail);
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

    public Flux<String> streamLiveHandshakeMessages() {
        return kafkaConsumerService.getLiveMessageStream()
                .flatMap(duo -> {
                            if(duo.size()==2 && HandShake.isValidHandShake(duo.get(0)) && HandShake.isValidHandShake(duo.get(1))) {
                                try {
                                    String handShakeMail = duo.get(0).getName() + " handshaked with " + duo.get(1).getName();
                                    System.out.println(handShakeMail);
                                    return Flux.just(handShakeMail);
                                } catch (Exception e) {
                                    System.out.println(e.getMessage());
                                    return Flux.empty();
                                }
                            } else {
                                return Flux.empty();
                            }
                        }
                ).onErrorResume(e -> {
                    System.err.println("Stream error: " + e.getMessage());
                    return Flux.never(); // Keep the connection alive on errors
                });
    }
}
