package com.github.ursteiner.movietracker.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ArdUrlAnalyzerTest {

    private ArdUrlAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        analyzer = new ArdUrlAnalyzer();
    }

    @ParameterizedTest
    @MethodSource("provideValidUrls")
    void getMovieId_ShouldReturnMovieId_WhenUrlIsValid(String url, String movieId) {
        assertThat(analyzer.getMovieId(url)).isEqualTo(movieId);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUrls")
    void getMovieId_ShouldReturnNull_WhenUrlIsValid(String url) {
        assertThat(analyzer.getMovieId(url)).isNull();
    }

    private static Stream<Arguments> provideValidUrls() {
        return Stream.of(
                Arguments.of("https://www.ardmediathek.de/video/expedition/zwei-tage-in-zweibruecken/swr/Y3JpZDovL3N3ci5kZS9hZXgvbzIyNjY5Nzc", "Y3JpZDovL3N3ci5kZS9hZXgvbzIyNjY5Nzc")
        );
    }

    private static Stream<String> provideInvalidUrls() {
        return Stream.of(
                "https://www.ardmediathek.de",
                ""
        );
    }

}
