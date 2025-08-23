package org.assistantAPI.dto;

public record CreateUserRequest(String name, String email, String password, String phone, String address) {}