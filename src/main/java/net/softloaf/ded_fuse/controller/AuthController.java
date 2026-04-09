package net.softloaf.ded_fuse.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.request.NewUserRequest;
import net.softloaf.ded_fuse.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final OneTimeTokenService oneTimeTokenService;
    private final OneTimeTokenGenerationSuccessHandler oneTimeTokenGenerationSuccessHandler;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody NewUserRequest newUserRequest, HttpServletRequest request, HttpServletResponse response) {
        userService.saveNewUser(newUserRequest);
        GenerateOneTimeTokenRequest ottRequest = new GenerateOneTimeTokenRequest(newUserRequest.getUsername());
        OneTimeToken ott = oneTimeTokenService.generate(ottRequest);
        try {
            oneTimeTokenGenerationSuccessHandler.handle(request, response, ott);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка отправки кода");
        }
        return ResponseEntity.noContent().build();
    }
}
