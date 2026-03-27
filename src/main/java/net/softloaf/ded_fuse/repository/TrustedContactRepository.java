package net.softloaf.ded_fuse.repository;

import net.softloaf.ded_fuse.model.TrustedContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrustedContactRepository extends JpaRepository<TrustedContact, Long> {
    List<TrustedContact> findAllByOwnerId(long ownerId);
}
