package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ItemTest {

    @Test
    void itemBuilder_shouldCreateItem() {
        Item item = Item.builder()
                .id(1L)
                .userId(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .requestId(1L)
                .build();

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getUserId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Test Item");
        assertThat(item.getDescription()).isEqualTo("Test Description");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getRequestId()).isEqualTo(1L);
    }

    @Test
    void itemNoArgsConstructor_shouldCreateEmptyItem() {
        Item item = new Item();

        assertThat(item).isNotNull();
    }

    @Test
    void itemAllArgsConstructor_shouldCreateItem() {
        Item item = new Item(1L, 1L, "Test", "Desc", true, 1L);

        assertThat(item).isNotNull();
        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getUserId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Test");
        assertThat(item.getDescription()).isEqualTo("Desc");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getRequestId()).isEqualTo(1L);
    }

    @Test
    void itemSettersAndGetters_shouldWork() {
        Item item = new Item();

        item.setId(1L);
        item.setUserId(1L);
        item.setName("Test");
        item.setDescription("Desc");
        item.setAvailable(true);
        item.setRequestId(1L);

        assertThat(item.getId()).isEqualTo(1L);
        assertThat(item.getUserId()).isEqualTo(1L);
        assertThat(item.getName()).isEqualTo("Test");
        assertThat(item.getDescription()).isEqualTo("Desc");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getRequestId()).isEqualTo(1L);
    }

    @Test
    void itemEquals_shouldCompareById() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        Item item3 = new Item();
        item3.setId(2L);

        assertThat(item1).isEqualTo(item2);
        assertThat(item1).isNotEqualTo(item3);
        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void itemEquals_withNull_shouldReturnFalse() {
        Item item = new Item();
        item.setId(1L);

        assertThat(item.equals(null)).isFalse();
    }

    @Test
    void itemEquals_withDifferentClass_shouldReturnFalse() {
        Item item = new Item();
        item.setId(1L);
        String differentObject = "string";

        assertThat(item.equals(differentObject)).isFalse();
    }

    @Test
    void itemEquals_withSameObject_shouldReturnTrue() {
        Item item = new Item();
        item.setId(1L);

        assertThat(item.equals(item)).isTrue();
    }

    @Test
    void itemHashCode_shouldBeConsistent() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        assertThat(item1.hashCode()).isEqualTo(item2.hashCode());
    }

    @Test
    void itemHashCode_shouldBeDifferentForDifferentIds() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(2L);

        assertThat(item1.hashCode()).isNotEqualTo(item2.hashCode());
    }
}