package org.demo.sql_array;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.vladmihalcea.hibernate.type.array.IntArrayType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.Data;
import lombok.NoArgsConstructor;

@TypeDef(name = "int-array", typeClass = IntArrayType.class)
@Data
@NoArgsConstructor
@Entity
public class SqlArrayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Type(type = "int-array")
    @Column(columnDefinition = "INTEGER[]")
    private int[] ints;

}
