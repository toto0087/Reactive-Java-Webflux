package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    static String REVIEWS_URL = "/v1/reviews";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewReactiveRepository;

    @BeforeEach
    void setUp() {
        var reviewsList = List.of(
                new Review("A", 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));
        reviewReactiveRepository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewReactiveRepository.deleteAll().block();
    }

    @Test
    void addReview() {

        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        //when
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {

                    var savedReview = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedReview!=null;
                    assert savedReview.getReviewId()!=null;
                });

        //then
    }


    @Test
    void getReview() {
        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateReview() {
        //given
        var id = "A";
        var movieInfo = new Review(null, 1L, "Awesome Movie1", 9.0);

        //when
        webTestClient
                .put()
                .uri(REVIEWS_URL+"/{id}",id)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(movieInfoEntityExchangeResult -> {

                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updatedMovieInfo!=null;
                    assert updatedMovieInfo.getReviewId()!=null;
                    assertEquals("Awesome Movie1",updatedMovieInfo.getComment());
                });
    }

    @Test
    void deleteReview() {
        var id = "A";
        webTestClient
                .delete()
                .uri(REVIEWS_URL+"/{movieInfoId}",id)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void getAllReviews() {
        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void getReviewsByMovieInfoId() {
        //given

        //when
        webTestClient
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder.path(REVIEWS_URL)
                            .queryParam("movieInfoId", "1")
                            .build();
                })
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviewList -> {
                    System.out.println("reviewList : " + reviewList);
                    assertEquals(2, reviewList.size());
                });

    }
}
