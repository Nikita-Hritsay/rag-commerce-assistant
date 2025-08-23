package org.assistantAPI.services;

import org.assistantAPI.domain.User;
import org.assistantAPI.dto.CreateUserRequest;

public interface UserService {
    public User createUser(CreateUserRequest createUserRequest);
    public User findById(Long id);
}
