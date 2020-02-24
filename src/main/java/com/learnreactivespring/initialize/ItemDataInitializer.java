package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {

        initialDataSetUp();
    }

    public List<Item> data() {

        return Arrays.asList(new Item(null, "Samsung TV", 399.99)
                , new Item(null, "LG TV", 329.99)
                , new Item(null, "Apple Watch TV", 349.99)
                , new Item("ABC", "Beats HeadPhones ", 199.99));
    }

    private void initialDataSetUp() {

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe((item -> {
                    System.out.println("Item inserted from CommandLineRunner : " + item);
                }));
    }
    // Item inserted from CommandLineRunner : Item(id=5e53606e97749342247a7cd4, description=Samsung TV, price=399.99)
    // Item inserted from CommandLineRunner : Item(id=5e53606e97749342247a7cd6, description=Apple Watch TV, price=349.99)
    // Item inserted from CommandLineRunner : Item(id=5e53606e97749342247a7cd5, description=LG TV, price=329.99)
    // Item inserted from CommandLineRunner : Item(id=ABC, description=Beats HeadPhones , price=199.99)
    // Also access http://localhost:8080/v1/items
}
