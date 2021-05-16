package org.demo.inheritence;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@ToString
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue(SourceType.OFFERS_TEXT)
public class StoredOfferSource extends StoredSource {

    @Column(name = "offer_id")
    private String offerId;

}
