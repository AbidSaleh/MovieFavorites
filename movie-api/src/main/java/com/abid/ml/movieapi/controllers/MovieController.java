package com.abid.ml.movieapi.controllers;

import com.abid.ml.movieapi.models.Favorites;
import com.abid.ml.movieapi.models.Movie;
import com.abid.ml.movieapi.models.User;
import com.abid.ml.movieapi.payload.response.MessageResponse;
import com.abid.ml.movieapi.repository.FavoritesRepository;
import com.abid.ml.movieapi.repository.MovieRepository;
import com.abid.ml.movieapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class MovieController {
    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    @Autowired
    MovieRepository movieRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FavoritesRepository favoritesRepository;

    @GetMapping("/movies")
    public ResponseEntity<List<Movie>> searchOrPopular(
            @RequestParam(required = false, value = "search") String searchKey) {
        try{
            Optional<List<Movie>> searchResult;
            if(searchKey == null){
                searchResult = movieRepository.getPopularMovies();
            }else {
                searchResult =  movieRepository.searchMovieByText(searchKey.toLowerCase());
            }
            if(searchResult.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(searchResult.get(),HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> getMovieById(
            @PathVariable("id") long id) {
        Optional<Movie> movieData = movieRepository.findById(id);
        if (movieData.isPresent()) {
            return new ResponseEntity<>(movieData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/movies")
    @PreAuthorize("hasRole('MODERATOR')")
    public ResponseEntity<?> addMovies(@Valid @RequestBody Movie movie) {

        try {
            Movie _movie = movieRepository
                    .save(new Movie(movie.getTitle(),
                            movie.getGenre(),
                            movie.getReleaseYear(),
                            movie.getDescription()));
            return new ResponseEntity<>(_movie, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Movie>> getFavorite(Principal principal) {

        try{
            String username = principal.getName();
            Long currentUserId = userRepository.findIdByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("UserId Not Found with username: " + username));

            List<Favorites> favoritesList = favoritesRepository.findByUserId(currentUserId);

            if(favoritesList.isEmpty()){
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            List<Movie> movies = favoritesList.stream()
                    .map(favorites -> favorites.getMovie())
                    .collect(Collectors.toList());

            return new ResponseEntity<>(movies, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/favorites/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> saveFavorite(Principal principal,
                                                    @PathVariable("id") long movieId) {
        try{
            String username = principal.getName();
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

            Optional<Movie> movieToSave = movieRepository.findById(movieId);
            if(movieToSave.isEmpty()){
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: This Movie Id is not Valid : "+ movieId));
            }

            Favorites favorites = new Favorites(movieToSave.get(),currentUser);

            favoritesRepository.save(favorites);

            return new ResponseEntity<>("Added "+ favorites.getMovie().getTitle()+" to favorites !",HttpStatus.CREATED);

        }catch (Exception e){
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}