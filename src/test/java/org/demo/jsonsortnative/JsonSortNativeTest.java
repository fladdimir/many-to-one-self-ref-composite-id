package org.demo.jsonsortnative;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;

// @ActiveProfiles("mysql")
@SpringBootTest
class JsonSortNativeTest {

    @Autowired
    private JsonSortNativeRepository repository;

    @Autowired
    private Environment env;

    @BeforeEach
    void beforeEach() {
        repository.deleteAllInBatch();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void test() {
        int n = 10;
        for (int i = 0; i < n; i++) {
            var entity = new JsonSortNativeEntity();
            entity.getJsonMap().put("key1", "value" + i);
            repository.save(entity);
        }
        assertThat(repository.findAll()).hasSize(n);
        repository.findAll().forEach(en -> assertThat(en.getJsonMap().get("key1")).startsWith("value"));

        // via native query
        var pageable = PageRequest.of(1, 2);
        // json path is specified differently for postgres and mysql..
        String sortKey = Arrays.asList(env.getActiveProfiles()).contains("mysql") ? "$.key1" // mysql
                : "key1"; // postgres
        var pageResult = repository.findSortedByJsonValueNative(pageable, sortKey);
        assertThat(pageResult.getNumberOfElements()).isEqualTo(2);
        assertThat(pageResult.get().map(en -> en.getJsonMap().get("key1"))).containsExactly("value7", "value6");
    }
}
