package com.example.plant.controller;
import com.example.plant.common.ApiResponse; import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/dashboard") public class DashboardController { private final com.example.plant.repository.UserRepository u; private final com.example.plant.repository.IntroductionRepository i; private final com.example.plant.repository.PlantRepository p;
 public DashboardController(com.example.plant.repository.UserRepository u,com.example.plant.repository.IntroductionRepository i,com.example.plant.repository.PlantRepository p){this.u=u;this.i=i;this.p=p;}
 @GetMapping("/stats") public ApiResponse<Map<String,Object>> stats(){return ApiResponse.ok(Map.of("users",u.count(),"introductions",i.count(),"pending",i.countPending(),"plants",p.count()));}
}
