package net.softloaf.ded_fuse.service;

import net.softloaf.ded_fuse.dto.request.NewUserRequest;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.repository.RoleRepository;
import net.softloaf.ded_fuse.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private SessionService sessionService;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void saveNewUser_shouldThrowIfUserExists() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setUsername("+70001234567");
        newUserRequest.setFullName("Иван Иванов");
        newUserRequest.setRole("MEMBER");

        when(userRepository.existsByUsername("+70001234567"))
                .thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> userService.saveNewUser(newUserRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void saveNewUser_shouldThrowIfInvalidPhone() {
        NewUserRequest newUserRequest = new NewUserRequest();
        newUserRequest.setUsername("he-he");
        newUserRequest.setFullName("Иван Иванов");
        newUserRequest.setRole("MEMBER");

        assertThrows(ResponseStatusException.class,
                () -> userService.saveNewUser(newUserRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_shouldThrowIfNotOwnerRequest() {
        User user = new User();
        user.setId(0);
        user.setUsername("+70001234567");
        user.setFullName("Иван Иванов");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        when(sessionService.getCurrentUserId())
                .thenReturn(3L);

        assertThrows(ResponseStatusException.class,
                () -> userService.deleteUser(1L));

        verify(userRepository, never()).deleteById(any());
    }
}
