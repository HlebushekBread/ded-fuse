package net.softloaf.ded_fuse.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
}
