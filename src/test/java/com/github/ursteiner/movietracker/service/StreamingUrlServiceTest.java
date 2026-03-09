package com.github.ursteiner.movietracker.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class StreamingUrlServiceTest {

    StreamingUrlService streamingUrlService = new StreamingUrlService(new ArrayList<>());

    @Test
    void getServiceName_ShouldReturnUnknown_WhenNoMatchingAnalyzerFound() {
        assertThat(streamingUrlService.getServiceName("https://www.netflix.com/watch/12345678")).isEqualTo("Unknown");
    }

    @Test
    void getMovieId_ShouldReturnNul_WhenNoMatchingAnalyzerFound() {
        assertThat(streamingUrlService.getMovieId("https://www.netflix.com/watch/12345678")).isNull();
    }

    @Test
    void getMovieWatchUrl_ShouldReturnNul_WhenNoMatchingAnalyzerFound() {
        assertThat(streamingUrlService.getMovieWatchUrl("XY", "123")).isNull();
    }
}
