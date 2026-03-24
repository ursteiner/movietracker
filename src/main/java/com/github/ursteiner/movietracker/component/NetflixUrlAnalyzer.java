package com.github.ursteiner.movietracker.component;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class NetflixUrlAnalyzer extends AbstractStreamingUrlAnalyzer {
    private static final List<Pattern> ID_PATTERNS = List.of(
            Pattern.compile("/watch/(\\d+)"),
            Pattern.compile("/title/(\\d+)"),
            Pattern.compile("/browse\\?jbv=(\\d+)")
    );
    private static final String STREAM_BASE_URL = "https://www.netflix.com/title/";

    public NetflixUrlAnalyzer() {
        super(ID_PATTERNS, STREAM_BASE_URL, "Netflix", "netflix.");
    }
}
