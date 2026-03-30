package net.softloaf.ded_fuse.service;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.NewUserDto;
import net.softloaf.ded_fuse.model.Role;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.repository.RoleRepository;
import net.softloaf.ded_fuse.repository.UserRepository;
import net.softloaf.ded_fuse.security.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void saveNewUser(NewUserDto newUserDto) {
        if (newUserDto.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Username не может быть null");
        }
        if (newUserDto.getUsername().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username не может быть пустым");
        }
        if (!roleRepository.existsByName(newUserDto.getRole())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующая кодировка роли");
        }
        if (userRepository.existsByUsername(newUserDto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь уже существует");
        }

        User user = new User();

        user.setUsername(newUserDto.getUsername());

        Role role = roleRepository.findByName(newUserDto.getRole()).orElse(null);
        user.setRole(role);

        user.setLastKnownLat(0.0);
        user.setLastKnownLon(0.0);

        LocalDateTime now = LocalDateTime.now();

        user.setLastKnownAt(now);
        user.setLastHeartbeatAt(now);
        user.setReminderSentAt(now);
        user.setLastActiveAt(now);

        user.setRegisteredAt(now);

        userRepository.save(user);
    }

    public List<String> findAllUsernamesByRoleName(String name) {
        Role role = roleRepository.findByName(name).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующая кодировка роли"));

        List<String> usernames = new ArrayList<>();
        for (User user : userRepository.findAllByRoleId(role.getId())) {
            usernames.add(user.getUsername());
        }
        return usernames;
    }

    @Transactional
    public void deleteUser(long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Неверный ID"));

        if (user.getId() != authService.getCurrentUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на удаление");
        }

        userRepository.deleteById(id);
    }
}
