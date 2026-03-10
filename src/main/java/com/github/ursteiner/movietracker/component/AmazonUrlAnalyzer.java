package com.github.ursteiner.movietracker.component;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class AmazonUrlAnalyzer extends AbstractStreamingUrlAnalyzer {
    private static final Pattern ID_PATTERN = Pattern.compile("/detail/([^/?#]+)");
    private static final String STREAM_BASE_URL = "https://www.amazon.de/gp/video/detail/";

    public AmazonUrlAnalyzer() {
        super(ID_PATTERN, STREAM_BASE_URL, "Amazon", "amazon.");
    }
}
