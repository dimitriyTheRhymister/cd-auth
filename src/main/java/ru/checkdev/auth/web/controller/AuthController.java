package ru.checkdev.auth.web.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.checkdev.auth.domain.Profile;
import ru.checkdev.auth.dto.ProfileTgDTO;
import ru.checkdev.auth.service.PersonService;
import ru.checkdev.auth.util.CircuitBreaker;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;

@Tag(name = "AuthController", description = "Authentication REST API")
@RestController
public class AuthController {
    private final PersonService persons;
    private final CircuitBreaker circuitBreaker;
    private final String ping = "{}";

    @Autowired
    public AuthController(final PersonService persons, CircuitBreaker circuitBreaker) {
        this.persons = persons;
        this.circuitBreaker = circuitBreaker;
    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @GetMapping("/ping")
    public String ping() {
        return this.ping;
    }

    @GetMapping("/auth/activated/{key}")
    public Object activated(@PathVariable String key) {
        Boolean success = circuitBreaker.exec(
                () -> persons.activated(key),
                false // значение по умолчанию
        );
        if (success) {
            return new Object() {
                public boolean getSuccess() {
                    return true;
                }
            };
        } else {
            return new Object() {
                public String getError() {
                    return "Notify has already activated";
                }
            };
        }
    }

    @PostMapping("/registration")
    public Object registration(@RequestBody Profile profile) {
        Optional<Profile> result = circuitBreaker.exec(
                () -> persons.reg(profile),
                Optional.empty()
        );
        if (result.isPresent()) {
            return new ProfileTgDTO(result.get().getId(),
                    result.get().getUsername(),
                    result.get().getEmail());
        } else {
            return new Object() {
                public String getError() {
                    return String.format("Пользователь с почтой %s уже существует.", profile.getEmail());
                }
            };
        }
    }

    @PostMapping("/forgot")
    public Object forgot(@RequestBody Profile profile) {
        Optional<Profile> result = circuitBreaker.exec(
                () -> persons.forgot(profile),
                Optional.empty()
        );
        if (result.isPresent()) {
            return new Object() {
                public String getOk() {
                    return "ok";
                }
            };
        } else {
            return new Object() {
                public String getError() {
                    return "E-mail не найден.";
                }
            };
        }
    }

    @PostMapping("/forgotTg")
    public Object forgotTg(@RequestBody Profile profile) {
        Optional<Profile> result = circuitBreaker.exec(
                () -> persons.forgotTg(profile),
                Optional.empty()
        );
        if (result.isPresent()) {
            return new Object() {
                public String getOk() {
                    return "ok";
                }
            };
        } else {
            return new Object() {
                public String getError() {
                    return "E-mail не найден.";
                }
            };
        }
    }

    @GetMapping("/revoke")
    @ResponseStatus(HttpStatus.OK)
    public void logout(HttpServletRequest request) {

    }

    @GetMapping("/test/error")
    public String testError() {
        return circuitBreaker.exec(() -> {
            throw new RuntimeException("Тестовая ошибка");
        }, "fallback");
    }
}