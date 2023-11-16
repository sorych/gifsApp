package com.sorych.gifsapp.controller;

import java.util.concurrent.CompletionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientException;

@ControllerAdvice
public class CustomExceptionHandler {

  @ExceptionHandler(RestClientException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public ResponseEntity handleRestClientException(RestClientException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Error while contacting Giphy API");
  }

  @ExceptionHandler(CompletionException.class)
  @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
  public ResponseEntity handleRestClientException(CompletionException ex) {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Error while contacting Giphy API");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity handleGenericException(Exception ex) {
    return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

