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

import com.redis.testcontainers.RedisContainer;
import com.vmware.tanzu.demos.springflix.movies.model.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@Testcontainers
class TMDBMovieServiceTest {
    @Container
    @ServiceConnection
    static RedisContainer redis = new RedisContainer(RedisContainer.DEFAULT_IMAGE_NAME.withTag("7"));

    @Autowired
    private TMDBMovieService ms;

    @Test
    void testGetUpcomingMovies() {
        stubFor(get(urlEqualTo("/3/movie/upcoming?region=FR"))
                .willReturn(aResponse().withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "dates":{
                                    "maximum":"2023-10-27",
                                    "minimum":"2023-10-02"
                                  },
                                  "page":1,
                                  "results":[
                                    {
                                      "adult":false,
                                      "backdrop_path":"/f33XdT6dwNXmXQNvQ4FuyhQrUob.jpg",
                                      "genre_ids":[
                                        27
                                      ],
                                      "id":807172,
                                      "original_language":"en",
                                      "original_title":"The Exorcist: Believer",
                                      "overview":"Since the death of his wife 12 years ago, Victor Fielding has raised their daughter, Angela on his own. But when Angela and her friend Katherine disappear in the woods, only to return three days later with no memory of what happened to them, it unleashes a chain of events that will force Victor to confront the nadir of evil and, in his terror and desperation, seek out the only person alive who has witnessed anything like it before: Chris MacNeil.",
                                      "popularity":228.592,
                                      "poster_path":"/wGOxTAHx1iq3VZlYTFUbUfMhN5l.jpg",
                                      "release_date":"2023-10-06",
                                      "title":"The Exorcist: Believer",
                                      "video":false,
                                      "vote_average":4.3,
                                      "vote_count":3
                                    },
                                    {
                                      "adult":false,
                                      "backdrop_path":null,
                                      "genre_ids":[
                                        35
                                      ],
                                      "id":1182002,
                                      "original_language":"en",
                                      "original_title":"Fintech",
                                      "overview":"A workplace comedy about two tech bros running their company into the ground.",
                                      "popularity":22.712,
                                      "poster_path":"/po0keBfkanr14iEIYpkbNyaJLQG.jpg",
                                      "release_date":"2023-10-03",
                                      "title":"Fintech",
                                      "video":false,
                                      "vote_average":0,
                                      "vote_count":0
                                    }
                                  ],
                                  "total_pages":8,
                                  "total_results":142
                                }""")));

        final var m1 = new Movie("807172", "The Exorcist: Believer", LocalDate.of(2023, 10, 6));
        final var m2 = new Movie("1182002", "Fintech", LocalDate.of(2023, 10, 3));

        final var resp = ms.getUpcomingMovies("FR");
        assertThat(resp).containsExactlyInAnyOrder(m1, m2);
    }
}
