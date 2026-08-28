package com.bhaskar.userservice.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Role extends BaseModel {
    private String role;
}
