package org.demo.sql_array;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OrderColumn;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.ListArrayType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.Data;
import lombok.NoArgsConstructor;

@TypeDef(name = "int-array", typeClass = IntArrayType.class)
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
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

    @ElementCollection
    @OrderColumn
    private List<Long> longs;
}
