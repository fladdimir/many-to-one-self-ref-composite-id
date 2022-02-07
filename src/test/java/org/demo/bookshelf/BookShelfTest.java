package org.demo.bookshelf;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.demo.bookshelf.Book.BookWithIdOnly;
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
		var book = bookRepository.saveAndFlush(new Book());
		var shelf = shelfRepository.saveAndFlush(new Shelf());

		bookId = book.getId();
		shelfId = shelf.getId();

		tm.commit(ts);
	}

	private Long bookId;
	private Long shelfId;

	@Test
	void testLoadManyIds() {
		var ts = tm.getTransaction(TransactionDefinition.withDefaults());
		var shelf = shelfRepository.findById(shelfId).get();
		var book = bookRepository.findById(bookId).get();
		shelf.books.add(book);
		book.shelfs.add(shelf);
		tm.commit(ts);

		ts = tm.getTransaction(TransactionDefinition.withDefaults());
		System.out.println("\n\nFIND SHELF BY ID\n\n");
		shelf = shelfRepository.findById(shelfId).get();
		System.out.println("\n\nFIND BOOK BY ID\n\n");
		book = bookRepository.findById(bookId).get();
		System.out.println("\n\nSHELF BOOKS SIZE\n\n");
		assertThat(shelf.books).hasSize(1);
		System.out.println("\n\nBOOK SHELFS SIZE\n\n");
		assertThat(book.shelfs).hasSize(1);
		tm.commit(ts);

		ts = tm.getTransaction(TransactionDefinition.withDefaults());

		System.out.println("\n\nFIND SHELF BY ID\n\n");
		shelf = shelfRepository.findById(shelfId).get();

		System.out.println("\n\nFIND SHELF IDs BY BOOK ID (JPA-QUERY)\n\n");
		List<Long> bookIds1 = bookRepository.getIdByShelfsIdJpaQuery(shelf.getId());
		assertThat(bookIds1).containsExactlyInAnyOrder(bookId);

		System.out.println("\n\nFIND SHELF IDs BY BOOK ID (SPRING-DATA-QUERY)\n\n");
		List<BookWithIdOnly> bookIds2 = bookRepository.getIdByShelfsId(shelf.getId());
		assertThat(bookIds2.stream().map(BookWithIdOnly::getId)).containsExactlyInAnyOrder(bookId);

		System.out.println("\n\nFIND SHELF IDs BY BOOK ID (NATIVE-QUERY)\n\n");
		List<Long> bookIds3 = bookRepository.getBookIdsForShelfId(shelf.getId());
		assertThat(bookIds3).containsExactlyInAnyOrder(bookId);

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
