package com.finance.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, unique = true)
    private ERole name;

    public Role() {}

    public Role(Long id, ERole name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ERole getName() { return name; }
    public void setName(ERole name) { this.name = name; }

    public static RoleBuilder builder() {
        return new RoleBuilder();
    }

    public static class RoleBuilder {
        private Long id;
        private ERole name;

        RoleBuilder() {}

        public RoleBuilder id(Long id) { this.id = id; return this; }
        public RoleBuilder name(ERole name) { this.name = name; return this; }

        public Role build() {
            return new Role(id, name);
        }
    }
}
