package com.project.makehandshake.service;

import com.google.gson.Gson;
import com.project.makehandshake.model.HandShake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class KafkaPublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson;

    @Autowired
    public KafkaPublisherService(KafkaTemplate<String, String> kafkaTemplate, Gson gson) {
        this.kafkaTemplate = kafkaTemplate;
        this.gson = gson;
    }

    public Mono<Void> sendMessage(String topic, HandShake handShake) {
        String handShakeString = gson.toJson(handShake);
        return Mono.fromRunnable(() -> kafkaTemplate.send(topic, handShakeString));
    }
}

