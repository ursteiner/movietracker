package com.github.ursteiner.movietracker.component;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class YoutubeUrlAnalyzerTest {

    private YoutubeUrlAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        analyzer = new YoutubeUrlAnalyzer();
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
                Arguments.of("https://www.youtube.com/watch?v=1a2b&pp=sAQB", "1a2b"),
                Arguments.of("https://www.youtube.com/watch?v=1122aa", "1122aa")
        );
    }

    private static Stream<String> provideInvalidUrls() {
        return Stream.of(
                "https://www.youtube.com/watch?v=",
                "https://www.youtube.com/watch/12345",
                ""
        );
    }

}
