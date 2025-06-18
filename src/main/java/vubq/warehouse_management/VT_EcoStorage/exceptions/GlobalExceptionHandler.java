package vubq.warehouse_management.VT_EcoStorage.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return Response.internalError();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Response handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Invalid input at {}: {}", request.getRequestURI(), ex.getMessage());
        return Response.badRequest(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Response handleInvalidJson(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("Invalid JSON at {}: {}", request.getRequestURI(), ex.getMessage());
        return Response.badRequest("Invalid JSON input");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Response handleValidationErrors(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Invalid input");

        return Response.badRequest(errorMsg);
    }
}
