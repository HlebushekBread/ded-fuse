package net.softloaf.ded_fuse.dto.request;

import lombok.Data;

@Data
public class NewUserRequest {
    private String username;
    private String fullName;
    private String role;
}
