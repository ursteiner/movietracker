package com.github.ursteiner.movietracker.component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractStreamingUrlAnalyzer implements StreamingUrlAnalyzer {

    private final List<Pattern> idPatterns;
    private final String streamBaseUrl;
    private final String streamingServiceName;
    private final String streamingHostIdentifier;

    protected AbstractStreamingUrlAnalyzer(
            List<Pattern> idPatterns,
            String streamBaseUrl,
            String streamingServiceName,
            String streamingHostIdentifier
    ) {
        this.idPatterns = idPatterns;
        this.streamBaseUrl = streamBaseUrl;
        this.streamingServiceName = streamingServiceName;
        this.streamingHostIdentifier = streamingHostIdentifier;
    }

    @Override
    public boolean matchesStreamingUrl(String url) {
        return url.contains(streamingHostIdentifier);
    }

    @Override
    public boolean matchesStreamingServiceName(String serviceName) {
        return getStreamingServiceName().equals(serviceName);
    }

    @Override
    public String getMovieId(String url) {
        for(Pattern idPattern : idPatterns) {
            Matcher matcher = idPattern.matcher(url);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    @Override
    public String getWatchUrl(String movieId) {
        return streamBaseUrl + movieId;
    }

    @Override
    public String getStreamingServiceName() {
        return streamingServiceName;
    }
}

