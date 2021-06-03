package org.demo.roles;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PreRemove;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @EqualsAndHashCode.Exclude
    @ManyToMany
    private Set<PermissionEntity> permissions = new HashSet<>();

    @PreRemove
    public void preRemove() {
        // remove permissions if this was the only associated role
        var permissionRepository = RepositoryService.getInstance().getPermissionRepository();
        permissions.stream().filter(p -> p.getRoles().size() == 1).forEach(permissionRepository::delete);
    }
}
