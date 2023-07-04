package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    private MovieInfoService movieInfoService;


    public MoviesInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }


    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) String year) {

        log.info("Year is : {}", year);

        if(year!=null){
            return movieInfoService.getMovieInfoByYear(year);
        }



        return movieInfoService.getAllMovies().log();
    }

    @GetMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfos(@PathVariable String id) {
        return movieInfoService.getMovie(id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return movieInfoService.addMovieInfo(movieInfo).log();
    }

    @PutMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody MovieInfo updatedMovieInfo, @PathVariable String id) {
        return movieInfoService.updateMovieInfo(updatedMovieInfo,id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id) {
        return movieInfoService.deleteMovieInfo(id).log();
    }
}
