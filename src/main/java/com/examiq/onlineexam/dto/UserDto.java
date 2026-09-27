package com.examiq.onlineexam.dto;

import com.examiq.onlineexam.model.Role;
import com.examiq.onlineexam.model.User;

public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private boolean active;

    public UserDto() {}

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.active = user.isActive();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
