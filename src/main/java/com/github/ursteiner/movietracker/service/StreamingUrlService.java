package com.github.ursteiner.movietracker.service;

import com.github.ursteiner.movietracker.component.StreamingUrlAnalyzer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StreamingUrlService {
    private final List<StreamingUrlAnalyzer> analyzers;

    public StreamingUrlService(List<StreamingUrlAnalyzer> analyzers) {
        this.analyzers = analyzers;
    }

    public Optional<StreamingUrlAnalyzer> findAnalyzerByUrl(String url) {
        return analyzers.stream()
                .filter(analyzer -> analyzer.matchesStreamingUrl(url))
                .findFirst();
    }

    public Optional<StreamingUrlAnalyzer> findAnalyzerByServiceName(String serviceName) {
        return analyzers.stream()
                .filter(analyzer -> analyzer.matchesStreamingServiceName(serviceName))
                .findFirst();
    }

    public String getServiceName(String url) {
        return findAnalyzerByUrl(url).map(StreamingUrlAnalyzer::getStreamingServiceName).orElse("Unknown");
    }

    public String getMovieId(String url) {
        return findAnalyzerByUrl(url).map(a -> a.getMovieId(url)).orElse(null);
    }

    public String getMovieWatchUrl(String serviceName, String movieId) {
        return findAnalyzerByServiceName(serviceName).map(a -> a.getWatchUrl(movieId)).orElse(null);
    }
}
