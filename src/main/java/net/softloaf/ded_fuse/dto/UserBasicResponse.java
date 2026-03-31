package net.softloaf.ded_fuse.dto;

import lombok.Data;
import net.softloaf.ded_fuse.model.User;

@Data
public class UserBasicResponse {
    private long id;
    private String username;
    private String roleName;

    public UserBasicResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.roleName = user.getRole().getName();
    }
}
