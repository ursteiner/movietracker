package com.github.ursteiner.movietracker.component;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class YoutubeUrlAnalyzer extends AbstractStreamingUrlAnalyzer {

    private static final Pattern ID_PATTERN = Pattern.compile("/watch\\?v=([^/?&#]+)");
    private static final String STREAM_BASE_URL = "https://www.youtube.com/watch?v=";

    public YoutubeUrlAnalyzer() {
        super(ID_PATTERN, STREAM_BASE_URL, "Youtube", "youtube.");
    }
}
