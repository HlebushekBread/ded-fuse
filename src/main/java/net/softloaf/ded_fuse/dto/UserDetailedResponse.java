package net.softloaf.ded_fuse.dto;

import lombok.Data;
import net.softloaf.ded_fuse.model.User;

import java.time.LocalDateTime;

@Data
public class UserDetailedResponse {
    private long id;
    private String username;
    private String fullName;
    private String roleName;
    private Double lastKnownLat;
    private Double lastKnownLon;
    private LocalDateTime lastKnownAt;
    private LocalDateTime lastHeartbeatAt;
    private LocalDateTime reminderSentAt;
    private LocalDateTime lastActiveAt;
    private LocalDateTime registeredAt;

    public UserDetailedResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.fullName = user.getFullName();
        this.roleName = user.getRole().getName();
        this.lastKnownLat = user.getLastKnownLat();
        this.lastKnownLon = user.getLastKnownLon();
        this.lastKnownAt = user.getLastKnownAt();
        this.lastHeartbeatAt = user.getLastHeartbeatAt();
        this.reminderSentAt = user.getReminderSentAt();
        this.lastActiveAt = user.getLastActiveAt();
        this.registeredAt = user.getRegisteredAt();
    }
}
