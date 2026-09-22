package com.example.employeemanagement.controller;
import com.example.employeemanagement.dto.EmployeeRequest; import com.example.employeemanagement.model.Employee; import com.example.employeemanagement.service.EmployeeService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/employees") @RequiredArgsConstructor
public class EmployeeController {private final EmployeeService service;
 @GetMapping public List<Employee> all(@RequestParam(required=false)String search){return service.findAll(search);}
 @GetMapping("/me") public Employee me(Authentication a){return service.findByEmail(a.getName());}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public Employee create(@Valid @RequestBody EmployeeRequest r){return service.create(r);}
 @PutMapping("/{id}") public Employee update(@PathVariable Long id,@Valid @RequestBody EmployeeRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
}
