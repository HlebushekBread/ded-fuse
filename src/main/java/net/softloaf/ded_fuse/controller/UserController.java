package net.softloaf.ded_fuse.controller;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/member")
    public List<String> getMembers() {
        return userService.findAllUsernamesByRoleName("MEMBER");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
