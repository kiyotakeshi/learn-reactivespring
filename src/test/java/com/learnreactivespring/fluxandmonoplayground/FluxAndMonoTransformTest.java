package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

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

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();

        // 01:19:57.439 [main] INFO reactor.Flux.FlatMap.1 - onNext(A)
        // 01:19:57.439 [main] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:19:58.440 [main] INFO reactor.Flux.FlatMap.1 - onNext(B)
        // 01:19:58.441 [main] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:19:59.445 [main] INFO reactor.Flux.FlatMap.1 - onNext(C)
        // 01:19:59.446 [main] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:20:00.449 [main] INFO reactor.Flux.FlatMap.1 - onNext(D)
        // 01:20:00.449 [main] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:20:01.454 [main] INFO reactor.Flux.FlatMap.1 - onNext(E)
        // 01:20:01.455 [main] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:20:02.455 [main] INFO reactor.Flux.FlatMap.1 - onNext(F)
        // 01:20:02.456 [main] INFO reactor.Flux.FlatMap.1 - onNext(newValue)

        // 01:20:02.456 [main] INFO reactor.Flux.FlatMap.1 - onComplete()
    }

    private List<String> convertToList(String s) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

    @Test
    public void transformUsingFlatMapUsingParallel() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                .window(2) // Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                .flatMap((s) -> s.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();

        // 01:15:41.516 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:15:41.516 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(C)
        // 01:15:41.516 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(A)
        // 01:15:41.516 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:15:41.516 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(E)
        // 01:15:41.516 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(newValue)

        // 01:15:42.515 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(D)
        // 01:15:42.515 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:15:42.516 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(F)
        // 01:15:42.516 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:15:42.519 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(B)
        // 01:15:42.519 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(newValue)

        // 01:15:42.520 [parallel-1] INFO reactor.Flux.FlatMap.1 - onComplete()
    }

    @Test
    public void transformUsingFlatMapParallelMaintainOrder() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // Flux<String>
                .window(2) // Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                /* .concatMap((s) -> s.map(this::convertToList).subscribeOn(parallel()))  */ // it tooks 6 seconds
                .flatMapSequential((s) -> s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s)) // Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();

        // 01:32:37.583 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(A)
        // 01:32:37.583 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:32:38.585 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(B)
        // 01:32:38.585 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:32:38.585 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(C)
        // 01:32:38.585 [parallel-1] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:32:38.586 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(D)
        // 01:32:38.586 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:32:38.586 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(E)
        // 01:32:38.586 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(newValue)
        // 01:32:38.586 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(F)
        // 01:32:38.586 [parallel-2] INFO reactor.Flux.FlatMap.1 - onNext(newValue)

        // 01:32:38.587 [parallel-2] INFO reactor.Flux.FlatMap.1 - onComplete()
    }
}
