package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure() {

        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe((element) -> System.out.println("element is : " + element)
                , (e) -> System.err.println("Exception is : " + e)
                , () -> System.out.println("Done") // this doesn't execute
                , (subscription -> subscription.request(2)));
    }

    @Test
    public void backPressureCancel() {

        Flux<Integer> finiteFlux = Flux.range(1, 10).log();

        finiteFlux.subscribe((element) -> System.out.println("element is : " + element)
                , (e) -> System.err.println("Exception is : " + e)
                , () -> System.out.println("Done") // this doesn't execute
                , (subscription -> subscription.cancel()));
    }
}
