package net.softloaf.ded_fuse.controller;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.UserBasicResponse;
import net.softloaf.ded_fuse.dto.UserDetailedResponse;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.service.AuthService;
import net.softloaf.ded_fuse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    @GetMapping("/{id}")
    public UserDetailedResponse getUser(@PathVariable(name = "id") long id) {
        return userService.findById(id);
    }

    @GetMapping("/member")
    public List<UserBasicResponse> getMembers() {
        return userService.findAllByRoleName("MEMBER");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSelf() {
        userService.deleteUser(authService.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
