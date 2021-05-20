package org.demo.manyToOneCompositeKey;

import org.demo.manyToOneCompositeKey.Entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

}
