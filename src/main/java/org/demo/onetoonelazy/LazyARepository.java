package org.demo.onetoonelazy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LazyARepository extends JpaRepository<LazyA, Long> {

    @Query("select a from LazyA a left join fetch a.lazyB where a.id=:id")
    LazyA getLazyA(@Param("id") Long id);
}
