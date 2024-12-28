package com.project.makehandshake.service;

import com.google.gson.Gson;
import com.project.makehandshake.model.HandShake;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class KafkaConsumerService {

    Gson gson;
    EmailService emailService;
    private static final List<String> messageBuffer = new ArrayList<>();
    private final Sinks.Many<List<HandShake>> kafkaMessageSink;
    private final Sinks.Many<List<HandShake>> userMessageSink;
    private static final String HANDSHAKE_SUBJECT = "Hi! You got a new handshake.";

    public KafkaConsumerService(Gson gson, EmailService emailService) {
        this.gson = gson;
        kafkaMessageSink = Sinks.many().replay().limit(100);
        userMessageSink =  Sinks.many().replay().limit(1);
        this.emailService = emailService;
    }



    @KafkaListener(topics = "djdhar", groupId = "handshake-group", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message, Acknowledgment acknowledgment) {
        synchronized (messageBuffer) {
            // Add the incoming message to the buffer
            messageBuffer.add(message);

            // Process only when two messages are available
            if (messageBuffer.size() == 2) {
                try {
                    HandShake handShake1 = gson.fromJson(messageBuffer.get(0), HandShake.class);
                    HandShake handShake2 = gson.fromJson(messageBuffer.get(1), HandShake.class);

                    if (HandShake.isValidHandShake(handShake1) && HandShake.isValidHandShake(handShake2)) {
                        String handShakeMail = handShake1.getName() + " handshaked with " + handShake2.getName();
                        System.out.println(handShakeMail);
                        ExecutorService executorService = Executors.newFixedThreadPool(2);

                        // Run emailService in separate threads
                        executorService.submit(() -> emailService.sendEmail(handShake1.getEmail(), HANDSHAKE_SUBJECT, handShakeMail));
                        executorService.submit(() -> emailService.sendEmail(handShake2.getEmail(), HANDSHAKE_SUBJECT, handShakeMail));

                        userMessageSink.tryEmitNext(List.of(deepCopy(handShake1), deepCopy(handShake2)));
                        kafkaMessageSink.tryEmitNext(List.of(deepCopy(handShake1), deepCopy(handShake2)));
                        System.out.println("Processed batch: " + messageBuffer);
                    } else {
                        System.out.println("One or both messages are invalid.");
                    }

                } catch (Exception e) {
                    System.out.println("Exception occurred while processing batch: " + messageBuffer);
                    e.printStackTrace();
                } finally {
                    // Clear the buffer after processing and acknowledge the messages
                    messageBuffer.clear();
                    acknowledgment.acknowledge();
                }
            } else {
                System.out.println("Message added to buffer. Waiting for more messages...");
            }
        }
    }

    public Flux<List<HandShake>> getAdminUnreadMessageStream() {
        return kafkaMessageSink.asFlux();
    }
    public Flux<List<HandShake>>
    getLiveMessageStream() {
        return userMessageSink.asFlux()
                .doOnSubscribe(subscription -> System.out.println("Subscriber connected"))
                .doOnNext(event -> System.out.println("Event emitted: " + event))
                .doOnCancel(() -> System.out.println("Subscriber disconnected"))
                .share();
    }

    private HandShake deepCopy(HandShake handShake) {
        return gson.fromJson(gson.toJson(handShake), HandShake.class);
    }
}

