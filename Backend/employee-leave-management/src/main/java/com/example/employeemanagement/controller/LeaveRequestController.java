package com.example.employeemanagement.controller;
import com.example.employeemanagement.dto.*; import com.example.employeemanagement.service.LeaveRequestService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/leaves") @RequiredArgsConstructor public class LeaveRequestController {private final LeaveRequestService service;
 @GetMapping public List<LeaveResponse> all(){return service.all();}
 @GetMapping("/pending") public List<LeaveResponse> pending(){return service.pending();}
 @GetMapping("/my") public List<LeaveResponse> mine(Authentication a){return service.mine(a.getName());}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public LeaveResponse create(Authentication a,@Valid @RequestBody LeaveCreateRequest r){return service.create(a.getName(),r);}
 @PutMapping("/{id}/approve") public LeaveResponse approve(Authentication a,@PathVariable Long id,@RequestBody(required=false) LeaveDecisionRequest r){return service.decide(id,a.getName(),true,r);}
 @PutMapping("/{id}/reject") public LeaveResponse reject(Authentication a,@PathVariable Long id,@RequestBody(required=false) LeaveDecisionRequest r){return service.decide(id,a.getName(),false,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void cancel(Authentication a,@PathVariable Long id){service.cancel(id,a.getName());}
}
