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
@DiscriminatorValue(SourceType.ADS_TEXT)
public class StoredAdSource extends StoredSource {

    @Column(name = "ad_id")
    private String adId;

}
