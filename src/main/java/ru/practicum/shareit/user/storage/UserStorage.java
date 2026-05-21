package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.madel.User;

import java.util.Collection;

public interface UserStorage {
    User get(Long id);

    Collection<User> getAll();

    User add(User user);

    User update(User user);

    Boolean delete(Long id);
}
