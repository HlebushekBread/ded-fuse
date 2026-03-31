package net.softloaf.ded_fuse.service;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.NewUserRequest;
import net.softloaf.ded_fuse.dto.UserBasicResponse;
import net.softloaf.ded_fuse.dto.UserDetailedResponse;
import net.softloaf.ded_fuse.model.Role;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.repository.RoleRepository;
import net.softloaf.ded_fuse.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void saveNewUser(NewUserRequest newUserRequest) {
        if (newUserRequest.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Username не может быть null");
        }
        if (newUserRequest.getUsername().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username не может быть пустым");
        }
        if (!roleRepository.existsByName(newUserRequest.getRole())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующая кодировка роли");
        }
        if (userRepository.existsByUsername(newUserRequest.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже существует");
        }

        User user = new User();

        user.setUsername(newUserRequest.getUsername());

        Role role = roleRepository.findByName(newUserRequest.getRole()).orElse(null);
        user.setRole(role);

        user.setLastKnownLat(null);
        user.setLastKnownLon(null);

        LocalDateTime now = LocalDateTime.now();

        user.setLastKnownAt(now);
        user.setLastHeartbeatAt(now);
        user.setReminderSentAt(now);
        user.setLastActiveAt(now);

        user.setRegisteredAt(now);

        userRepository.save(user);
    }

    public UserDetailedResponse findById(long id) {
        return new UserDetailedResponse(userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующий ID")));
    }

    public List<UserBasicResponse> findAllByRoleName(String name) {
        Role role = roleRepository.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующая кодировка роли"));

        List<UserBasicResponse> userBasicResponses = new ArrayList<>();
        for (User user : userRepository.findAllByRoleId(role.getId())) {
            userBasicResponses.add(new UserBasicResponse(user));
        }
        return userBasicResponses;
    }

    @Transactional
    public void deleteUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующий ID"));

        if (user.getId() != authService.getCurrentUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на удаление");
        }

        userRepository.deleteById(id);
    }
}
