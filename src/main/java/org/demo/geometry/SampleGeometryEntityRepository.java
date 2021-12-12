package org.demo.geometry;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Profile("mysql")
@Repository
public interface SampleGeometryEntityRepository extends JpaRepository<SampleGeometryEntity, Long> {

    @Query(value = "SELECT boundary FROM SampleGeometryEntity")
    List<Geometry> getAllBoundaries();

}
