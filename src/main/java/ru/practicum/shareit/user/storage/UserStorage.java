package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.madel.User;

import java.util.List;

public interface UserStorage {
    User get(Long id);

    List<User> getAll();

    User add(User user);

    User update(User user);

    Boolean delete(Long id);
}
