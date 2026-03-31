package net.softloaf.ded_fuse.dto;

import lombok.Data;
import net.softloaf.ded_fuse.model.HeartbeatLog;

import java.time.LocalDateTime;

@Data
public class HeartbeatLogResponse {
    private long id;
    private UserBasicResponse user;
    private LocalDateTime tappedAt;
    private Double lat;
    private Double lon;

    public HeartbeatLogResponse(HeartbeatLog heartbeatLog) {
        this.id = heartbeatLog.getId();
        this.user = new UserBasicResponse(heartbeatLog.getUser());
        this.tappedAt = heartbeatLog.getTappedAt();
        this.lat = heartbeatLog.getLat();
        this.lon = heartbeatLog.getLon();
    }
}
