package com.abid.ml.movieapi.repository;

import com.abid.ml.movieapi.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    @Query(value = "SELECT m from Movie m WHERE LOWER(m.title) LIKE %?1%")
    Optional<List<Movie>> searchMovieByText(String searchKeyInLowerCase);

    Optional<Movie> findById(Long id);

    @Query(value = "SELECT DISTINCT m from Movie m INNER JOIN Favorites f ON m = f.movie")
    Optional<List<Movie>> getPopularMovies();
}
