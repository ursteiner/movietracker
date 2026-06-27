package com.github.ursteiner.movietracker.component;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class ArdUrlAnalyzer extends AbstractStreamingUrlAnalyzer{
    private static final List<Pattern> ID_PATTERNS = List.of(
            Pattern.compile("/([A-Za-z0-9]+)$")
    );
    private static final String STREAM_BASE_URL = "https://www.ardmediathek.de/video/";

    public ArdUrlAnalyzer() {
        super(ID_PATTERNS, STREAM_BASE_URL, "ARD media library", "ardmediathek.");
    }
}
