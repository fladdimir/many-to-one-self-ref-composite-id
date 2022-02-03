package org.demo.mapsid;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class B {

    @Id
    private Long id;

    @MapsId
    @OneToOne
    private A a;
}
