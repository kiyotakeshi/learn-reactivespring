package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0)
            , new Item(null, "LG TV", 420.0)
            , new Item(null, "Apple TV", 299.99)
            , new Item(null, "Beats Headphones", 499.99)
            , new Item("ABC", "Bose Headphones", 499.99));

    @Before
    public void setUp() {

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserterd Item is :" + item);
                }))
                .blockLast();
    }

    @Test
    public void getAllItems() {

        StepVerifier.create(itemReactiveRepository.findAll()) // 0
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    // Inserterd Item is :Item(id=5e52588a19e7203fc06d773f, description=LG TV, price=420.0)
    // Inserterd Item is :Item(id=5e52588a19e7203fc06d7740, description=Apple TV, price=299.99)
    // Inserterd Item is :Item(id=5e52588a19e7203fc06d7741, description=Beats Headphones, price=499.99)
    // Inserterd Item is :Item(id=5e52588a19e7203fc06d773e, description=Samsung TV, price=400.0)
    // 2020-02-23 19:48:42.756  INFO 17294 --- [extShutdownHook] org.mongodb.driver.connection            : Closed connection [connectionId{localValue:5, serverValue:5}] to localhost:27017 because the pool has been closed.
    // 2020-02-23 19:48:42.756  INFO 17294 --- [extShutdownHook] org.mongodb.driver.connection            : Closed connection [connectionId{localValue:6, serverValue:6}] to localhost:27017 because the pool has been closed.
    // 2020-02-23 19:48:42.756  INFO 17294 --- [extShutdownHook] org.mongodb.driver.connection            : Closed connection [connectionId{localValue:3, serverValue:3}] to localhost:27017 because the pool has been closed.
    // 2020-02-23 19:48:42.757  INFO 17294 --- [extShutdownHook] org.mongodb.driver.connection            : Closed connection [connectionId{localValue:4, serverValue:4}] to localhost:27017 because the pool has been closed.

    @Test
    public void getItemByID() {

        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("Bose Headphones")))
                .verifyComplete();
    }
}