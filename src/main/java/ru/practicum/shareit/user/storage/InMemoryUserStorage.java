package ru.practicum.shareit.user.storage;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Component
@Getter
public class InMemoryUserStorage {
    private final Map<Long, User> userStorageMap = new HashMap<>();
    private final List<String> emailList = new ArrayList<>();
    private long userId = 1;

    private long setId() {
        return userId++;
    }

    public User saveUser(User user) {
        checkEmail(user);
        if (user.getId() == 0) {
            user.setId(setId());
        }
        emailList.add(user.getEmail());
        userStorageMap.put(user.getId(), user);
        return user;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userStorageMap.values());
    }

    public Optional<User> getUserById(long id) {
        return userStorageMap.containsKey(id) ? Optional.of(userStorageMap.get(id)) : Optional.empty();
    }

    public User deleteUser(long id) {
        User user = getUserById(id).orElseThrow(() -> new NotFoundException(String.format("User %s not found", id)));
        emailList.remove(user.getEmail());
        return userStorageMap.remove(id);
    }

    private void checkEmail(User user) {
        if (emailList.contains(user.getEmail())) {
            throw new ConflictException("email already used");
        }
    }
}