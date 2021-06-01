package org.demo.paragraph;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
class ParagraphTest {

  @Autowired
  ParagraphRepository paragraphRepository;

  @Autowired
  QuestionRepository questionRepository;

  @Autowired
  PlatformTransactionManager tm;

  Long pid1;
  Long pid2;
  Long pid3;
  Long pid4;

  @BeforeEach
  public void setup() {
    paragraphRepository.deleteAllInBatch();
    questionRepository.deleteAllInBatch();
    assertThat(paragraphRepository.findAll()).isEmpty();
    assertThat(questionRepository.findAll()).isEmpty();

    // p -> p2, p3
    // p3 -> p4
    var p1 = paragraphRepository.save(new Paragraph());
    var p2 = paragraphRepository.save(new Paragraph());
    var p3 = paragraphRepository.save(new Paragraph());
    var p4 = paragraphRepository.save(new Paragraph());
    var q1 = questionRepository.save(new Question());
    var q2 = questionRepository.save(new Question());
    var q3 = questionRepository.save(new Question());
    var q4 = questionRepository.save(new Question());

    p1.getSubParagraphs().add(p2);
    p1.getSubParagraphs().add(p3);
    p3.getSubParagraphs().add(p4);
    p1.getQuestions().add(q1);
    p2.getQuestions().add(q2);
    p3.getQuestions().add(q3);
    p4.getQuestions().add(q4);
    List.of(p1, p2, p3, p4).forEach(paragraphRepository::save);

    List.of(q1, q2, q3, q4).forEach(q -> {
      q.setContent("content" + q.getId());
      questionRepository.save(q);
    });

    pid1 = p1.getId();
    pid2 = p2.getId();
    pid3 = p3.getId();
    pid4 = p4.getId();
  }

  @Test
  void test() {
    var ts = tm.getTransaction(TransactionDefinition.withDefaults());
    List<Paragraph> all = paragraphRepository.findAll();
    assertThat(all).hasSize(4);
    var p = paragraphRepository.findById(pid1).get();
    assertThat(p.getSubParagraphs()).hasSize(2);
    var iterator = p.getSubParagraphs().iterator();
    var p2 = iterator.next();
    var p3 = iterator.next();
    assertThat(p.getQuestions()).hasSize(1);
    assertThat(p2.getQuestions()).hasSize(1);
    assertThat(p3.getQuestions()).hasSize(1);
    tm.commit(ts);
  }

  @Test
  void testQuery() {
    var ts = tm.getTransaction(TransactionDefinition.withDefaults());

    System.out.println("--- issueing first DB select... ---");

    Paragraph p = paragraphRepository.customFindWithSubParagraphQuestions(pid1);

    var subps = p.getSubParagraphs();
    assertThat(subps).hasSize(2);
    assertThat(subps).extracting(s -> s.getId()).containsExactlyInAnyOrder(pid2, pid3);

    // assertions over the content of all questions associated to all direct
    // subparagraphs
    var questions = subps.stream().flatMap(s -> s.getQuestions().stream()).collect(Collectors.toList());
    assertThat(questions).hasSize(2);
    questions.forEach(q -> assertThat(q.getContent()).isEqualTo("content" + q.getId()));
    // all info already fetched -> no further query needed

    System.out.println("--- no additional select needed so far ---");

    // query needed again in case info on the subparagraph of a subparagraph is
    // accessed..
    var p4 = subps.stream().filter(sp -> sp.getSubParagraphs().stream().anyMatch(ssp -> ssp.getId().equals(pid4)))
        .findFirst().get();
    assertThat(p4.getQuestions()).hasSize(1);
    var q4 = p4.getQuestions().iterator().next();
    assertThat(q4.getContent()).isEqualTo("content" + q4.getId());

    tm.commit(ts);
  }

}
