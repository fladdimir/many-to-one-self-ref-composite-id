package org.demo.countassociated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class Plan {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private RateCard rateCard;

}
