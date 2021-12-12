package org.demo.textcontainer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.EnabledIf;

// 'docker-compose up' before running the tests (or use test-containers)
@SpringBootTest
// test native queries for mariadb
// docker-compose -f docker-compose_mariadb.yml up
// export SPRING_PROFILES_ACTIVE=mariadb
// @ActiveProfiles("mariadb")
@EnabledIf(expression = "#{environment.acceptsProfiles('default', 'mariadb', 'postgis')}", loadContext = true)
class QuestionQueryTest {

    @Autowired
    TextContainerRepository repo;

    @BeforeEach
    void before() {
        repo.deleteAllInBatch();

        IntStream.rangeClosed(1, 4).forEach(i -> {
            var t = new TextContainerEntity();
            t.setContent("c" + i);
            t.setOtherInfo("i" + i);
            repo.save(t);
        });
    }

    @Test
    void testContains() {
        var result = repo.findByContentContains("c");
        assertThat(result).hasSize(4);

        var result2 = repo.findByContentContains("2");
        assertThat(result2).hasSize(1);
    }

    @Test
    void testListContains() {
        var result = repo.findByContentIn(List.of("c1"));
        assertThat(result).hasSize(1);
    }

    @ParameterizedTest
    @MethodSource
    void testContentRegex(String contentRegex, Integer expectedResultSize) {
        var result = repo.findByContentRegex(contentRegex);
        assertThat(result).hasSize(expectedResultSize);
    }

    static Stream<Arguments> testContentRegex() {
        return Stream.of( //
                Arguments.of("c1", 1), //
                Arguments.of("1", 1), //
                Arguments.of("c", 4), //
                Arguments.of("c3|4", 2) //
        );
    }

    @ParameterizedTest
    @MethodSource
    void testContentRegexList(List<String> contentList, Integer expectedResultSize) {
        var result = repo.findByContentList(contentList);
        assertThat(result).hasSize(expectedResultSize);
    }

    static Stream<Arguments> testContentRegexList() {
        return Stream.of( //
                Arguments.of(List.of("1"), 1), //
                Arguments.of(List.of("c3", "4"), 2), //
                Arguments.of(List.of("a", "c3", "4", "xx"), 2) //
        );
    }

    @ParameterizedTest
    @MethodSource
    void testContentOrInfoRegexList(List<String> contentList, List<String> infoList, Integer expectedResultSize) {
        var result = repo.findByContentListOrInfoList(contentList, infoList);
        assertThat(result).hasSize(expectedResultSize);
    }

    static Stream<Arguments> testContentOrInfoRegexList() {
        return Stream.of( //
                Arguments.of(List.of("c3", "4"), List.of("i3"), 2), //
                Arguments.of(List.of("asdf", "qwre"), List.of("n", "n"), 0), //
                Arguments.of(List.of("c3", "4"), List.of("i2", "4"), 3) //
        );
    }
}
