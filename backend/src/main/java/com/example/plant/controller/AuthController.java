package com.example.plant.controller;
import com.example.plant.common.ApiResponse; import com.example.plant.dto.*; import com.example.plant.model.User; import com.example.plant.security.JwtTokenService; import com.example.plant.service.UserService; import org.springframework.web.bind.annotation.*;
import javax.validation.Valid; import java.util.*;
@RestController @RequestMapping("/api") public class AuthController { private final UserService users; private final JwtTokenService tokens; public AuthController(UserService users,JwtTokenService tokens){this.users=users;this.tokens=tokens;}
 @PostMapping("/login") public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest r){User u=users.login(r.username(),r.password());return ApiResponse.ok(new LoginResponse(tokens.create(u.getUsername(),u.getRole()),Map.of("id",u.getId(),"username",u.getUsername(),"nickname",u.getNickname(),"role",u.getRole(),"phone",u.getPhone(),"email",u.getEmail())));}
 @GetMapping("/users/me") public ApiResponse<User> me(org.springframework.security.core.Authentication a){return ApiResponse.ok(users.me(a.getName()));}
 @PutMapping("/users/me") public ApiResponse<User> me(org.springframework.security.core.Authentication a,@Valid @RequestBody ProfileRequest r){return ApiResponse.ok(users.updateProfile(a.getName(),r));}
}
