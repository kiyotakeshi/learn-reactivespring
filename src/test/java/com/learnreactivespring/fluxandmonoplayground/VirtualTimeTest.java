package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    @Test
    public void testWithVirtualTime() {

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0l, 1l, 2l)
                .verifyComplete();

        // 00:25:35.504 [parallel-1] INFO reactor.Flux.Take.1 - onNext(0)
        // 00:25:36.502 [parallel-1] INFO reactor.Flux.Take.1 - onNext(1)
        // 00:25:37.498 [parallel-1] INFO reactor.Flux.Take.1 - onNext(2)
        // 00:25:37.499 [parallel-1] INFO reactor.Flux.Take.1 - onComplete()
    }

    @Test
    public void testingWithVirtualTime() {

        VirtualTimeScheduler.getOrSet();

        Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
                .take(3);

        StepVerifier.withVirtualTime(() -> longFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0l, 1l, 2l)
                .verifyComplete();

        // 00:29:20.194 [main] INFO reactor.Flux.Take.1 - onSubscribe(FluxTake.TakeSubscriber)
        // 00:29:20.198 [main] INFO reactor.Flux.Take.1 - request(unbounded)
        // 00:29:20.200 [main] INFO reactor.Flux.Take.1 - onNext(0)
        // 00:29:20.200 [main] INFO reactor.Flux.Take.1 - onNext(1)
        // 00:29:20.201 [main] INFO reactor.Flux.Take.1 - onNext(2)
        // 00:29:20.201 [main] INFO reactor.Flux.Take.1 - onComplete()
    }

    @Test
    public void combineUsingConcatWithDelay() {

        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.withVirtualTime(() -> mergedFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))
                .expectNextCount(6)
                .verifyComplete();

        // 00:34:56.007 [main] INFO reactor.Flux.ConcatArray.1 - onSubscribe(FluxConcatArray.ConcatArraySubscriber)
        // 00:34:56.014 [main] INFO reactor.Flux.ConcatArray.1 - request(unbounded)
        // 00:34:56.090 [main] INFO reactor.Flux.ConcatArray.1 - onNext(A)
        // 00:34:56.090 [main] INFO reactor.Flux.ConcatArray.1 - onNext(B)
        // 00:34:56.091 [main] INFO reactor.Flux.ConcatArray.1 - onNext(C)
        // 00:34:56.091 [main] INFO reactor.Flux.ConcatArray.1 - onNext(D)
        // 00:34:56.091 [main] INFO reactor.Flux.ConcatArray.1 - onNext(E)
        // 00:34:56.091 [main] INFO reactor.Flux.ConcatArray.1 - onNext(F)
        // 00:34:56.091 [main] INFO reactor.Flux.ConcatArray.1 - onComplete()

        // Not use virtual time scheduler
        // StepVerifier.create(mergedFlux.log())
        //         .expectSubscription()
        //         .expectNext("A", "B", "C", "D", "E", "F")
        //         .verifyComplete();

        // in order and take 6 seconds
        // 23:51:36.447 [parallel-1] INFO reactor.Flux.ConcatArray.1 - onNext(A)
        // 23:51:37.449 [parallel-2] INFO reactor.Flux.ConcatArray.1 - onNext(B)
        // 23:51:38.454 [parallel-3] INFO reactor.Flux.ConcatArray.1 - onNext(C)
        // 23:51:39.457 [parallel-4] INFO reactor.Flux.ConcatArray.1 - onNext(D)
        // 23:51:40.460 [parallel-1] INFO reactor.Flux.ConcatArray.1 - onNext(E)
        // 23:51:41.465 [parallel-2] INFO reactor.Flux.ConcatArray.1 - onNext(F)
        // 23:51:41.466 [parallel-2] INFO reactor.Flux.ConcatArray.1 - onComplete()
    }
}
