package com.reactivespring.controller;

import com.reactivespring.client.MovienInfoRestClient;
import com.reactivespring.client.ReviewsRestClient;
import com.reactivespring.domain.Movie;
import com.reactivespring.domain.MovieInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1/movies")
public class MoviesController {

    private MovienInfoRestClient movienInfoRestClient;
    private ReviewsRestClient reviewsRestClient;

    public MoviesController(MovienInfoRestClient movienInfoRestClient, ReviewsRestClient reviewsRestClient) {
        this.movienInfoRestClient = movienInfoRestClient;
        this.reviewsRestClient = reviewsRestClient;
    }

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovieById(@PathVariable("id")String movieId) {

        return movienInfoRestClient.retrieveMovieInfo(movieId)
                .flatMap(moviesInfo -> {
                    var reviewList = reviewsRestClient.retrieveReviews(movieId)
                            .collectList();

                    return reviewList.map(
                            reviews -> new Movie(moviesInfo,reviews));
                });
    }


}
