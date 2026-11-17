package com.quanta.bom.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebInputException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiResponseOut<Void>> handleApplication(ApplicationException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ApiResponseFactory.error(ex.getMessage()));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ApiResponseOut<Void>> handleInput(ServerWebInputException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseFactory.error("La solicitud es inválida: revise el cuerpo o los parámetros enviados"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseOut<Void>> handleUnexpected(Exception ex) {
        LOG.error("Error inesperado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseFactory.error("Ocurrió un error interno. Intente nuevamente más tarde"));
    }
}
