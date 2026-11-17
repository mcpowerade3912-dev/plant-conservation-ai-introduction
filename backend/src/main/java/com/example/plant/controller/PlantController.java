package com.example.plant.controller;
import com.example.plant.common.ApiResponse; import com.example.plant.dto.PlantRequest; import com.example.plant.model.Plant; import com.example.plant.service.PlantService; import org.springframework.web.bind.annotation.*;
import javax.validation.Valid; import java.util.*;
@RestController @RequestMapping("/api/plants") public class PlantController { private final PlantService service; public PlantController(PlantService service){this.service=service;} @GetMapping public ApiResponse<List<Plant>> list(){return ApiResponse.ok(service.list());} @PostMapping public ApiResponse<Plant> create(@Valid @RequestBody PlantRequest r){return ApiResponse.ok(service.create(r));} @PutMapping("/{id}") public ApiResponse<Plant> update(@PathVariable Long id,@Valid @RequestBody PlantRequest r){return ApiResponse.ok(service.update(id,r));} @DeleteMapping("/{id}") public ApiResponse<Void> delete(@PathVariable Long id){service.delete(id);return ApiResponse.ok(null);}
}
