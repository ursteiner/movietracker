package com.github.ursteiner.movietracker.component;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class AmazonUrlAnalyzer extends AbstractStreamingUrlAnalyzer {
    private static final List<Pattern> ID_PATTERNS = List.of(Pattern.compile("/detail/([^/?#]+)"));
    private static final String STREAM_BASE_URL = "https://www.amazon.de/gp/video/detail/";

    public AmazonUrlAnalyzer() {
        super(ID_PATTERNS, STREAM_BASE_URL, "Amazon", "amazon.");
    }
}
