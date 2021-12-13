package org.demo.bookshelf;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShelfRepository extends JpaRepository<Shelf, Long>, QuerydslPredicateExecutor<Shelf> {

    @Lock(LockModeType.OPTIMISTIC)
    @Override
    Optional<Shelf> findById(Long id);
}
