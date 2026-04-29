package net.softloaf.ded_fuse.dto.response;

import lombok.Data;
import net.softloaf.ded_fuse.model.HeartbeatLog;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class HeartbeatLogResponse {
    private long id;
    private UserBasicResponse user;
    private LocalDateTime tappedAt;
    private Double lat;
    private Double lon;
    private String status;

    public HeartbeatLogResponse(HeartbeatLog heartbeatLog) {
        this.id = heartbeatLog.getId();
        this.user = new UserBasicResponse(heartbeatLog.getUser());
        this.tappedAt = heartbeatLog.getTappedAt();
        this.lat = heartbeatLog.getLat();
        this.lon = heartbeatLog.getLon();

        Duration duration = Duration.between(tappedAt, LocalDateTime.now());
        if(duration.toHours() >= 3) {
            this.status = "ALERT";
        }
        else if(duration.toHours() >= 1) {
            this.status = "WARN";
        }
        else {
            this.status = "OK";
        }
    }
}
