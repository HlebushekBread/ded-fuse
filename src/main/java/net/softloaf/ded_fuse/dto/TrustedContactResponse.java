package net.softloaf.ded_fuse.dto;

import lombok.Getter;
import lombok.Setter;
import net.softloaf.ded_fuse.model.TrustedContact;

import java.time.LocalDateTime;

@Getter
@Setter
public class TrustedContactResponse {
    private long id;
    private UserBasicResponse owner;
    private UserBasicResponse contact;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public TrustedContactResponse(TrustedContact trustedContact) {
        this.id = trustedContact.getId();
        this.owner = new UserBasicResponse(trustedContact.getOwner());
        this.contact = new UserBasicResponse(trustedContact.getContact());
        this.status = trustedContact.getStatus();
        this.createdAt = trustedContact.getCreatedAt();
        this.respondedAt = trustedContact.getRespondedAt();
    }
}
