package com.example.plant.controller;
import com.example.plant.common.ApiResponse; import com.example.plant.dto.ReviewRequest; import com.example.plant.model.IntroductionRecord; import com.example.plant.service.IntroductionService; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*;
import javax.validation.Valid; import java.util.*;
@RestController @RequestMapping("/api/reviews") public class ReviewController { private final IntroductionService service; private final com.example.plant.repository.UserRepository users; public ReviewController(IntroductionService service,com.example.plant.repository.UserRepository users){this.service=service;this.users=users;}
 @GetMapping("/pending") public ApiResponse<List<IntroductionRecord>> pending(){return ApiResponse.ok(service.pending());}
 @PostMapping("/{id}") public ApiResponse<Map<String,Object>> review(@PathVariable Long id,Authentication a,@Valid @RequestBody ReviewRequest r){return ApiResponse.ok(service.review(id,users.findByUsername(a.getName()).orElseThrow().getId(),r.result(),r.comment()));}
}
