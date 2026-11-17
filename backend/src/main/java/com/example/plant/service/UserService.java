package com.example.plant.service;

import com.example.plant.dto.ProfileRequest;
import com.example.plant.dto.UserRequest;
import com.example.plant.model.User;
import com.example.plant.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User login(String username, String password) {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户名或密码错误"));
        if (!encoder.matches(password, user.getPassword()) || user.getStatus() != 1) {
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return user;
    }

    public List<User> list() {
        return repo.findAll();
    }

    public User me(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    public User create(UserRequest request) {
        if (repo.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("用户名已存在");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(encoder.encode(
                request.password() == null || request.password().isBlank() ? "123456" : request.password()));
        user.setNickname(request.nickname() == null ? request.username() : request.nickname());
        user.setPhone("");
        user.setEmail("");
        user.setRole(request.role());
        user.setStatus(request.status());
        return repo.insert(user);
    }

    public User update(Long id, UserRequest request) {
        User user = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setNickname(request.nickname());
        user.setRole(request.role());
        user.setStatus(request.status());
        repo.update(user);
        return user;
    }

    public User updateProfile(String username, ProfileRequest request) {
        User user = me(username);
        repo.updateProfile(user.getId(), request.nickname(), request.phone(), request.email());
        if (request.password() != null && !request.password().isBlank()) {
            repo.updatePassword(user.getId(), encoder.encode(request.password()));
        }
        return repo.findById(user.getId()).orElseThrow();
    }

    public void delete(Long id) {
        repo.delete(id);
    }
}
