package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge() {

        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMergeWithDelay() {

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.merge(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                // .expectNext("A", "B", "C", "D", "E", "F") // fail
                .expectNextCount(6)
                .verifyComplete();

        // 23:46:56.574 [parallel-1] INFO reactor.Flux.Merge.1 - onNext(A)
        // 23:46:56.576 [parallel-2] INFO reactor.Flux.Merge.1 - onNext(D)
        // 23:46:57.578 [parallel-3] INFO reactor.Flux.Merge.1 - onNext(B)
        // 23:46:57.579 [parallel-3] INFO reactor.Flux.Merge.1 - onNext(E)
        // 23:46:58.580 [parallel-2] INFO reactor.Flux.Merge.1 - onNext(C)
        // 23:46:58.580 [parallel-2] INFO reactor.Flux.Merge.1 - onNext(F)
        // 23:46:58.581 [parallel-2] INFO reactor.Flux.Merge.1 - onComplete()

    }

    @Test
    public void combineUsingConcat() {

        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcatWithDelay() {

        Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));

        Flux<String> mergedFlux = Flux.concat(flux1, flux2);

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();

        // in order and take 6 seconds
        // 23:51:36.447 [parallel-1] INFO reactor.Flux.ConcatArray.1 - onNext(A)
        // 23:51:37.449 [parallel-2] INFO reactor.Flux.ConcatArray.1 - onNext(B)
        // 23:51:38.454 [parallel-3] INFO reactor.Flux.ConcatArray.1 - onNext(C)
        // 23:51:39.457 [parallel-4] INFO reactor.Flux.ConcatArray.1 - onNext(D)
        // 23:51:40.460 [parallel-1] INFO reactor.Flux.ConcatArray.1 - onNext(E)
        // 23:51:41.465 [parallel-2] INFO reactor.Flux.ConcatArray.1 - onNext(F)
        // 23:51:41.466 [parallel-2] INFO reactor.Flux.ConcatArray.1 - onComplete()
    }

    @Test
    public void combineUsingZip() {

        Flux<String> flux1 = Flux.just("A", "B", "C");
        Flux<String> flux2 = Flux.just("D", "E", "F");

        Flux<String> mergedFlux = Flux.zip(flux1, flux2, (t1, t2) -> {
            return t1.concat(t2); // AD, BE, CF
        }); // A,D : B,E : C,F

        StepVerifier.create(mergedFlux.log())
                .expectSubscription()
                .expectNext("AD", "BE", "CF")
                .verifyComplete();

        // 00:00:56.591 [main] INFO reactor.Flux.Zip.1 - onSubscribe(FluxZip.ZipCoordinator)
        // 00:00:56.596 [main] INFO reactor.Flux.Zip.1 - request(unbounded)
        // 00:00:56.598 [main] INFO reactor.Flux.Zip.1 - onNext(AD)
        // 00:00:56.598 [main] INFO reactor.Flux.Zip.1 - onNext(BE)
        // 00:00:56.598 [main] INFO reactor.Flux.Zip.1 - onNext(CF)
        // 00:00:56.599 [main] INFO reactor.Flux.Zip.1 - onComplete()
    }
}
