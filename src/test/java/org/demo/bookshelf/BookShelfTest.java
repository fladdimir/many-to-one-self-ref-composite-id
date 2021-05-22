package org.demo.bookshelf;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class BookShelfTest {

	@Autowired
	BookRepository bookRepository;

	@Autowired
	ShelfRepository shelfRepository;

	@Autowired
	PlatformTransactionManager tm;

	@BeforeEach
	public void setup() {
		var ts = tm.getTransaction(TransactionDefinition.withDefaults());

		shelfRepository.deleteAll();
		bookRepository.deleteAll();

		// create not yet associated entities:
		bookRepository.save(new Book());
		shelfRepository.save(new Shelf());

		tm.commit(ts);
	}

	@Test
	void test() {
		var ts = tm.getTransaction(TransactionDefinition.withDefaults());

		var books = bookRepository.findAll();
		assertThat(books).hasSize(1);
		var shelfs = shelfRepository.findAll();
		assertThat(shelfs).hasSize(1);
		var book_1 = books.get(0);
		var shelf_1 = shelfs.get(0);

		assertThat(shelf_1.getVersion()).isZero();

		book_1.shelfs.add(shelf_1);
		shelf_1.books.add(book_1); // synchronized association

		bookRepository.saveAndFlush(book_1);
		shelfRepository.saveAndFlush(shelf_1); // not dirty, since not owning the association

		assertThat(shelf_1.books).hasSize(1);

		tm.commit(ts);

		// 2nd transaction to check that everything worked fine:
		ts = tm.getTransaction(TransactionDefinition.withDefaults());

		shelf_1 = shelfRepository.findAll().get(0);
		assertThat(shelf_1.getVersion()).isZero(); // still 0!
		assertThat(shelf_1.books).hasSize(1); // join-table successfully updated

		tm.commit(ts);
	}

}
