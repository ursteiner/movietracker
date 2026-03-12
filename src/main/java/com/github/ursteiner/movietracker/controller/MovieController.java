package com.github.ursteiner.movietracker.controller;

import java.util.List;
import java.util.Optional;

import com.github.ursteiner.movietracker.model.Movie;
import com.github.ursteiner.movietracker.repository.MovieRepository;
import com.github.ursteiner.movietracker.service.StreamingUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MovieController {

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private StreamingUrlService streamingUrlService;
    
    @GetMapping("/")
    public String listMovies(Model model,
                             @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             @RequestParam(required = false) String searchName,
                             @RequestParam(defaultValue = "dateWatched") String sortBy,
                             @RequestParam(defaultValue = "desc") String sortOrder) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(15);

        Direction direction = sortOrder.equals("desc") ? Direction.DESC : Direction.ASC;
        Order order = new Order(direction,sortBy);

        Pageable paging = PageRequest.of(currentPage -1, pageSize, Sort.by(order));
        Page<Movie> moviePage;
        if(searchName != null) {
            moviePage = movieRepository.findByNameContainingIgnoreCaseAndInWatchlistFalse(searchName, paging);
        }else{
            moviePage = movieRepository.findByInWatchlistFalse(paging);
        }

        fillStreamingUrl(moviePage.getContent());

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("page", moviePage.getNumber() + 1);
        model.addAttribute("totalMovies", moviePage.getTotalElements());
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("size", pageSize);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("activePage", "list");
        model.addAttribute("searchName", searchName);

        return "list-movies";
    }

    @GetMapping("/watchlist")
    public String listWatchlistMovies(Model model) {
        List<Movie> watchlistMovies = movieRepository.findByInWatchlistTrueOrderByNameAsc();
        fillStreamingUrl(watchlistMovies);

        model.addAttribute("watchlistMovies", watchlistMovies);
        model.addAttribute("activePage", "watchlist");
        return "list-watchlist-movies";
    }

    @GetMapping("/add")
    public String showAddForm(Movie movie, Model model) {
        model.addAttribute("activePage", "add");
        return "add-movie";
    }

    @PostMapping("/add")
    public String addIMovie(Movie movie) {
        movie.setMovieId(streamingUrlService.getMovieId(movie.getStreamingUrl()));
        movie.setStreamingService(streamingUrlService.getServiceName(movie.getStreamingUrl()));
        movieRepository.save(movie);
        return getListRedirectUrl(movie);
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, @RequestParam(required = false) String returnUrl, Model model) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id: " + id));
        fillStreamingUrl(movie);
        model.addAttribute("movie", movie);
        model.addAttribute("returnUrl", returnUrl);
        return "update-movie";
    }

    @PostMapping("/update/{id}")
    public String updateMovie(@PathVariable("id") long id, Movie movie) {
        movie.setId(id);
        movie.setMovieId(streamingUrlService.getMovieId(movie.getStreamingUrl()));
        movie.setStreamingService(streamingUrlService.getServiceName(movie.getStreamingUrl()));
        movieRepository.save(movie);
        return getListRedirectUrl(movie);
    }

    @GetMapping("/delete/{id}")
    public String showDeleteForm(@PathVariable("id") long id, @RequestParam(required = false) String returnUrl, Model model) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id: " + id));
        model.addAttribute("movie", movie);
        model.addAttribute("returnUrl", returnUrl);
        return "delete-movie";
    }

    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable("id") long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id: " + id));
        movieRepository.delete(movie);
        return getListRedirectUrl(movie);
    }

    @GetMapping("/statistic")
    public String showStatistic(Model model) {
        List<Object[]> moviesPerMonth = movieRepository.countMoviesWatchedPerYearMonthNative();
        model.addAttribute("moviesPerMonth", moviesPerMonth);
        return "statistic";
    }

    private String getListRedirectUrl(Movie movie) {
        if(movie.getInWatchlist()) {
            return "redirect:/watchlist";
        }else {
            return "redirect:/";
        }
    }

    private void fillStreamingUrl(List<Movie> movies) {
        movies.forEach(this::fillStreamingUrl);
    }

    private void fillStreamingUrl(Movie movie) {
        movie.setStreamingUrl(streamingUrlService.getMovieWatchUrl(movie.getStreamingService(), movie.getMovieId()));
    }
}
