package com.github.ursteiner.movietracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoviesPerMonthDTO {
    private String yearMonth;
    private long countAmazon;
    private long countNetflix;
    private long countYoutube;
    private long countTotal;
}
