package com.github.ursteiner.movietracker.component;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NetflixUrlAnalyzer implements StreamingUrlAnalyzer{
    private static final Pattern ID_PATTERN = Pattern.compile("/watch/(\\d+)");
    private static final String STREAM_BASE_URL = "https://www.netflix.com/watch/";

    @Override
    public boolean matchesStreamingUrl(String url) {
        return url.contains("netflix.");
    }

    @Override
    public boolean matchesStreamingServiceName(String serviceName) {
        return getStreamingServiceName().equals(serviceName);
    }

    @Override
    public String getMovieId(String url) {
        Matcher matcher = ID_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Override
    public String getWatchUrl(String movieId) {
        return STREAM_BASE_URL + movieId;
    }

    @Override
    public String getStreamingServiceName() {
        return "Netflix";
    }
}
