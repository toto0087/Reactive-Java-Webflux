package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;


import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {

        return route()
                .POST("/v1/reviews", request -> reviewHandler.addReview(request))
                .GET("/v1/reviews", request -> reviewHandler.getReview(request))
                .PUT("/v1/reviews/{id}",request -> reviewHandler.updateReview(request))
                .DELETE("/v1/reviews/{id}",request -> reviewHandler.deleteReview(request))
                .GET("/stream",(request -> reviewHandler.getReviewsStream(request)))
                .build();
    }
}
