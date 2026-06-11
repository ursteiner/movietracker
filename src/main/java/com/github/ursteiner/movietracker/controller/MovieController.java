package com.github.ursteiner.movietracker.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.github.ursteiner.movietracker.model.Movie;
import com.github.ursteiner.movietracker.model.MoviesPerMonthDTO;
import com.github.ursteiner.movietracker.repository.MovieRepository;
import com.github.ursteiner.movietracker.repository.UserRepository;
import com.github.ursteiner.movietracker.service.StreamingUrlService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class MovieController {

    private static final int PAGE_SIZE = 15;

    private final MovieRepository movieRepository;
    private final StreamingUrlService streamingUrlService;
    private final UserRepository userRepository;

    @Autowired
    public MovieController(MovieRepository movieRepository, StreamingUrlService streamingUrlService, UserRepository userRepository){
        this.movieRepository = movieRepository;
        this.streamingUrlService = streamingUrlService;
        this.userRepository = userRepository;
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

        UUID currentUserId = getCurrentUserId();

        Pageable paging = createPageable(page, sortBy, sortOrder);
        Page<Movie> moviePage;
        if(searchName != null) {
            moviePage = movieRepository.findByUserIdAndNameContainingIgnoreCaseAndDateWatchedIsNotNull(currentUserId, searchName, paging);
        }else{
            moviePage = movieRepository.findByUserIdAndDateWatchedIsNotNull(currentUserId, paging);
        }

        fillStreamingUrl(moviePage.getContent());

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
        UUID currentUser = getCurrentUserId();

        Pageable paging = createPageable(page, sortBy, sortOrder);
        Page<Movie> watchlistMoviePage = movieRepository.findByUserIdAndDateWatchedIsNull(currentUser, paging);
        fillStreamingUrl(watchlistMoviePage.getContent());

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
        UUID currentUserId = getCurrentUserId();

        movie.setMovieId(streamingUrlService.getMovieId(movie.getStreamingUrl()));
        movie.setStreamingService(streamingUrlService.getServiceName(movie.getStreamingUrl()));
        movie.setUser(userRepository.findById(currentUserId).orElseThrow(() -> new EntityNotFoundException("User not found with id: " + currentUserId)));
        movieRepository.save(movie);
        return getListRedirectUrl(movie);
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") UUID id, @RequestParam(required = false) String returnUrl, Model model) {
        UUID currentUserId = getCurrentUserId();
        Movie movie = movieRepository.findByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        fillStreamingUrl(movie);
        model.addAttribute("movie", movie);
        model.addAttribute("returnUrl", returnUrl);
        return "update-movie";
    }

    @PostMapping("/update/{id}")
    public String updateMovie(@PathVariable("id") UUID id, Movie movie) {
        UUID currentUserId = getCurrentUserId();
        Movie foundMovie = movieRepository.findByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        foundMovie.setName(movie.getName());
        foundMovie.setDateWatched(movie.getDateWatched());
        foundMovie.setMovieId(streamingUrlService.getMovieId(movie.getStreamingUrl()));
        foundMovie.setStreamingService(streamingUrlService.getServiceName(movie.getStreamingUrl()));

        movieRepository.save(foundMovie);
        return getListRedirectUrl(foundMovie);
    }

    @GetMapping("/delete/{id}")
    public String showDeleteForm(@PathVariable("id") UUID id, @RequestParam(required = false) String returnUrl, Model model) {
        UUID currentUserId = getCurrentUserId();
        Movie movie = movieRepository.findByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        model.addAttribute("movie", movie);
        model.addAttribute("returnUrl", returnUrl);
        return "delete-movie";
    }

    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable("id") UUID id) {
        UUID currentUserId = getCurrentUserId();
        Movie movie = movieRepository.findByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        movieRepository.delete(movie);
        return getListRedirectUrl(movie);
    }

    @GetMapping("/statistic")
    public String showStatistic(Model model) {
        UUID currentUser = getCurrentUserId();
        List<MoviesPerMonthDTO> moviesPerMonth = movieRepository.countMoviesWatchedPerYearMonthNative(currentUser);
        model.addAttribute("moviesPerMonth", moviesPerMonth);
        model.addAttribute("activePage", "statistic");
        return "statistic";
    }

    @GetMapping("/user")
    public String showUser() {
        return "user";
    }

    private String getListRedirectUrl(Movie movie) {
        return movie.isInWatchlist() ? "redirect:/watchlist" : "redirect:/movies";
    }

    private void fillStreamingUrl(List<Movie> movies) {
        movies.forEach(this::fillStreamingUrl);
    }

    private void fillStreamingUrl(Movie movie) {
        movie.setStreamingUrl(streamingUrlService.getMovieWatchUrl(movie.getStreamingService(), movie.getMovieId()));
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

    public static UUID getCurrentUserId() {
       OAuth2User currentUser = getCurrentUser();
       if (currentUser != null) {
           return currentUser.getAttribute("appUserId");
       }
       return null;
    }

    public static OAuth2User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof OAuth2User) {
            return (OAuth2User) principal;
        }
        return null;
    }
}
