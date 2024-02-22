package com.example.test.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name="users")
public class User
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
    private String username;

    @Column(nullable=false)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
            name="users_roles",
            joinColumns = { @JoinColumn(name = "user_id") },
            inverseJoinColumns = { @JoinColumn(name = "role_id") })
    private List<Role> roles = new ArrayList<>();

    @OneToMany(mappedBy="user",
            cascade=CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Account> accounts = new ArrayList<>();

    @OneToMany(mappedBy="user",
            cascade=CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Operation> operations = new ArrayList<>();
    @Column(nullable=false)
    private Boolean locked=false;

    @Column(nullable=false)
    private Boolean enabled=false;

    public boolean isUser() {
        for (Role role : this.getRoles()) {
            if (role.getName().equals("ROLE_USER")) return true;
        }
        return false;
    }

    public void addOperation(Operation operation) {
        this.operations.add(operation);
    }

}