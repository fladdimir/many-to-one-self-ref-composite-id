package org.demo.extralazy;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ElParent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    // @BatchSize(size = 2) // <-- no effect for list-accesses!
    @LazyCollection(LazyCollectionOption.EXTRA)
    @OrderColumn(name = "el_child_order_column")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parent")
    private List<ElChild> children = new ArrayList<>();

}
