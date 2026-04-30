package net.softloaf.ded_fuse.repository;

import net.softloaf.ded_fuse.model.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    List<PushToken> findByUserId(long userId);
    List<PushToken> findByUserIdIn(List<Long> ids);
}
