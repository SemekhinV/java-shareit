package ru.practicum.shareit.item.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
public class RepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    ItemRepository itemRepository;
    User user = User.builder()
            .id(null)
            .name("Vas")
            .email("vas@mail.com")
            .build();
    Item item1;
    Item item2;
    Item item3;

    @BeforeEach
    void beforeEach() {

        entityManager.persist(user);

        item1 = Item.builder()
                .id(null)
                .name("item1")
                .description("test desc")
                .available(true)
                .owner(user)
                .request(null)
                .build();

        item2 = Item.builder()
                .id(null)
                .name("item2")
                .description("anti desc")
                .available(true)
                .owner(user)
                .request(null)
                .build();

        item3 = Item.builder()
                .id(null)
                .name("item3")
                .description("test desc")
                .available(true)
                .owner(user)
                .request(null)
                .build();

        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
    }

    @Test
    void oneItemTest() {

        List<Item> items = itemRepository.searchForItems("desc");

        assertThat(items, containsInAnyOrder(item1, item2, item3));
        assertThat(items, hasSize(3));
    }

    @Test
    void twoItemTest() {

        List<Item> items = itemRepository.searchForItems("test");

        assertThat(items, containsInAnyOrder(item1, item3));
        assertThat(items, hasSize(2));
    }

    @Test
    void threeItemTest() {

        List<Item> items = itemRepository.searchForItems("123123");

        assertThat(items, empty());
    }
}
