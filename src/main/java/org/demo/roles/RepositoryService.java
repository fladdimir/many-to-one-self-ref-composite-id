package org.demo.roles;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Service
public class RepositoryService {

    private static RepositoryService instance;

    public static RepositoryService getInstance() {
        return instance;
    }

    @PostConstruct
    public void postConstruct() {
        instance = this;
    }

    @Getter
    @Autowired
    private PermissionRepository permissionRepository;
}
