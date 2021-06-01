package org.demo.paragraph;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParagraphRepository extends JpaRepository<Paragraph, Long> {

    @EntityGraph(value = "subParagraphsWithQuestions")
    @Query("SELECT p FROM Paragraph p WHERE p.id=:id")
    Paragraph customFindWithSubParagraphQuestions(@Param("id") long id);

}
