package net.softloaf.ded_fuse.dto.request;

import lombok.Data;

@Data
public class PushTokenRequest {
    String username;
    String token;
    String platform;
}
