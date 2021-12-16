package org.demo.unique;

import javax.persistence.PersistenceException;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface EntityWithUniqueNameRepository extends JpaRepository<EntityWithUniqueName, Long> {

    @Override
    @Transactional(noRollbackFor = PersistenceException.class)
    <S extends EntityWithUniqueName> S saveAndFlush(S entity);
}
