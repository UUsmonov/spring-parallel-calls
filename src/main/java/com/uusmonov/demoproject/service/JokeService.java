package com.uusmonov.demoproject.service;

import com.uusmonov.demoproject.model.Joke;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class JokeService {
    private final WebClient jokesWebClient;
    private final RestTemplate restTemplate;
    private static final String COUNT_ERROR_MSG = "За один раз можно получить не более 100 шуток.";

    public void inputValidation(int count) {
        if (count > 100) {
            throw new IllegalArgumentException(COUNT_ERROR_MSG);
        }
    }

    public Mono<List<Joke>> getJokesWebClient(int count) {
        inputValidation(count);

        int batchSize = 10;
        Flux<Joke> jokesFlux = Flux.fromIterable(IntStream.range(0, count)
                        .boxed()
                        .toList())
                .buffer(batchSize)
                .concatMap(batch -> Flux.merge(batch.stream()
                        .map(i -> fetchJoke())
                        .toList()));

        return jokesFlux.collectList();
    }

    private Mono<Joke> fetchJoke() {
        return jokesWebClient.get()
                .uri("/random_joke")
                .retrieve()
                .bodyToMono(Joke.class);
    }

    public List<Joke> getJokesRestTemplate(int count) {
        inputValidation(count);

        List<CompletableFuture<Joke>> jokeFutures = new ArrayList<>();

        for (int i = 0; i < count; i += 10) {
            int batchSize = Math.min(10, count - i);
            List<CompletableFuture<Joke>> batchFutures = Stream.generate(this::fetchJokeRestTemplate)
                    .limit(batchSize)
                    .toList();

            jokeFutures.addAll(batchFutures);
        }

        try {
            return CompletableFuture.allOf(
                    jokeFutures.toArray(new CompletableFuture[0])).thenApply(
                            v -> jokeFutures.stream().map(CompletableFuture::join).collect(Collectors.toList())
            ).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private CompletableFuture<Joke> fetchJokeRestTemplate() {
        return CompletableFuture.supplyAsync(() -> restTemplate.getForEntity("https://official-joke-api.appspot.com/random_joke", Joke.class).getBody());
    }
}
