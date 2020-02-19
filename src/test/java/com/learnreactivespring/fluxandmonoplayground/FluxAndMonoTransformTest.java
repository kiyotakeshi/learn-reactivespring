package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase()) // ADAM, ANNA, JACK, JENNY
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();

    }

    @Test
    public void transformUsingMapLength() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length()) // ADAM, ANNA, JACK, JENNY
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMapLengthRepeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(s -> s.length()) // ADAM, ANNA, JACK, JENNY
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingFilterMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4) // jenny
                .map(s -> s.toUpperCase()) // JENNY
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // A, B, C, E, F
                .flatMap(s -> {

                    return Flux.fromIterable(convertToList(s)); // A -> List[A, newValue] , B -> List[B, newValue]
                })
                .log(); // db or external service call that returns a flux -> s -> Flux<String>

        StepVerifier.create(stringFlux) // it takes 6 seconds
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

}
