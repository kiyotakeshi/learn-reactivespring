package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {

        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)) // starts from 0 -> ...
                .log();

        infiniteFlux.subscribe((element) -> System.out.println("Value is :" + element));

        Thread.sleep(2000);
        // one of the behavior of reactive progoraming basically,Asyncronus nonblocking
        // 19:04:10.303 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(0)
        // Value is :0
        //         19:04:10.502 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(1)
        // Value is :1
        //         19:04:10.702 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(2)
        // Value is :2
        //         19:04:10.904 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(3)
        // Value is :3
        //         19:04:11.102 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(4)
        // Value is :4
        //         19:04:11.302 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(5)
        // Value is :5
        //         19:04:11.502 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(6)
        // Value is :6
        //         19:04:11.702 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(7)
        // Value is :7
        //         19:04:11.905 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(8)
        // Value is :8
        //         19:04:12.106 [parallel-1] INFO reactor.Flux.Interval.1 - onNext(9)
        // Value is :9
    }


    @Test
    public void infiniteSequenceTest() throws InterruptedException {

        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L, 1L, 2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMapWithDelay() throws InterruptedException {

        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> l.intValue())
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0, 1, 2)
                .verifyComplete();
    }
}
