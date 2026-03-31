package net.softloaf.ded_fuse.repository;

import net.softloaf.ded_fuse.model.HeartbeatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HeartbeatLogRepository extends JpaRepository<HeartbeatLog, Long> {
    Optional<HeartbeatLog> findByUserId(long id);
}
