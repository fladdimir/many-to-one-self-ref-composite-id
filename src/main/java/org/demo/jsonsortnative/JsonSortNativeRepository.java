package org.demo.jsonsortnative;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Profile("default") // postgres
@Repository
public interface JsonSortNativeRepository extends JpaRepository<JsonSortNativeEntity, Long> {

        @Query(value = "SELECT * from json_sort_native_entity ORDER BY "
                        + "json_map->> :sortKey DESC", //
                        countQuery = "select count(*) from json_sort_native_entity", //
                        nativeQuery = true)
        Page<JsonSortNativeEntity> findSortedByJsonValueNative(Pageable pageable, @Param("sortKey") String sortKey);
}
