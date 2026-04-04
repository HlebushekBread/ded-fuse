package net.softloaf.ded_fuse.service;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.LatLonRequest;
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
    private final SessionService sessionService;
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
        user.setFullName(newUserRequest.getFullName());

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

    @Transactional(readOnly = true)
    public UserDetailedResponse findById(long id) {
        return new UserDetailedResponse(userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующий ID")));
    }

    @Transactional(readOnly = true)
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

        if (user.getId() != sessionService.getCurrentUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на удаление");
        }

        userRepository.deleteById(id);
    }

    @Transactional
    public void backgroundUpdate(LatLonRequest latLonRequest) {
        User user = userRepository.findById(sessionService.getCurrentUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неавторизованный запрос"));

        if(latLonRequest.getLat() != null || latLonRequest.getLon() != null) {
            user.setLastKnownLat(latLonRequest.getLat());
            user.setLastKnownLon(latLonRequest.getLon());
            user.setLastKnownAt(LocalDateTime.now());
        }

        userRepository.save(user);
    }

    @Transactional
    public void appOpenUpdate() {
        User user = userRepository.findById(sessionService.getCurrentUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неавторизованный запрос"));

        user.setLastActiveAt(LocalDateTime.now());

        userRepository.save(user);
    }
}
