package org.demo.manyToOneCompositeKey;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Entities {

    @Entity
    @IdClass(SubjectId.class)
    static class Subject {
        @Id
        String name;

        @Id
        String volume;
    }

    static class SubjectId implements Serializable {
        String name;
        String volume;
    }

    @Entity
    static class Student {
        @Id
        String studentId;

        @ManyToOne
        Subject subject;

        @Column(name = "subject_name", insertable = false, updatable = false)
        String subjectName;
    }
}
