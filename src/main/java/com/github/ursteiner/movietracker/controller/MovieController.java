package com.github.ursteiner.movietracker.controller;

import java.util.Optional;
import java.util.UUID;

import com.github.ursteiner.movietracker.model.AppUser;
import com.github.ursteiner.movietracker.model.Movie;
import com.github.ursteiner.movietracker.security.CurrentUserProvider;
import com.github.ursteiner.movietracker.service.MovieService;
import com.github.ursteiner.movietracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MovieController {

    private static final int PAGE_SIZE = 15;

    private final MovieService movieService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public MovieController(MovieService movieService, UserService userService, CurrentUserProvider currentUserProvider) {
        this.movieService = movieService;
        this.userService = userService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/movies")
    public String listMovies(Model model,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam(required = false) String searchName,
                             @RequestParam(defaultValue = "dateWatched") String sortBy,
                             @RequestParam(defaultValue = "desc") String sortOrder) {

        Pageable paging = createPageable(page, sortBy, sortOrder);
        Page<Movie> moviePage = movieService.getWatchedMovies(currentUserProvider.getCurrentUserId(), searchName, paging);

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("page", moviePage.getNumber() + 1);
        model.addAttribute("totalMovies", moviePage.getTotalElements());
        model.addAttribute("activePage", "list");
        model.addAttribute("searchName", searchName);
        addPagingAttributes(model, paging, moviePage.getTotalPages());

        return "list-movies";
    }

    @GetMapping("/watchlist")
    public String listWatchlistMovies(Model model,
                                      @RequestParam("page") Optional<Integer> page,
                                      @RequestParam(defaultValue = "name") String sortBy,
                                      @RequestParam(defaultValue = "asc") String sortOrder) {
        Pageable paging = createPageable(page, sortBy, sortOrder);
        Page<Movie> watchlistMoviePage = movieService.getWatchlistMovies(currentUserProvider.getCurrentUserId(), paging);

        model.addAttribute("watchlistMovies", watchlistMoviePage);
        model.addAttribute("page", watchlistMoviePage.getNumber() + 1);
        model.addAttribute("totalWatchlist", watchlistMoviePage.getTotalElements());
        model.addAttribute("activePage", "watchlist");
        addPagingAttributes(model, paging, watchlistMoviePage.getTotalPages());

        return "list-watchlist-movies";
    }

    @GetMapping("/add")
    public String showAddForm(Movie movie, Model model) {
        model.addAttribute("activePage", "add");
        return "add-movie";
    }

    @PostMapping("/add")
    public String addMovie(Movie movie) {
        Movie saved = movieService.addMovie(currentUserProvider.getCurrentUserId(), movie);
        return getListRedirectUrl(saved);
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") UUID id, @RequestParam(required = false) String returnUrl, Model model) {
        Movie movie = movieService.getMovieForEdit(currentUserProvider.getCurrentUserId(), id);
        model.addAttribute("movie", movie);
        model.addAttribute("returnUrl", returnUrl);
        return "update-movie";
    }

    @PostMapping("/update/{id}")
    public String updateMovie(@PathVariable("id") UUID id, Movie movie) {
        Movie updated = movieService.updateMovie(currentUserProvider.getCurrentUserId(), id, movie);
        return getListRedirectUrl(updated);
    }

    @GetMapping("/delete/{id}")
    public String showDeleteForm(@PathVariable("id") UUID id, @RequestParam(required = false) String returnUrl, Model model) {
        Movie movie = movieService.getOwnedMovie(currentUserProvider.getCurrentUserId(), id);
        model.addAttribute("movie", movie);
        model.addAttribute("returnUrl", returnUrl);
        return "delete-movie";
    }

    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable("id") UUID id) {
        Movie movie = movieService.deleteMovie(currentUserProvider.getCurrentUserId(), id);
        return getListRedirectUrl(movie);
    }

    @GetMapping("/statistic")
    public String showStatistic(Model model) {
        model.addAttribute("moviesPerMonth", movieService.getMoviesPerMonth(currentUserProvider.getCurrentUserId()));
        model.addAttribute("activePage", "statistic");
        return "statistic";
    }

    @GetMapping("/user")
    public String showUser(Model model) {
        AppUser user = userService.getUser(currentUserProvider.getCurrentUserId());
        model.addAttribute("user", user);
        model.addAttribute("activePage", "user");
        return "user";
    }

    @PostMapping("/deleteUser")
    public String deleteUser(HttpServletRequest request, HttpServletResponse response) {
        userService.deleteAccount(currentUserProvider.getCurrentUserId());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, auth);

        return "redirect:/";
    }

    private String getListRedirectUrl(Movie movie) {
        return movie.isInWatchlist() ? "redirect:/watchlist" : "redirect:/movies";
    }

    static int normalizePageNumber(int page) {
        return Math.max(page, 1);
    }

    static AllowedSortField normalizeSortBy(String sortBy) {
        return AllowedSortField.fromString(sortBy);
    }

    static Direction normalizeSortOrder(String sortOrder) {
        return "asc".equalsIgnoreCase(sortOrder) ? Direction.ASC : Direction.DESC;
    }

    private Pageable createPageable(Optional<Integer> page, String sortBy, String sortOrder) {
        int currentPage = normalizePageNumber(page.orElse(1));
        AllowedSortField validatedSortField = normalizeSortBy(sortBy);
        Direction direction = normalizeSortOrder(sortOrder);

        Order order = new Order(direction, validatedSortField.property());
        return PageRequest.of(currentPage -1, PAGE_SIZE, Sort.by(order));
    }

    private void addPagingAttributes(Model model, Pageable paging, int pages) {
        model.addAttribute("totalPages", pages == 0 ? 1 : pages);
        model.addAttribute("size", PAGE_SIZE);
        model.addAttribute("sortBy", paging.getSort().get().findFirst().map(Order::getProperty).orElse("dateWatched"));
        model.addAttribute("sortOrder", paging.getSort().get().findFirst().map(Order::getDirection).orElse(Direction.DESC).name().toLowerCase());
    }
}
