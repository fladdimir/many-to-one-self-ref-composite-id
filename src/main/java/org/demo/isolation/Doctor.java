package org.demo.isolation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Data;

@Data
@Entity
public class Doctor {

    @Id
    @GeneratedValue
    private Long id;

    private boolean onCall = true;
}