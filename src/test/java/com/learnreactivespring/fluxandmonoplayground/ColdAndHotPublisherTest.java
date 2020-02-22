package com.learnreactivespring.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdAndHotPublisherTest {

    @Test
    public void coldPublisherTest() throws InterruptedException {

        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe(s -> System.out.println("Subscriber 1 :" + s)); // emits the value from beginning

        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2 :" + s)); // emits the value from beginning

        Thread.sleep(4000);

        // Subscriber 1 :A
        // Subscriber 1 :B
        // Subscriber 2 :A
        // Subscriber 1 :C
        // Subscriber 2 :B
        // Subscriber 1 :D
        // Subscriber 2 :C
        // Subscriber 1 :E
        // Subscriber 2 :D
        // Process finished with exit code 0
    }

    @Test
    public void hotPublisherTest() throws InterruptedException {


        Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();

        connectableFlux.subscribe((s) -> System.out.println("Subscriber 1: " + s));
        Thread.sleep(3000);

        connectableFlux.subscribe((s) -> System.out.println("Subscriber 2: " + s)); // does not emit the values from beginning
        Thread.sleep(4000);

        // Subscriber 1: A
        // Subscriber 1: B
        // Subscriber 1: C
        // Subscriber 2: C
        // Subscriber 1: D
        // Subscriber 2: D
        // Subscriber 1: E
        // Subscriber 2: E
        // Subscriber 1: F
        // Subscriber 2: F
    }
}