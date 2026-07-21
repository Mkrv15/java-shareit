package ru.practicum.shareit.user;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserPatchDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserClient userClient;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable @NonNull Long userId) {
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid @NotNull UserRequestDto userDto) {
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> patchUser(@RequestBody @Valid @NotNull UserPatchDto userDto,
                                            @PathVariable("userId") @NotNull Long userId) {
        return userClient.patchUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @NotNull Long userId) {
        return userClient.deleteUser(userId);
    }
}
