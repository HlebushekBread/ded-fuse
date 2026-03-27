package net.softloaf.ded_fuse.exception;

import net.softloaf.ded_fuse.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException e) {
        ErrorResponse error = new ErrorResponse(
                e.getStatusCode().value(),
                e.getReason()
        );

        return new ResponseEntity<>(error, e.getStatusCode());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        ErrorResponse error = new ErrorResponse(500, e.getMessage());
        return ResponseEntity.internalServerError().body(error);
    }
}
