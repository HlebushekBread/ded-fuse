package net.softloaf.ded_fuse.controller;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.JwtRequest;
import net.softloaf.ded_fuse.dto.JwtResponse;
import net.softloaf.ded_fuse.dto.NewUserDto;
import net.softloaf.ded_fuse.security.JwtUtils;
import net.softloaf.ded_fuse.security.UserDetailsImpl;
import net.softloaf.ded_fuse.security.UserDetailsServiceImpl;
import net.softloaf.ded_fuse.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        UserDetailsImpl userDetails;
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Incorrect credentials", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> saveNewUser(@RequestBody NewUserDto newUserDto) {
        userService.saveNewUser(newUserDto);

        UserDetailsImpl userDetails;
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(newUserDto.getUsername(), newUserDto.getPassword()));
            userDetails = userDetailsService.loadUserByUsername(newUserDto.getUsername());
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Incorrect credentials", HttpStatus.UNAUTHORIZED);
        }
        String token = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
