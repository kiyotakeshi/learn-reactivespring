package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
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

    @Test
    public void findItemByDescription() {

        StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones").log("findItemByDescription : "))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

        // 2020-02-24 08:59:52.360  INFO 17786 --- [           main] findItemByDescription :                  : onSubscribe(FluxOnErrorResume.ResumeSubscriber)
        // 2020-02-24 08:59:52.364  INFO 17786 --- [           main] findItemByDescription :                  : request(unbounded)
        // 2020-02-24 08:59:52.404  INFO 17786 --- [ntLoopGroup-2-6] findItemByDescription :                  : onNext(Item(id=ABC, description=Bose Headphones, price=499.99))
        // 2020-02-24 08:59:52.405  INFO 17786 --- [ntLoopGroup-2-6] findItemByDescription :                  : onComplete()
    }

    @Test
    public void saveItem() {

        Item item = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("saveItem : "))
                .expectSubscription()
                .expectNextMatches(item1 -> (item1.getId() != null && item1.getDescription().equals("Google Home Mini")))
                .verifyComplete();
        // 2020-02-24 10:18:44.277  INFO 17940 --- [           main] saveItem :                               : | onSubscribe([Fuseable] MonoFlatMap.FlatMapMain)
        // 2020-02-24 10:18:44.279  INFO 17940 --- [           main] saveItem :                               : | request(unbounded)
        // 2020-02-24 10:18:44.291  INFO 17940 --- [ntLoopGroup-2-6] saveItem :                               : | onNext(Item(id=5e5324744fc97d3351a7d4c9, description=Google Home Mini, price=30.0))
        // 2020-02-24 10:18:44.292  INFO 17940 --- [ntLoopGroup-2-6] saveItem :                               : | onComplete()
    }

    @Test
    public void updateItem() {

        double newPrice = 520.00;

        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice); // setting the new price
                    return item;

                })
                .flatMap(item -> {
                    return itemReactiveRepository.save(item); // saving the item with the new price
                });

        StepVerifier.create(updatedItem.log("updated item : "))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520.00)
                .verifyComplete();
        // 2020-02-24 10:42:34.515  INFO 1161 --- [           main] updated item :                           : onSubscribe(FluxFlatMap.FlatMapMain)
        // 2020-02-24 10:42:34.519  INFO 1161 --- [           main] updated item :                           : request(unbounded)
        // 2020-02-24 10:42:34.560  INFO 1161 --- [ntLoopGroup-2-6] updated item :                           : onNext(Item(id=5e532a0a1693ca16ac4294d8, description=LG TV, price=520.0))
        // 2020-02-24 10:42:34.561  INFO 1161 --- [ntLoopGroup-2-6] updated item :                           : onComplete()
    }

    @Test
    public void deleteItemById() {

        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC") // Mono<Item>
                .map(Item::getId) // get Id -> Transform one type to another type
                .flatMap((id) -> {
                    return itemReactiveRepository.deleteById(id);
                });

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List : "))
                .expectNextCount(4)
                .verifyComplete();
        // 2020-02-24 10:49:53.825  INFO 1301 --- [           main] The new Item List :                      : onSubscribe(FluxOnErrorResume.ResumeSubscriber)
        // 2020-02-24 10:49:53.826  INFO 1301 --- [           main] The new Item List :                      : request(unbounded)
        // 2020-02-24 10:49:53.834  INFO 1301 --- [ntLoopGroup-2-6] The new Item List :                      : onNext(Item(id=5e532bc15268397d928bc94b, description=Samsung TV, price=400.0))
        // 2020-02-24 10:49:53.834  INFO 1301 --- [ntLoopGroup-2-6] The new Item List :                      : onNext(Item(id=5e532bc15268397d928bc94c, description=LG TV, price=420.0))
        // 2020-02-24 10:49:53.835  INFO 1301 --- [ntLoopGroup-2-6] The new Item List :                      : onNext(Item(id=5e532bc15268397d928bc94d, description=Apple TV, price=299.99))
        // 2020-02-24 10:49:53.836  INFO 1301 --- [ntLoopGroup-2-6] The new Item List :                      : onNext(Item(id=5e532bc15268397d928bc94e, description=Beats Headphones, price=499.99))
        // 2020-02-24 10:49:53.836  INFO 1301 --- [ntLoopGroup-2-6] The new Item List :                      : onComplete()
    }

    @Test
    public void deleteItemB() {

        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV")
                .flatMap((item -> {
                    return itemReactiveRepository.delete(item);
                }));

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
        // 2020-02-24 10:55:30.339  INFO 1327 --- [           main] The new Item List :                      : onSubscribe(FluxOnErrorResume.ResumeSubscriber)
        // 2020-02-24 10:55:30.340  INFO 1327 --- [           main] The new Item List :                      : request(unbounded)
        // 2020-02-24 10:55:30.345  INFO 1327 --- [ntLoopGroup-2-6] The new Item List :                      : onNext(Item(id=5e532d128bbd327d9a36d564, description=Samsung TV, price=400.0))
        // 2020-02-24 10:55:30.345  INFO 1327 --- [ntLoopGroup-2-6] The new Item List :                      : onNext(Item(id=5e532d128bbd327d9a36d566, description=Apple TV, price=299.99))
        // 2020-02-24 10:55:30.346  INFO 1327 --- [ntLoopGroup-2-6] The new Item List :                      : onNext(Item(id=5e532d128bbd327d9a36d567, description=Beats Headphones, price=499.99))
        // 2020-02-24 10:55:30.346  INFO 1327 --- [ntLoopGroup-2-6] The new Item List :                      : onNext(Item(id=ABC, description=Bose Headphones, price=499.99))
        // 2020-02-24 10:55:30.346  INFO 1327 --- [ntLoopGroup-2-6] The new Item List :                      : onComplete()
    }


}