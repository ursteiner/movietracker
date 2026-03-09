package com.github.ursteiner.movietracker.component;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class NetflixUrlAnalyzerTest {

    private NetflixUrlAnalyzer analyzer;

    @BeforeEach
    public void setUp() {
        analyzer = new NetflixUrlAnalyzer();
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
                Arguments.of("https://www.netflix.com/watch/12345678", "12345678"),
                Arguments.of("https://www.netflix.de/watch/1234567?trackId=123456", "1234567")
        );
    }

    private static Stream<String> provideInvalidUrls() {
        return Stream.of(
                "https://www.netflix.com/watch/abc12345678",
                "https://www.netflix.com/watch/",
                "https://www.netflix.com",
                ""
        );
    }
}