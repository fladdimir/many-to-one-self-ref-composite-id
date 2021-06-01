package org.demo.paragraph;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NamedEntityGraph(name = "subParagraphsWithQuestions", attributeNodes = {
    @NamedAttributeNode(value = "subParagraphs", subgraph = "subgraph") }, subgraphs = {
        @NamedSubgraph(name = "subgraph", attributeNodes = { @NamedAttributeNode(value = "questions") }) })
@Data
@NoArgsConstructor
@Entity
public class Paragraph {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Long id;

  @EqualsAndHashCode.Exclude
  @OneToMany
  private Set<Question> questions = new HashSet<>();

  @EqualsAndHashCode.Exclude
  @OneToMany
  private Set<Paragraph> subParagraphs = new HashSet<>();

}
