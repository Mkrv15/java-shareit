package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void userBuilder_shouldCreateUser() {
        User user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .build();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void userNoArgsConstructor_shouldCreateEmptyUser() {
        User user = new User();
        assertThat(user).isNotNull();
    }

    @Test
    void userAllArgsConstructor_shouldCreateUser() {
        User user = new User(1L, "Test", "test@example.com");
        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void userSettersAndGetters_shouldWork() {
        User user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@example.com");

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void userEquals_shouldCompareById() {
        User user1 = new User();
        user1.setId(1L);

        User user2 = new User();
        user2.setId(1L);

        User user3 = new User();
        user3.setId(2L);

        assertThat(user1).isEqualTo(user2);
        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}
