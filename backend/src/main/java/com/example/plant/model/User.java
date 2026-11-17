package com.example.plant.model;
import java.time.LocalDateTime;
public class User {
 private Long id; private String username; private String password; private String nickname; private String phone; private String email; private String role; private Integer status; private LocalDateTime createTime; private LocalDateTime updateTime;
 public Long getId(){return id;} public void setId(Long v){id=v;} public String getUsername(){return username;} public void setUsername(String v){username=v;} public String getPassword(){return password;} public void setPassword(String v){password=v;}
 public String getNickname(){return nickname;} public void setNickname(String v){nickname=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
 public String getRole(){return role;} public void setRole(String v){role=v;} public Integer getStatus(){return status;} public void setStatus(Integer v){status=v;}
 public LocalDateTime getCreateTime(){return createTime;} public void setCreateTime(LocalDateTime v){createTime=v;} public LocalDateTime getUpdateTime(){return updateTime;} public void setUpdateTime(LocalDateTime v){updateTime=v;}
}
