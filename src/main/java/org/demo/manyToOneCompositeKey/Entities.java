package org.demo.manyToOneCompositeKey;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

public class Entities {

    @Entity
    @IdClass(SubjectId.class)
    class Subject {
        @Id
        String name;

        @Id
        String volume;
    }

    class SubjectId implements Serializable {
        String name;
        String volume;
    }

    @Entity
    class Student {
        @Id
        String studentId;

        @ManyToOne
        Subject subject;
    }
}
