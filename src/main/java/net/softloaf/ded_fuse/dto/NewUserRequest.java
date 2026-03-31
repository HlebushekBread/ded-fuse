package net.softloaf.ded_fuse.dto;

import lombok.Data;

@Data
public class NewUserRequest {
    private String username;
    private String role;
}
