package com.example.plant.controller;
import com.example.plant.common.ApiResponse; import com.example.plant.dto.*; import com.example.plant.model.IntroductionRecord; import com.example.plant.service.IntroductionService; import org.springframework.web.bind.annotation.*;
import javax.validation.Valid; import java.util.*;
@RestController @RequestMapping("/api/introductions") public class IntroductionController { private final IntroductionService service; public IntroductionController(IntroductionService service){this.service=service;}
 @GetMapping public ApiResponse<List<IntroductionRecord>> list(org.springframework.security.core.Authentication a,@RequestParam(required=false) String status,@RequestParam(required=false) String keyword){return ApiResponse.ok(service.list(a.getName(),status,keyword));}
 @PostMapping public ApiResponse<IntroductionRecord> create(org.springframework.security.core.Authentication a,@Valid @RequestBody IntroductionRequest r){return ApiResponse.ok(service.create(a.getName(),r));}
 @PutMapping("/{id}") public ApiResponse<IntroductionRecord> update(@PathVariable Long id,org.springframework.security.core.Authentication a,@Valid @RequestBody IntroductionRequest r){return ApiResponse.ok(service.update(id,a.getName(),r));}
 @DeleteMapping("/{id}") public ApiResponse<Void> delete(@PathVariable Long id,org.springframework.security.core.Authentication a){service.delete(id,a.getName());return ApiResponse.ok(null);}
}
