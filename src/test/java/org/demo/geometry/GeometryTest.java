package org.demo.geometry;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

// 'docker-compose up' before running the tests (or use test-containers)
// geometry features only available in mysql (and postgis, requires setup)
// (no spatial hibernate driver for mariadb?)
@SpringBootTest
// @ActiveProfiles("mysql")
@EnabledIf(expression = "#{environment.acceptsProfiles('mysql')}", loadContext = true)
class GeometryTest {

    @Autowired
    SampleGeometryEntityRepository shelfRepository;

    @BeforeEach
    void setup() {
        shelfRepository.deleteAllInBatch();
    }

    private static final Point POINT_1_1 = new GeometryFactory().createPoint(new Coordinate(1, 1));
    private static final Point POINT_1_2 = new GeometryFactory().createPoint(new Coordinate(1, 2));

    @Test
    void testGeometry() {
        var shelf = new SampleGeometryEntity();
        shelf.setBoundary(POINT_1_1);
        shelfRepository.save(shelf);

        shelf = shelfRepository.findAll().get(0);
        assertThat(shelf.getBoundary().distance(POINT_1_2))
                .isEqualTo(1);
    }

    @Test
    void testGeometryList() {
        var shelf = new SampleGeometryEntity();
        shelf.setBoundary(POINT_1_1);
        shelfRepository.save(shelf);
        shelf = new SampleGeometryEntity();
        shelf.setBoundary(POINT_1_2);
        shelfRepository.save(shelf);

        var boundaries = shelfRepository.getAllBoundaries();
        assertThat(boundaries).containsExactlyInAnyOrder(POINT_1_1, POINT_1_2);
    }
}
