package com.example.ewalletdemo.user.exception;

import com.example.ewalletdemo.user.util.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ExceptionAdvice {

    public static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<Object> genericRuntimeExceptionHandler(RuntimeException ex, WebRequest request) {
        logger.error("Runtime Exception: {0}", ex);
        return new ResponseEntity<>(ResponseBuilder.genericErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {FinalException.class})
    protected ResponseEntity<Object> finalExceptionHandler(FinalException ex, WebRequest request) {
        logger.error("Final Exception: {0}", ex);
        return new ResponseEntity<>(ResponseBuilder.genericErrorResponse(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> genericExceptionHandler(Exception ex, WebRequest request) {
        logger.error("Generic Exception: {0}", ex);
        return new ResponseEntity<>(ResponseBuilder.genericErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
