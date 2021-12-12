package org.demo.geometry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.locationtech.jts.geom.Geometry;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // should only be included for profile 'mysql'
@Data
@NoArgsConstructor
public class SampleGeometryEntity {

    @Id
    @GeneratedValue
    Long id;

    @Column(columnDefinition = "geometry")
    private Geometry boundary;

}
