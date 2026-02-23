package com.pm.groceryshop.controller;

import com.pm.groceryshop.service.exception.InsufficientStockException;
import com.pm.groceryshop.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void handleNotFound_returns404() {
        ResponseEntity<Map<String, Object>> response = handler.handleNotFound(new ResourceNotFoundException("missing"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("missing", response.getBody().get("message"));
    }

    @Test
    void handleInsufficient_returns409() {
        ResponseEntity<Map<String, Object>> response = handler.handleInsufficient(new InsufficientStockException("low stock"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("low stock", response.getBody().get("message"));
    }

    @Test
    void handleGeneric_returns500() {
        ResponseEntity<Map<String, Object>> response = handler.handleGeneric(new RuntimeException("boom"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("boom", response.getBody().get("message"));
    }
}
