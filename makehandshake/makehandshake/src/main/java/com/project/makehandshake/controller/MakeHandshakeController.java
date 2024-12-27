package com.project.makehandshake.controller;

import com.project.makehandshake.model.HandShake;
import com.project.makehandshake.model.HandShakeResponse;
import com.project.makehandshake.service.KafkaConsumerService;
import com.project.makehandshake.service.MakeHandshakeServ;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class MakeHandshakeController {
    MakeHandshakeServ makeHandshakeServ;
    KafkaConsumerService kafkaConsumerService;

    public MakeHandshakeController(MakeHandshakeServ makeHandshakeServ, KafkaConsumerService kafkaConsumerService) {
        this.makeHandshakeServ = makeHandshakeServ;
        this.kafkaConsumerService = kafkaConsumerService;
    }

    @PostMapping("/v1/create_handshake")
    public Mono<HandShakeResponse> createHandshake(@Validated @RequestBody HandShake handShake) {
        return makeHandshakeServ.makeHandshakeService(handShake);
    }

    @GetMapping(value = "/v1/stream_handshake", produces = "text/event-stream")
    public Flux<String> streamHandshakeMessages() {
        return makeHandshakeServ.streamHandshakeMessages();
    }
}
