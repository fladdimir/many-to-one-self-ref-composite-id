package org.demo.extralazy;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ElChild {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String myValue;

    @ManyToOne(fetch = FetchType.LAZY)
    private ElParent parent;

    public ElChild(String myValue, ElParent parent) {
        this.myValue = myValue;
        this.parent = parent;
    }
}
