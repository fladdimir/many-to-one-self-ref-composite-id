package org.demo.jsonsortnative;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Profile("mysql")
@Repository
public interface JsonSortNativeRepositoryMySql extends JsonSortNativeRepository {

        @Query(value = "SELECT * FROM json_sort_native_entity ORDER BY "
                        + "json_map->> :sortKey" // <-- the json-key for the value to order by
                        + " DESC", // direction
                        countQuery = "select count(*) from json_sort_native_entity", //
                        nativeQuery = true)
        Page<JsonSortNativeEntity> findSortedByJsonValueNative(Pageable pageable, @Param("sortKey") String sortKey);
}
