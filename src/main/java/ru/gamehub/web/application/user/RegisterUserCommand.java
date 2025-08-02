package ru.gamehub.web.application.user;

public record RegisterUserCommand(String email, String name, String password) {}
