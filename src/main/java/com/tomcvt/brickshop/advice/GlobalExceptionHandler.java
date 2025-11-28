package com.tomcvt.brickshop.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.tomcvt.brickshop.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        log.error("ResponseStatusException: {}", ex);
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.error("UserAlreadyExistsException: {}", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(EntityAlreadyExists.class)
    public ResponseEntity<String> handleEntityAlreadyExists(EntityAlreadyExists ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<String> handleFileUploadException(FileUploadException ex) {
        log.error("File upload exception: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<String> handleEmptyCartException(EmptyCartException ex) {
        log.warn("EmptyCartException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleNoSuchEntityExists(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(NoOrderForSessionException.class)
    public ResponseEntity<String> handleNoOrderForSessionException(NoOrderForSessionException ex) {
        log.error("NoOrderForSessionException: {}", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
    }
    @ExceptionHandler(NotInStockException.class)
    public ResponseEntity<String> handleNotInStockException(NotInStockException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<String> handleNotAuthorizedException(NotAuthorizedException ex) {
        log.error("NotAuthorizedException: {}", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
    @ExceptionHandler(WrongOperationException.class)
    public ResponseEntity<String> handleWrongOperationException(WrongOperationException ex) {
        log.error("WrongOperationException: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    @ExceptionHandler(OwnershipMismatchException.class)
    public ResponseEntity<String> handleOwnershipMismatchException(OwnershipMismatchException ex) {
        log.error("OwnershipMismatchException: {}", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
        log.error("NotFoundException: {}", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
