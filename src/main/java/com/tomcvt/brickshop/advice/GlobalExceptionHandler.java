package com.tomcvt.brickshop.advice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.tomcvt.brickshop.dto.ErrorResponse;
import com.tomcvt.brickshop.exception.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    //TODO refactor to reuturn standard error response body
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        log.error("ResponseStatusException: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(
            new ErrorResponse(ex.getStatusCode().toString(), ex.getReason()));
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        log.error("UserAlreadyExistsException: {}", ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            new ErrorResponse("CONFLICT", ex.getMessage())
        );
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("BAD_REQUEST", ex.getMessage())
        );
    }
    @ExceptionHandler(EntityAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handleEntityAlreadyExists(EntityAlreadyExists ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
            new ErrorResponse("CONFLICT", ex.getMessage())
        );
    }
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException ex) {
        log.error("File upload exception: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("BAD_REQUEST", ex.getMessage())
        );
    }
    @ExceptionHandler(EmptyCartException.class)
    public ResponseEntity<ErrorResponse> handleEmptyCartException(EmptyCartException ex) {
        log.warn("EmptyCartException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("BAD_REQUEST", ex.getMessage())
        );
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchEntityExists(ProductNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse("NOT_FOUND", ex.getMessage())
        );
    }
    @ExceptionHandler(NoOrderForSessionException.class)
    public ResponseEntity<ErrorResponse> handleNoOrderForSessionException(NoOrderForSessionException ex) {
        log.error("NoOrderForSessionException: {}", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse("NOT_FOUND", ex.getMessage())
        );
    }
    @ExceptionHandler(NotInStockException.class)
    public ResponseEntity<ErrorResponse> handleNotInStockException(NotInStockException ex) {
        log.error("NotInStockException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("BAD_REQUEST", ex.getMessage())
        );
    }
    @ExceptionHandler(NotAuthorizedException.class)
    public ResponseEntity<ErrorResponse> handleNotAuthorizedException(NotAuthorizedException ex) {
        log.error("NotAuthorizedException: {}", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            new ErrorResponse("FORBIDDEN", ex.getMessage())
        );
    }
    @ExceptionHandler(WrongOperationException.class)
    public ResponseEntity<ErrorResponse> handleWrongOperationException(WrongOperationException ex) {
        log.error("WrongOperationException: {}", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("BAD_REQUEST", ex.getMessage())
        );
    }
    @ExceptionHandler(OwnershipMismatchException.class)
    public ResponseEntity<ErrorResponse> handleOwnershipMismatchException(OwnershipMismatchException ex) {
        log.error("OwnershipMismatchException: {}", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse("NOT_FOUND", ex.getMessage())
        );
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        log.error("NotFoundException: {}", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse("NOT_FOUND", ex.getMessage())
        );
    }
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceededException(RateLimitExceededException ex) {
        log.error("RateLimitExceededException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
            new ErrorResponse("TOO_MANY_REQUESTS", ex.getMessage())
        );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unhandled exception: {}", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            new ErrorResponse("INTERNAL_SERVER_ERROR", "An unexpected error occurred.")
        );
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex) {
        log.error("RequestMethodNotSupportedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            new ErrorResponse("METHOD_NOT_ALLOWED", "The request method is not supported for this endpoint.")
        );
    }
    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        log.error("AuthorizationDeniedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            new ErrorResponse("FORBIDDEN", ex.getMessage())
        );
    }
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("NoResourceFoundException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse("NOT_FOUND", ex.getMessage())
        );
    }
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException ex) {
        log.error("MultipartException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("BAD_REQUEST", ex.getMessage())
        );
    }
    @ExceptionHandler(RequestRejectedException.class)
    public ResponseEntity<ErrorResponse> handleRequestRejectedException(RequestRejectedException ex) {
        log.error("RequestRejectedException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse("BAD_REQUEST", ex.getMessage())
        );
    }
}
