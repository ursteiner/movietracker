package com.github.ursteiner.movietracker.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoviesPerMonthDTO {
    private String yearMonth;
    private long countAmazon;
    private long countNetflix;
    private long countYoutube;
    private long countTotal;
}
