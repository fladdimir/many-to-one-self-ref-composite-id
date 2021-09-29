package org.demo.textcontainer;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!mariadb") // postgres
public interface TextContainerRepository extends JpaRepository<TextContainerEntity, Long> {

        List<TextContainerEntity> findByContentContains(String searchTerm);

        List<TextContainerEntity> findByContentIn(Collection<String> searchTerms);

        @Query(nativeQuery = true, value = "SELECT * FROM text_container_entity WHERE content ~ :contentRegex")
        List<TextContainerEntity> findByContentRegex(@Param("contentRegex") String contentRegex);

        @Query(nativeQuery = true, value = "SELECT * FROM text_container_entity WHERE content ~ CONCAT_WS('|', :contentList)")
        List<TextContainerEntity> findByContentList(@Param("contentList") Collection<String> contentList);

        @Query(nativeQuery = true, value = "SELECT * FROM text_container_entity WHERE "
                        + "content ~ CONCAT_WS('|', :contentList) " + "OR " //
                        + "other_info ~ CONCAT_WS('|', :infoList) ")
        List<TextContainerEntity> findByContentListOrInfoList(@Param("contentList") Collection<String> contentList,
                        @Param("infoList") Collection<String> infoList);
}
