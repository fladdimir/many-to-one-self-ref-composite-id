package org.demo.sql_array;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SqlArrayEntityRepository
        extends JpaRepository<SqlArrayEntity, Long>, QuerydslPredicateExecutor<SqlArrayEntity> {

}
