package org.libmanager.server.controller;

import org.libmanager.server.response.Response;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class ExceptionController {

    @ExceptionHandler(value = Exception.class)
    public Response<Boolean> handleException(Exception e) {
        Response<Boolean> response = null;
        if (e instanceof DataIntegrityViolationException) {
            return new Response<>(Response.Code.INTEGRITY_VIOLATION, false);
        }
        return response;
    }

}
