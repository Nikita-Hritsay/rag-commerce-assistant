package org.assistantAPI.services.impl;

import lombok.AllArgsConstructor;
import org.assistantAPI.domain.Authority;
import org.assistantAPI.domain.User;
import org.assistantAPI.dto.CreateUserRequest;
import org.assistantAPI.repository.AuthorityRepository;
import org.assistantAPI.repository.UserRepository;
import org.assistantAPI.services.AuthorityService;
import org.assistantAPI.services.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;


@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private AuthorityService authorityService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(CreateUserRequest createUserRequest) {
        if (userRepository.findByEmail(createUserRequest.email()).isPresent()) {
            return null;
        }
        User user = new User();
        user.setName(createUserRequest.name());
        user.setEmail(createUserRequest.email());
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));
        user.setAddress(createUserRequest.address());
        user.setPhone(createUserRequest.phone());
        user = userRepository.save(user);

        Authority authority = new Authority();
        authority.setName("USER");
        authority.setUser(user);
        authorityService.createAuthorities(Set.of(authority));

        return user;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}
