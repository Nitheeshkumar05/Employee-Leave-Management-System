package com.example.employeemanagement.exception;

import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class) ResponseEntity<?> notFound(ResourceNotFoundException e){return body(404,e.getMessage());}
    @ExceptionHandler(BadRequestException.class) ResponseEntity<?> bad(BadRequestException e){return body(400,e.getMessage());}
    @ExceptionHandler(BadCredentialsException.class) ResponseEntity<?> credentials(){return body(401,"Invalid email or password");}
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<?> validation(MethodArgumentNotValidException e){
        Map<String,String> fields=new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(x->fields.put(x.getField(),x.getDefaultMessage()));
        Map<String,Object> b=new LinkedHashMap<>(); b.put("timestamp",LocalDateTime.now()); b.put("status",400); b.put("error","Validation failed"); b.put("fields",fields); return ResponseEntity.badRequest().body(b);
    }
    @ExceptionHandler(Exception.class) ResponseEntity<?> generic(Exception e){return body(500,"An unexpected error occurred");}
    private ResponseEntity<?> body(int status,String message){Map<String,Object>b=new LinkedHashMap<>();b.put("timestamp",LocalDateTime.now());b.put("status",status);b.put("error",message);return ResponseEntity.status(status).body(b);}
}
