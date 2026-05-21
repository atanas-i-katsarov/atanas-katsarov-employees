package com.atanaskatsarov.employees.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.atanaskatsarov.employees.exception.InvalidCsvException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  
    @ExceptionHandler(InvalidCsvException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleInvalidCsv(InvalidCsvException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }
}
