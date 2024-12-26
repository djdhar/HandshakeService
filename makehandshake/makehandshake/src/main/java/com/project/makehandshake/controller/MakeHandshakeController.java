package com.project.makehandshake.controller;

import com.project.makehandshake.model.HandShake;
import com.project.makehandshake.model.HandShakeResponse;
import com.project.makehandshake.service.MakeHandshakeServ;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MakeHandshakeController {
    MakeHandshakeServ makeHandshakeServ;

    public MakeHandshakeController(MakeHandshakeServ makeHandshakeServ) {
        this.makeHandshakeServ = makeHandshakeServ;
    }

    @PostMapping("/v1/create_handshake")
    public Mono<HandShakeResponse> createHandshake(@Validated @RequestBody HandShake handShake) {
        return makeHandshakeServ.makeHandshakeService(handShake);
    }
}
