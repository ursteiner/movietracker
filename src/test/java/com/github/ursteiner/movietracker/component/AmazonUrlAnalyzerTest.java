package com.github.ursteiner.movietracker.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AmazonUrlAnalyzerTest {

    private AmazonUrlAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        analyzer = new AmazonUrlAnalyzer();
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
                Arguments.of("https://www.amazon.de/gp/video/detail/ABC1234", "ABC1234"),
                Arguments.of("https://www.amazon.de/gp/video/detail/1234567?trackId=123456", "1234567"),
                Arguments.of("https://www.amazon.de/gp/video/detail/xyz1234/ref=321", "xyz1234")
        );
    }

    private static Stream<String> provideInvalidUrls() {
        return Stream.of(
                "https://www.amazon.de/gp/video/abc12345678",
                "https://www.amazon.de",
                ""
        );
    }
}