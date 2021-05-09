package org.demo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Entity
@IdClass(UserEntityCompositeId.class)
public class UserEntity {

    @Id
    private Integer id;

    @Id
    private String firstName;

    @Id
    private String lastName;

    @JoinColumn(name = "parent_id", referencedColumnName = "id", updatable = false, insertable = true)
    @JoinColumn(name = "parent_firstname", referencedColumnName = "firstname", updatable = false, insertable = true)
    @JoinColumn(name = "parent_lastname", referencedColumnName = "lastname", updatable = false, insertable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    private UserEntity parent = null;

    @Setter(AccessLevel.NONE)
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<UserEntity> children = new HashSet<>();

    public Set<UserEntity> getChildren() {
        return Collections.unmodifiableSet(children); // add only via method below
    }

    public void addChild(UserEntity child) {
        child.parent = this; // sync owning side of the association
        this.children.add(child);
    }

    public void setParent(UserEntity parent) {
        parent.addChild(this);
    }
}
