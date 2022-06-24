package ru.yandex.yashop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.yandex.temp.model.Error;
import ru.yandex.yashop.exсeption.NoFoundException;
import ru.yandex.yashop.exсeption.ValidException;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler
        extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Error error = new Error();
        error.code(400);
        error.message("Validation Failed");
        return this.handleExceptionInternal(ex, error, headers, status, request);
    }

    @ExceptionHandler(value = {ValidException.class})
    protected ResponseEntity<Object> validException(RuntimeException ex, WebRequest request) {
        log.error("Ошибка при выполнении: ", ex);
        Error error = new Error();
        error.code(400);
        error.message("Validation Failed");
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.BAD_REQUEST,
                request);
    }

    @ExceptionHandler(value = {NoFoundException.class})
    protected ResponseEntity<Object> noFindException(RuntimeException ex, WebRequest request) {
        log.error("Ошибка при выполнении: ", ex);
        Error error = new Error();
        error.code(404);
        error.message("Item not found");
        return handleExceptionInternal(ex, error, new HttpHeaders(), HttpStatus.NOT_FOUND,
                request);
    }

}
