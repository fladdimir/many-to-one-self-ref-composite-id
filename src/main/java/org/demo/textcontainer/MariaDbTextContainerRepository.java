package org.demo.textcontainer;

import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mariadb")
public interface MariaDbTextContainerRepository extends TextContainerRepository {

        // 'REGEXP' instead of '~'
        @Override
        @Query(nativeQuery = true, value = "SELECT * FROM text_container_entity WHERE content REGEXP :contentRegex")
        List<TextContainerEntity> findByContentRegex(@Param("contentRegex") String contentRegex);

        @Override
        @Query(nativeQuery = true, value = "SELECT * FROM text_container_entity WHERE content REGEXP CONCAT_WS('|', :contentList)")
        List<TextContainerEntity> findByContentList(@Param("contentList") Collection<String> contentList);

        @Override
        @Query(nativeQuery = true, value = "SELECT * FROM text_container_entity WHERE "
                        + "content REGEXP CONCAT_WS('|', :contentList) " + "OR "
                        + "other_info REGEXP CONCAT_WS('|', :infoList) ")
        List<TextContainerEntity> findByContentListOrInfoList(@Param("contentList") Collection<String> contentList,
                        @Param("infoList") Collection<String> infoList);
}
