package com.github.ursteiner.movietracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateWatched;
    @Transient
    private String streamingUrl;
    private String streamingService;
    private String movieId;
    @ManyToOne
    @JoinColumn(name = "user_id")  // Foreign key in 'movie' table
    private AppUser user;
    
    public boolean isInWatchlist() {
        return dateWatched == null;
    }
}

