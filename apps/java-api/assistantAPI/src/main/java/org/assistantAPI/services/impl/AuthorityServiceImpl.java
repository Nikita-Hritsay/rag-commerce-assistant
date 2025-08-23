package org.assistantAPI.services.impl;

import lombok.AllArgsConstructor;
import org.assistantAPI.domain.Authority;
import org.assistantAPI.repository.AuthorityRepository;
import org.assistantAPI.services.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@AllArgsConstructor
public class AuthorityServiceImpl  implements AuthorityService {
    private AuthorityRepository authorityRepository;

    @Override
    public void createAuthorities(Set<Authority> authorities) {
        authorityRepository.saveAll(authorities);
    }
}
