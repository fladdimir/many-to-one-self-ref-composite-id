package org.demo.manyToOneCompositeKey;

import org.demo.manyToOneCompositeKey.Entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {

}
