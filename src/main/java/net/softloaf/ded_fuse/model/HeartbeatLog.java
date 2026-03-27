package net.softloaf.ded_fuse.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "heartbeat_log")
public class HeartbeatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "tapped_at")
    private LocalDateTime tappedAt;

    @Column(name = "lat")
    private double lat;

    @Column(name = "lon")
    private double lon;
}
