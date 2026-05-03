package net.softloaf.ded_fuse.service;

import net.softloaf.ded_fuse.model.Role;
import net.softloaf.ded_fuse.model.TrustedContact;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.repository.HeartbeatLogRepository;
import net.softloaf.ded_fuse.repository.TrustedContactRepository;
import net.softloaf.ded_fuse.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HeartbeatLogServiceTests {
    @Mock
    private SessionService sessionService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private HeartbeatLogRepository heartbeatLogRepository;
    @Mock
    private TrustedContactRepository trustedContactRepository;

    @InjectMocks
    private HeartbeatLogService heartbeatLogService;

    @Test
    void getHeartbeatLog_shouldThrowIfNotMember() {
        Role role = new Role();
        role.setId(0L);
        role.setName("KEEPER");

        User user = new User();
        user.setId(0L);
        user.setUsername("+123456789");
        user.setRole(role);

        when(userRepository.findById(0L))
                .thenReturn(Optional.of(user));

        assertThrows(ResponseStatusException.class,
                () -> heartbeatLogService.getHeartbeatLog(0L));

        verify(heartbeatLogRepository, never()).findByUserId(anyLong());
    }

    @Test
    void getHeartbeatLog_shouldThrowIfNotContact() {

        Role role = new Role();
        role.setId(1);
        role.setName("MEMBER");

        User user = new User();
        user.setId(0);
        user.setUsername("+987654321");
        user.setRole(role);

        when(userRepository.findById(0L))
                .thenReturn(Optional.of(user));
        when(sessionService.getCurrentUserId())
                .thenReturn(1L);
        when(trustedContactRepository.findAllByMemberId(0L))
                .thenReturn(List.of());

        assertThrows(ResponseStatusException.class,
                () -> heartbeatLogService.getHeartbeatLog(0L));

        verify(heartbeatLogRepository, never()).findByUserId(anyLong());
    }
}
