package net.softloaf.ded_fuse.dto;

import lombok.Getter;
import lombok.Setter;
import net.softloaf.ded_fuse.model.TrustedContact;

import java.time.LocalDateTime;

@Getter
@Setter
public class TrustedContactDto {
    private long id;
    private String ownerUsername;
    private String contactUsername;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public TrustedContactDto(TrustedContact trustedContact) {
        this.id = trustedContact.getId();
        this.ownerUsername = trustedContact.getOwner().getUsername();
        this.contactUsername = trustedContact.getOwner().getUsername();
        this.status = trustedContact.getStatus();
        this.createdAt = trustedContact.getCreatedAt();
        this.respondedAt = trustedContact.getRespondedAt();
    }
}
