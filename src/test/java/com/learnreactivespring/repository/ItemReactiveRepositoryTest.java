package com.learnreactivespring.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Test
    public void getAllItems(){

        StepVerifier.create(itemReactiveRepository.findAll()) // 0
                .expectSubscription()
                .expectNextCount(0)
                .verifyComplete();
    }

}
