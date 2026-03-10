package com.github.ursteiner.movietracker.component;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class NetflixUrlAnalyzer extends AbstractStreamingUrlAnalyzer {
    private static final Pattern ID_PATTERN = Pattern.compile("/watch/(\\d+)");
    private static final String STREAM_BASE_URL = "https://www.netflix.com/watch/";

    public NetflixUrlAnalyzer() {
        super(ID_PATTERN, STREAM_BASE_URL, "Netflix", "netflix.");
    }
}
