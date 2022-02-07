package org.demo.bookshelf;

import java.util.List;

import org.demo.bookshelf.Book.BookWithIdOnly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT book.id FROM Book book JOIN book.shelfs as shelf WHERE shelf.id=:shelfId")
    List<Long> getIdByShelfsIdJpaQuery(@Param("shelfId") Long shelfId);

    List<BookWithIdOnly> getIdByShelfsId(Long shelfId);

    @Query(nativeQuery = true, //
            value = "SELECT books_id FROM shelf_books WHERE shelfs_id=:shelfId")
    List<Long> getBookIdsForShelfId(@Param("shelfId") Long shelfId);
}
