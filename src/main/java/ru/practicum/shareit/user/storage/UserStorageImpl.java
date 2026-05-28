package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.madel.User;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class UserStorageImpl  implements UserStorage {
    private final AtomicLong atomicLong;
    private final Map<Long, User> users;
    private final Set<String> emails;

    public UserStorageImpl() {
        this.emails = new CopyOnWriteArraySet<>();
        this.users = new ConcurrentHashMap<>();
        this.atomicLong = new AtomicLong();
    }

    @Override
    public User get(Long id) {
        validateId(id);
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
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
        User oldUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isEmpty()) {
            oldUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()
                && !oldUser.getEmail().equalsIgnoreCase(user.getEmail())) {
            validateEmail(user);
            oldUser.setEmail(user.getEmail());
        }
        users.put(oldUser.getId(), oldUser);
        return users.get(oldUser.getId());
    }

    @Override
    public Boolean delete(Long id) {
        User user = users.remove(id);
        emails.remove(user.getEmail());
        return !users.containsKey(id);
    }

    private void validateId(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не зарегистрирован!");
        }
    }

    private void validateEmail(User user) {
        if (!emails.add(user.getEmail())) {
            throw new EmailException("Пользователь с таким адресом Эл. почты " +
                    user.getEmail() + " уже существует!");
        }
    }
}
