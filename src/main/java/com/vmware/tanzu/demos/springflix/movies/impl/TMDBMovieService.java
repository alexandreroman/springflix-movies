/*
 * Copyright (c) 2023 VMware, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vmware.tanzu.demos.springflix.movies.impl;

import com.vmware.tanzu.demos.springflix.movies.model.Movie;
import com.vmware.tanzu.demos.springflix.movies.model.MovieService;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
class TMDBMovieService implements MovieService {
    private final Logger logger = LoggerFactory.getLogger(TMDBMovieService.class);
    private final TMDBClient client;
    private final ObservationRegistry observationRegistry;

    TMDBMovieService(TMDBClient client, ObservationRegistry observationRegistry) {
        this.client = client;
        this.observationRegistry = observationRegistry;
    }

    @Override
    @Cacheable(value = "movies.upcoming", key = "#region")
    public List<Movie> getUpcomingMovies(String region) {
        return Observation.createNotStarted("tmdb.upcomingMovies", observationRegistry)
                .lowCardinalityKeyValue("region", region)
                .observe(() -> doGetUpcomingMovies(region));
    }

    private List<Movie> doGetUpcomingMovies(String region) {
        final var resp = client.getUpcomingMovies(region);
        return resp.results().stream()
                .map(m -> new Movie(m.id(), m.title(), m.releaseDate()))
                .sorted(Comparator.comparing(Movie::releaseDate)).toList();
    }

    @Override
    @Cacheable(value = "movie", key = "#movieId")
    public Optional<Movie> getMovie(String movieId) {
        return Observation.createNotStarted("tmdb.movie", observationRegistry)
                .highCardinalityKeyValue("movie", movieId)
                .observe(() -> doGetMovie(movieId));
    }

    private Optional<Movie> doGetMovie(String movieId) {
        try {
            final var m = client.getMovie(movieId);
            return Optional.of(new Movie(m.id(), m.title(), m.releaseDate()));
        } catch (Exception e) {
            logger.warn("Failed to lookup movie: " + movieId);
            return Optional.empty();
        }
    }
}
