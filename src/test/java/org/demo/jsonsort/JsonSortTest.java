package org.demo.jsonsort;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
class JsonSortTest {

    @Autowired
    private JsonSortRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.deleteAllInBatch();
        assertThat(repository.findAll()).isEmpty();
    }

    @Test
    void test() {
        int n = 10;
        for (int i = 0; i < n; i++) {
            var entity = new JsonSortEntity();
            entity.getJsonMap().put("key1", "value" + i);
            repository.save(entity);
        }
        assertThat(repository.findAll()).hasSize(n);
        repository.findAll().forEach(en -> assertThat(en.getJsonMap().get("key1")).startsWith("value"));
        repository.findAll().forEach(en -> assertThat(en.getFormulaValue()).startsWith("value"));

        // via formula
        var sort = Sort.by("formulaValue").descending();
        PageRequest pageable = PageRequest.of(1, 2, sort);
        var pageResult = repository.findAll(pageable);
        assertThat(pageResult.getNumberOfElements()).isEqualTo(2);
        assertThat(pageResult.get().map(en -> en.getJsonMap().get("key1"))).containsExactly("value7", "value6");
    }
}
