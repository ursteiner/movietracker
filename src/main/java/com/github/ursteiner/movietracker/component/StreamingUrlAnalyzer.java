package com.github.ursteiner.movietracker.component;

public interface StreamingUrlAnalyzer {

    boolean matchesStreamingUrl(String url);
    boolean matchesStreamingServiceName(String serviceName);
    String getStreamingServiceName();
    String getMovieId(String url);
    String getWatchUrl(String movieId);
}
