package com.project.makehandshake.service;

import com.google.gson.Gson;
import com.project.makehandshake.model.HandShake;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class KafkaConsumerService {

    Gson gson;

    public KafkaConsumerService(Gson gson) {
        this.gson = gson;
    }

    private final Sinks.Many<HandShake> messageSink = Sinks.many().replay().all();

    @KafkaListener(topics = "djdhar", groupId = "handshake-group")
    public void listen(String message) {
        try {
            HandShake handShake = gson.fromJson(message, HandShake.class);
            if(HandShake.isValidHandShake(handShake)) messageSink.tryEmitNext(handShake);
        } catch (Exception e) {
            System.out.println("Exception occurred!");
        }

    }

    public Flux<HandShake> getMessageStream() {
        return messageSink.asFlux();
    }
}

