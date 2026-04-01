package net.softloaf.ded_fuse.controller;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.LatLonRequest;
import net.softloaf.ded_fuse.dto.UserBasicResponse;
import net.softloaf.ded_fuse.dto.UserDetailedResponse;
import net.softloaf.ded_fuse.service.SessionService;
import net.softloaf.ded_fuse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final SessionService sessionService;

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
        userService.deleteUser(sessionService.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<?> backgroundUpdate(@RequestBody LatLonRequest latLonRequest) {
        userService.backgroundUpdate(latLonRequest);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<?> appOpenUpdate() {
        userService.appOpenUpdate();
        return ResponseEntity.noContent().build();
    }
}
