package com.project.makehandshake.service;

import com.project.makehandshake.model.HandShake;
import com.project.makehandshake.model.HandShakeErrorResponse;
import com.project.makehandshake.model.HandShakeResponse;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MakeHandshakeServ {
    public Mono<HandShakeResponse> makeHandshakeService(HandShake handShake) {
        if(StringUtil.isNullOrEmpty(handShake.getName()) || StringUtil.isNullOrEmpty(handShake.getEmail())) {
            return Mono.error(new HandShakeErrorResponse("Handshake request error: " + handShake));
        } else {
            return Mono.just(new HandShakeResponse(handShake + "request triggered!"));
        }
    }
}
