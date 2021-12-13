package org.demo.querydsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;

import com.querydsl.core.types.dsl.BooleanExpression;

import org.demo.bookshelf.QShelf;
import org.demo.bookshelf.Shelf;
import org.demo.bookshelf.ShelfRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QueryDslTest {

    @Autowired
    private ShelfRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.deleteAllInBatch();
        int n = 10;
        IntStream.range(5, 5 + n).forEach(i -> {
            var entity = new Shelf();
            entity.setName("e_" + i); // 5x e_1%
            entity.setCategoryNumber(i % 2); // 5x 0, 5x 1
            repository.save(entity);
        });
        assertThat(repository.count()).isEqualTo(n);
    }

    @Test
    void testQueryDslPredicateQuery() {
        BooleanExpression nameContains1 = QShelf.shelf.name.contains("1");
        var result = repository.findAll(nameContains1);
        assertThat(result).hasSize(5);

        BooleanExpression isCategory0 = QShelf.shelf.categoryNumber.eq(0);
        var result2 = repository.findAll(isCategory0);
        assertThat(result2).hasSize(5);

        var result3 = repository.findAll(nameContains1.and(isCategory0));
        assertThat(result3).hasSize(3)
                .allMatch(entity -> entity.getName().contains("1") && entity.getCategoryNumber() == 0);

        var result4 = repository.findAll(nameContains1.or(isCategory0));
        assertThat(result4).hasSize(7)
                .allMatch(entity -> entity.getName().contains("1") || entity.getCategoryNumber() == 0);

    }

    // todo: more tests including e.g. a subquery on an assocation
    // QShelf.shelf.books.contains(JPAExpressions.selectFrom(QBook.book).where(QBook.book.shelfs.size().gt(Expressions.THREE)));
}
