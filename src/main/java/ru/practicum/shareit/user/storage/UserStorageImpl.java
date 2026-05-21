package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.madel.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UserStorageImpl  implements UserStorage {
    private final AtomicLong atomicLong;
    private final Map<Long, User> users;

    public UserStorageImpl() {
        this.users = new HashMap<>();
        this.atomicLong = new AtomicLong();
    }

    @Override
    public User get(Long id) {
        validateId(id);
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User add(User user) {
        validateEmail(user);
        user.setId(atomicLong.incrementAndGet());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User update(User user) {
        validateId(user.getId());
        validateEmail(user);
        User oldUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isEmpty()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            oldUser.setEmail(user.getEmail());
        }
        users.put(oldUser.getId(), oldUser);
        return users.get(oldUser.getId());
    }

    @Override
    public Boolean delete(Long id) {
        users.remove(id);
        return !users.containsKey(id);
    }

    private void validateId(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не зарегистрирован!");
        }
    }

    private void validateEmail(User user) {
        if (users.values().stream()
                .anyMatch(
                        stored -> stored.getEmail().equalsIgnoreCase(user.getEmail())
                                && !stored.getId().equals(user.getId())
                )) {
            throw new EmailException("Пользователь с таким адресом Эл. почты " +
                    user.getEmail() + " уже существует!");
        }
    }
}
