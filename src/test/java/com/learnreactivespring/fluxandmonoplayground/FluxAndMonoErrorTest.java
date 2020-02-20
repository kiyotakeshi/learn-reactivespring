package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

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
}
