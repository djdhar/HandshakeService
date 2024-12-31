package com.project.makehandshake.controller;

import com.project.makehandshake.model.HandShake;
import com.project.makehandshake.model.HandShakeResponse;
import com.project.makehandshake.service.KafkaConsumerService;
import com.project.makehandshake.service.MakeHandshakeServ;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Service is healthy!");
    }

    @GetMapping(value = "/v1/stream_admin_handshake", produces = "text/event-stream")
    public Flux<String> streamAdminHandshakeMessages() {
        return makeHandshakeServ.streamHandshakeMessages();
    }

    @GetMapping(value = "/v1/stream_live_handshake", produces = "text/event-stream")
    public Flux<String> streamLiveHandshakeMessages() {
        return makeHandshakeServ.streamLiveHandshakeMessages();
    }
}
