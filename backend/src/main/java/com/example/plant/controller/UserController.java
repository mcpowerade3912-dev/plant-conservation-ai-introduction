package com.example.plant.controller;
import com.example.plant.common.ApiResponse; import com.example.plant.dto.UserRequest; import com.example.plant.model.User; import com.example.plant.service.UserService; import org.springframework.web.bind.annotation.*;
import javax.validation.Valid; import java.util.*;
@RestController @RequestMapping("/api/users") public class UserController { private final UserService users; public UserController(UserService users){this.users=users;}
 @GetMapping public ApiResponse<List<User>> list(){return ApiResponse.ok(users.list());} @PostMapping public ApiResponse<User> create(@Valid @RequestBody UserRequest r){return ApiResponse.ok(users.create(r));}
 @PutMapping("/{id}") public ApiResponse<User> update(@PathVariable Long id,@Valid @RequestBody UserRequest r){return ApiResponse.ok(users.update(id,r));} @DeleteMapping("/{id}") public ApiResponse<Void> delete(@PathVariable Long id){users.delete(id);return ApiResponse.ok(null);}
}
