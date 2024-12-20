package com.javarest.socks.exception;

import com.javarest.socks.exception.exceptions.*;
import com.javarest.socks.exception.response.ErrorResponse;
import org.apache.poi.EmptyFileException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.javarest.socks.exception.constant.ErrorMessage.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SocksNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleSocksNotFoundException() {
        ErrorResponse response = new ErrorResponse(SOCKS_NOT_FOUND.getMsg());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException() {
        ErrorResponse response = new ErrorResponse(INSUFFICIENT_STOCK.getMsg());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InvalidFileFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidFileFormatException() {
        ErrorResponse response = new ErrorResponse(INVALID_FILE_FORMAT.getMsg());
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(response);
    }

    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ErrorResponse> handleFileProcessingException() {
        ErrorResponse response = new ErrorResponse(FILE_PROCESSING.getMsg());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    @ExceptionHandler({UnsupportedOperationException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleUnsupportedOperatorException() {
        ErrorResponse response = new ErrorResponse(UNSUPPORTED_OPERATOR.getMsg());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    @ExceptionHandler(EmptyFileException.class)
    public ResponseEntity<ErrorResponse> handleEmptyFileException() {
        ErrorResponse response = new ErrorResponse(EMPTY_FILE.getMsg());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(NoFilterParametersException.class)
    public ResponseEntity<ErrorResponse> handleNoFilterParametersException() {
        ErrorResponse response = new ErrorResponse(NO_FILTERS.getMsg());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    @ExceptionHandler({InvalidSortDirectionException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleInvalidSortDirectionException() {
        ErrorResponse response = new ErrorResponse(INVALID_SORT_DIRECTION.getMsg());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e) {
        ErrorResponse response = new ErrorResponse(GENERIC_ERROR.getMsg());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
