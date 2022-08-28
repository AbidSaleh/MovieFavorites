package com.abid.ml.movieapi.repository;

import com.abid.ml.movieapi.models.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites,Long> {
    List<Favorites> findByUserId(Long userId);
}
