package com.github.ursteiner.movietracker.controller;

import com.github.ursteiner.movietracker.model.Movie;
import com.github.ursteiner.movietracker.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
    public String listMovies(Model model) {
        model.addAttribute("movies", movieRepository.findAll(Sort.by(Sort.Direction.DESC, "dateWatched")));
        model.addAttribute("activePage", "list");
        return "list-movies";
    }

    @GetMapping("/add")
    public String showAddForm(Movie movie, Model model) {
        model.addAttribute("activePage", "add");
        return "add-movie";
    }

    @PostMapping("/add")
    public String addIMovie(Movie movie) {
        movieRepository.save(movie);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable("id") long id, Model model) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
        model.addAttribute("movie", movie);
        return "update-movie";
    }

    @PostMapping("/update/{id}")
    public String updateMovie(@PathVariable("id") long id, Movie movie) {
        movie.setId(id);
        movieRepository.save(movie);
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String showDeleteForm(@PathVariable("id") long id, Model model) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid movie Id:" + id));
        model.addAttribute("movie", movie);
        return "delete-movie";
    }

    @PostMapping("/delete/{id}")
    public String deleteMovie(@PathVariable("id") long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id:" + id));
        movieRepository.delete(movie);
        return "redirect:/";
    }

    @GetMapping("/search")
    public String showSearchResults(@RequestParam String searchName, Model model) {
        model.addAttribute("movies", movieRepository.findByNameStartingWithIgnoreCaseOrderByDateWatchedDesc(searchName));
        model.addAttribute("activePage", "list");
        model.addAttribute("searchName", "");
        return "list-movies";
    }
}
