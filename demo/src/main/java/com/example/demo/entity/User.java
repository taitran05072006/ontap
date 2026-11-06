package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String email;
    private String phone;
    private LocalDateTime createAt;
    @OneToMany(mappedBy = "assignee")
    @Builder.Default
    List<Task> tasks = new ArrayList<>();

    @Builder.Default
    @Column(name = "user_role")
    private Role role = Role.USER;
    private String password;
    @Builder.Default
    private boolean enabled = false;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    List<Comment> comments = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.createAt = LocalDateTime.now();
    }

}
