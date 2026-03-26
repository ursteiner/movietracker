package com.github.ursteiner.movietracker.controller;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort.Direction;

import static org.assertj.core.api.Assertions.assertThat;

class MovieControllerTest {

    @Nested
    class NormalizePageParameter {
        @Test
        void normalizePageNumber_ShouldFallbackToFirstPage_WhenPageIsZeroOrNegative() {
            assertThat(MovieController.normalizePageNumber(0)).isEqualTo(1);
            assertThat(MovieController.normalizePageNumber(-5)).isEqualTo(1);
        }

        @Test
        void normalizePageNumber_ShouldKeepPositivePageNumber() {
            assertThat(MovieController.normalizePageNumber(3)).isEqualTo(3);
        }
    }

    @Nested
    class NormalizeSortParameter {
        @Test
        void normalizeSortBy_ShouldFallbackToDateWatched_WhenFieldIsNotAllowed() {
            assertThat(MovieController.normalizeSortBy("id")).isEqualTo(AllowedSortField.DATE_WATCHED);
            assertThat(MovieController.normalizeSortBy(null)).isEqualTo(AllowedSortField.DATE_WATCHED);
        }

        @Test
        void normalizeSortBy_ShouldKeepAllowedFields() {
            assertThat(MovieController.normalizeSortBy(AllowedSortField.NAME.property())).isEqualTo(AllowedSortField.NAME);
            assertThat(MovieController.normalizeSortBy(AllowedSortField.DATE_WATCHED.property())).isEqualTo(AllowedSortField.DATE_WATCHED);
        }

        @Test
        void normalizeSortOrder_ShouldReturnAscOnlyForAscInput() {
            assertThat(MovieController.normalizeSortOrder("asc")).isEqualTo(Direction.ASC);
            assertThat(MovieController.normalizeSortOrder("ASC")).isEqualTo(Direction.ASC);
            assertThat(MovieController.normalizeSortOrder("desc")).isEqualTo(Direction.DESC);
            assertThat(MovieController.normalizeSortOrder("anything-else")).isEqualTo(Direction.DESC);
            assertThat(MovieController.normalizeSortOrder(null)).isEqualTo(Direction.DESC);
        }
    }
}

