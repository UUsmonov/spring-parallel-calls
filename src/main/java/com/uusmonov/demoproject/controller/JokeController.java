package com.uusmonov.demoproject.controller;

import com.uusmonov.demoproject.model.Joke;
import com.uusmonov.demoproject.service.JokeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jokes")
public class JokeController {
    private final JokeService jokeService;

    @GetMapping("/webclient")
    public Mono<List<Joke>> getJokesWebClient(@RequestParam(defaultValue = "5") int count) {
        return jokeService.getJokesWebClient(count);
    }

    @GetMapping("/resttemplate")
    public List<Joke> getJokesRestTemplate(@RequestParam(defaultValue = "5") int count) {
        return jokeService.getJokesRestTemplate(count);
    }
}
