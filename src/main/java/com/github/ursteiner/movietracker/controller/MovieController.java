package com.github.ursteiner.movietracker.controller;

import java.util.Optional;

import com.github.ursteiner.movietracker.model.Movie;
import com.github.ursteiner.movietracker.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    
    @GetMapping("/")
    public String listMovies(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size, @RequestParam(required = false) String searchName) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(15);
        
        Pageable paging = PageRequest.of(currentPage -1, pageSize);
        Page<Movie> moviePage;
        if(searchName != null) {
            moviePage = movieRepository.findByNameStartingWithIgnoreCaseAndInWatchlistFalseOrderByDateWatchedDesc(searchName, paging);
        }else{
            moviePage = movieRepository.findByInWatchlistFalseOrderByDateWatchedDesc(paging);
        }
        
        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("page", moviePage.getNumber() + 1);
        model.addAttribute("totalMovies", moviePage.getTotalElements());
        model.addAttribute("totalPages", moviePage.getTotalPages());
        model.addAttribute("size", pageSize);
        model.addAttribute("activePage", "list");
        return "list-movies";
    }

    @GetMapping("/watchlist")
    public String listWatchlistMovies(Model model) {
        model.addAttribute("watchlistMovies", movieRepository.findByInWatchlistTrueOrderByNameAsc());
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
        movieRepository.save(movie);
        return getListRedirectUrl(movie);
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, @RequestParam(required = false) String returnUrl, Model model) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id: " + id));
        model.addAttribute("movie", movie);
        model.addAttribute("returnUrl", returnUrl);
        return "update-movie";
    }

    @PostMapping("/update/{id}")
    public String updateMovie(@PathVariable("id") long id, Movie movie) {
        movie.setId(id);
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
    
    private String getListRedirectUrl(Movie movie) {
        if(movie.getInWatchlist()) {
            return "redirect:/watchlist";
        }else {
            return "redirect:/";
        }
    }
}
