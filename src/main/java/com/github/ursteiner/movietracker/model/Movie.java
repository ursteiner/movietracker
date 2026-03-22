package com.github.ursteiner.movietracker.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateWatched;
    @Transient
    private String streamingUrl;
    private Boolean inWatchlist;
    private String streamingService;
    private String movieId;
    @ManyToOne
    @JoinColumn(name = "user_id")  // Foreign key in 'movie' table
    private AppUser user;
}

