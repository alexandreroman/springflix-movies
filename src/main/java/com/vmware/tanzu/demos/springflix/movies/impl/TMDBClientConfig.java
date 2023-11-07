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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration(proxyBeanMethods = false)
class TMDBClientConfig {
    @Bean
    TMDBClient tmdbClient(
            RestClient.Builder rcb,
            @Value("${app.tmdb.api.url}") String apiUrl,
            @Value("${app.tmdb.api.key}") String apiKey,
            @Value("${app.client.connectTimeout}") Duration connectTimeout,
            @Value("${app.client.readTimeout}") Duration readTimeout,
            @Value("${spring.application.name}") String appName) {
        final var client = rcb.clone()
                .baseUrl(apiUrl)
                .requestFactory(ClientHttpRequestFactories.get(
                        ClientHttpRequestFactorySettings.DEFAULTS
                                .withConnectTimeout(connectTimeout)
                                .withReadTimeout(readTimeout)))
                .defaultHeaders(headers -> {
                    headers.set(HttpHeaders.USER_AGENT, appName);
                    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
                })
                .build();

        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(client))
                .build()
                .createClient(TMDBClient.class);
    }
}
