package org.demo.employee;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

public class Entities {

    @Data
    @Entity
    public static class Employee {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST,
                CascadeType.MERGE }, mappedBy = "employees")
        private Set<Department> departments = new HashSet<>();
    }

    @Data
    @Entity
    public static class Department {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
        @Fetch(FetchMode.JOIN)
        @JoinTable(name = "dept_emp", joinColumns = { @JoinColumn(name = "dept_id") }, inverseJoinColumns = {
                @JoinColumn(name = "emp_id") })
        private Set<Employee> employees = new HashSet<>();
    }
}
