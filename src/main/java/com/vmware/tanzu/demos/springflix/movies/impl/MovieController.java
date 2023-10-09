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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class MovieController {
    private final Logger logger = LoggerFactory.getLogger(MovieController.class);
    private final MovieService movieService;

    MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping(value = "/api/v1/movies/upcoming", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Movie> getUpcomingMovies(@RequestParam(value = "region", defaultValue = "US") String region) {
        logger.info("Looking up upcoming movies in region: {}", region);
        return movieService.getUpcomingMovies(region);
    }

    @GetMapping(value = "/api/v1/movies/{movieId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Movie> getMovie(@PathVariable("movieId") String movieId) {
        logger.info("Looking up movie: {}", movieId);
        final var m = movieService.getMovie(movieId);
        return m.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
