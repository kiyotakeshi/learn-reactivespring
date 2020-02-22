package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D")) // not executed this line
                .onErrorResume((e) -> { // this block gets executed
                    System.out.println("Exception is :" + e);
                    return Flux.just("default", "default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
//                .expectError(RuntimeException.class)
//                .verify();
                .expectNext("default", "default1")
                .verifyComplete();

        // 00:23:14.344 [main] INFO reactor.Flux.OnErrorResume.1 - onSubscribe(FluxOnErrorResume.ResumeSubscriber)
        // 00:23:14.348 [main] INFO reactor.Flux.OnErrorResume.1 - request(unbounded)
        // 00:23:14.350 [main] INFO reactor.Flux.OnErrorResume.1 - onNext(A)
        // 00:23:14.350 [main] INFO reactor.Flux.OnErrorResume.1 - onNext(B)
        // 00:23:14.350 [main] INFO reactor.Flux.OnErrorResume.1 - onNext(C)
        // Exception is :java.lang.RuntimeException: Exception Occurred
        // 00:23:14.362 [main] INFO reactor.Flux.OnErrorResume.1 - onNext(default)
        // 00:23:14.362 [main] INFO reactor.Flux.OnErrorResume.1 - onNext(default1)
        // 00:23:14.363 [main] INFO reactor.Flux.OnErrorResume.1 - onComplete()
        // Process finished with exit code 0
    }

    @Test
    public void fluxErrorHandlingOnErrorReturn() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D")) // not executed this line
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("default")
                .verifyComplete();

        // 07:42:43.484 [main] INFO reactor.Flux.OnErrorResume.1 - request(unbounded)
        // 07:42:43.485 [main] INFO reactor.Flux.OnErrorResume.1 - onNext(A)
        // 07:42:43.485 [main] INFO reactor.Flux.OnErrorResume.1 - onNext(B)
        // 07:42:43.485 [main] INFO reactor.Flux.OnErrorResume.1 - onNext(C)
        // 07:42:43.486 [main] INFO reactor.Flux.OnErrorResume.1 - onNext(default)
        // 07:42:43.487 [main] INFO reactor.Flux.OnErrorResume.1 - onComplete()
        // Process finished with exit code 0
    }

    @Test
    public void fluxErrorHandlingOnErrorMap() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D")) // not executed this line
                .onErrorMap((e) -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandlingOnErrorMapWithRetry() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D")) // not executed this line
                .onErrorMap((e) -> new CustomException(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(CustomException.class)
                .verify();
    }
    @Test
    public void fluxErrorHandlingOnErrorMapWithRetryBackoff() {

        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D")) // not executed this line
                .onErrorMap((e) -> new CustomException(e))
                .retryBackoff(2, Duration.ofSeconds(3));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectNext("A", "B", "C")
                .expectError(IllegalStateException.class)
                .verify();
    }
}
