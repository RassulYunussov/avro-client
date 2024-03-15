package org.example;

import io.netty.buffer.Unpooled;
import org.example.avro.User;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;

public class App {
    private static final AvroSerializer<User> avroSerializer = new AvroSerializer<>(User.getClassSchema());

    public static void main(String[] args) throws IOException {
        final var user = UserFactory.getAvroUser();
        final var serialized = avroSerializer.serialize(user);
        final var responseBytes = HttpClient.create()
                .baseUrl("http://localhost:8080")
                .post()
                .uri("/avro")
                .send(Flux.just(Unpooled.copiedBuffer(serialized)))
                .responseSingle((res, content) -> content.asByteArray())
                .block();
        System.out.println(avroSerializer.deserialize(responseBytes));
    }
}
