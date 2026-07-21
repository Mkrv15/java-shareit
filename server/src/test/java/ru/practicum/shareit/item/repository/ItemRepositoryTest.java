package ru.practicum.shareit.item.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase
class ItemRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void shouldSearchAvailableItemsByTextIgnoreCase() {

        User ownerFirst = new User();
        ownerFirst.setEmail("ownerFirst@email");
        ownerFirst.setName("ownerFirst");
        entityManager.persist(ownerFirst);

        User ownerSecond = new User();
        ownerSecond.setEmail("ownerSecond@email");
        ownerSecond.setName("ownerSecond");
        entityManager.persist(ownerSecond);

        Item itemFirst = new Item();
        itemFirst.setUserId(ownerFirst.getId());
        itemFirst.setName("ITEMFirst");
        itemFirst.setDescription("ITEMFirstDescription");
        itemFirst.setAvailable(true);
        entityManager.persist(itemFirst);

        Item itemSecond = new Item();
        itemSecond.setUserId(ownerSecond.getId());
        itemSecond.setName("Second");
        itemSecond.setDescription("itemSecondDescription");
        itemSecond.setAvailable(true);
        entityManager.persist(itemSecond);

        Item thirdItem = new Item();
        thirdItem.setUserId(ownerFirst.getId());
        thirdItem.setName("Third");
        thirdItem.setDescription("Description");
        thirdItem.setAvailable(true);
        entityManager.persist(thirdItem);

        Item fourthItem = new Item();
        fourthItem.setUserId(ownerSecond.getId());
        fourthItem.setName("FourthItem");
        fourthItem.setDescription("Description");
        fourthItem.setAvailable(true);
        entityManager.persist(fourthItem);

        Item fifthItem = new Item();
        fifthItem.setUserId(ownerFirst.getId());
        fifthItem.setName("FifthItem");
        fifthItem.setDescription("FifthDescription");
        fifthItem.setAvailable(false);
        entityManager.persist(fifthItem);

        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        List<Item> result = itemRepository.findByNameOrDescriptionLike("item", pageable);

        assertThat(result)
                .isNotNull();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(Item::getName)
                .containsExactly("ITEMFirst", "Second", "FourthItem");
    }
}
