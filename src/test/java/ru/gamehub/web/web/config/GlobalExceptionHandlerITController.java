package ru.gamehub.web.web.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Validated
class GlobalExceptionHandlerITController {

    @GetMapping("/param")
    public String param(@RequestParam @NotBlank String q) { return "ok"; }

    @GetMapping("/missing")
    public String missing(@RequestParam(name = "q") String q) { return "ok"; }

    @GetMapping("/type")
    public String type(@RequestParam @Min(1) int limit) { return "ok"; }

    @PostMapping(value = "/body", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String body(@RequestBody @Validated BodyDto dto) { return "ok"; }

    @PostMapping(value = "/model", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String model(@ModelAttribute @Validated ModelDto dto) { return "ok"; }

    @GetMapping("/user-exists")
    public String userExists() { throw new ru.gamehub.web.domain.user.exception.UserAlreadyExistsException("User already exists"); }

    @GetMapping("/user-registration-error")
    public String userRegistrationError() { throw new ru.gamehub.web.domain.user.exception.UserRegistrationException("Registration failed"); }

    @GetMapping("/fail")
    public String fail() { throw new RuntimeException("boom"); }

    static class BodyDto { @NotBlank public String name; }
    static class ModelDto { @NotBlank public String name; }
}
