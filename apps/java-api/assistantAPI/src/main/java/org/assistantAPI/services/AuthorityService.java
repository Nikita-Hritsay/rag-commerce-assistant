package org.assistantAPI.services;

import org.assistantAPI.domain.Authority;

import java.util.Set;

public interface AuthorityService {
    public void createAuthorities(Set<Authority> authorities);
}
