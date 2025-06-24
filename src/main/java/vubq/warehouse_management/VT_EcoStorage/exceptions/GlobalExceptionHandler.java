package vubq.warehouse_management.VT_EcoStorage.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.QueryTimeoutException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;
import vubq.warehouse_management.VT_EcoStorage.utils.https.ResponseCode;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response handleAllExceptions(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        if (ex.getMessage().equalsIgnoreCase("Access Denied")) {
            return Response.badRequest("Access Denied");
        }
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
        List<ValidationErrorDto> violations = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ))
                .entrySet()
                .stream()
                .map(entry -> ValidationErrorDto.builder()
                        .field(entry.getKey())
                        .messages(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return Response.badRequest("Validation failed").data(violations).show(false);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    public Response handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation at {}: {}", request.getRequestURI(), ex.getMessage());
        String message = "Data integrity violation at " + request.getRequestURI() + ": " + ex.getMessage();
        if (ex.getMessage() != null && ex.getMessage().contains("tb_users_unique")) {
            message = "Username already exists";
        }
        return Response.badRequest(message);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseBody
    public Response handleBadSql(BadSqlGrammarException ex, HttpServletRequest request) {
        log.error("Bad SQL at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        String message = "Bad SQL at " + request.getRequestURI() + ": " + ex.getMessage();
        return Response.error(ResponseCode.INTERNAL_ERROR, message);
    }

    @ExceptionHandler(QueryTimeoutException.class)
    @ResponseBody
    public Response handleQueryTimeout(QueryTimeoutException ex, HttpServletRequest request) {
        log.warn("Query timeout at {}: {}", request.getRequestURI(), ex.getMessage());
        String message = "Query timeout at " + request.getRequestURI() + ": " + ex.getMessage();
        return Response.error(ResponseCode.INTERNAL_ERROR, message);
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    @ResponseBody
    public Response handleDataAccessResourceFailure(DataAccessResourceFailureException ex, HttpServletRequest request) {
        log.error("Database resource failure at {}: {}", request.getRequestURI(), ex.getMessage());
        String message = "Database resource failure at " + request.getRequestURI() + ": " + ex.getMessage();
        return Response.error(ResponseCode.INTERNAL_ERROR, message);
    }

    @ExceptionHandler(TransientDataAccessResourceException.class)
    @ResponseBody
    public Response handleTransientDataAccess(TransientDataAccessResourceException ex, HttpServletRequest request) {
        log.warn("Transient DB error at {}: {}", request.getRequestURI(), ex.getMessage());
        String message = "Transient DB error at " + request.getRequestURI() + ": " + ex.getMessage();
        return Response.error(ResponseCode.INTERNAL_ERROR, message);
    }

    @ExceptionHandler(DataRetrievalFailureException.class)
    @ResponseBody
    public Response handleDataRetrievalFailure(DataRetrievalFailureException ex, HttpServletRequest request) {
        log.warn("Data retrieval failed at {}: {}", request.getRequestURI(), ex.getMessage());
        String message = "Data retrieval failed at " + request.getRequestURI() + ": " + ex.getMessage();
        return Response.error(ResponseCode.INTERNAL_ERROR, message);
    }
}
