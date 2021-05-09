package org.demo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntityCompositeId implements Serializable {
    private Integer id;

    private String firstName;

    private String lastName;
}
