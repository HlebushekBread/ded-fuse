package net.softloaf.ded_fuse.dto.response;

import lombok.Getter;
import lombok.Setter;
import net.softloaf.ded_fuse.model.TrustedContact;

import java.time.LocalDateTime;

@Getter
@Setter
public class TrustedContactResponse {
    private long id;
    private UserBasicResponse keeper;
    private UserBasicResponse member;
    private int status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;

    public TrustedContactResponse(TrustedContact trustedContact) {
        this.id = trustedContact.getId();
        this.keeper = new UserBasicResponse(trustedContact.getKeeper());
        this.member = new UserBasicResponse(trustedContact.getMember());
        this.status = trustedContact.getStatus();
        this.createdAt = trustedContact.getCreatedAt();
        this.respondedAt = trustedContact.getRespondedAt();
    }
}
