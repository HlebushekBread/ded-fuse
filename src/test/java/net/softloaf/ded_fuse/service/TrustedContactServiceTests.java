package net.softloaf.ded_fuse.service;

import net.softloaf.ded_fuse.dto.request.NewTrustedContactRequest;
import net.softloaf.ded_fuse.model.Role;
import net.softloaf.ded_fuse.model.TrustedContact;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.repository.TrustedContactRepository;
import net.softloaf.ded_fuse.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
public class TrustedContactServiceTests {
    @Mock
    private SessionService sessionService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TrustedContactRepository trustedContactRepository;

    @InjectMocks
    private TrustedContactService trustedContactService;

    private Role keeperRole;
    private Role memberRole;

    @BeforeEach
    void setUp() {
        keeperRole = new Role();
        keeperRole.setId(0);
        keeperRole.setName("KEEPER");

        memberRole = new Role();
        memberRole.setId(1);
        memberRole.setName("MEMBER");
    }

    @Test
    void addTrustedContact_shouldThrowIfContactNotMember() {
        NewTrustedContactRequest request = new NewTrustedContactRequest();
        request.setMemberUsername("+987654321");

        Role keeperRole = new Role();
        keeperRole.setId(0);
        keeperRole.setName("KEEPER");

        User keeper = new User();
        keeper.setId(0);
        keeper.setUsername("+123456789");
        keeper.setRole(keeperRole);

        User member = new User();
        member.setId(1);
        member.setUsername("+987654321");
        member.setRole(keeperRole);


        when(sessionService.getCurrentUserId())
                .thenReturn(0L);

        when(userRepository.findById(0L))
                .thenReturn(Optional.of(keeper));

        when(userRepository.findByUsername("+987654321"))
                .thenReturn(Optional.of(member));

        assertThrows(ResponseStatusException.class,
                () -> trustedContactService.addTrustedContact(request));

        verify(trustedContactRepository, never()).save(any());
    }

    @Test
    void addTrustedContact_shouldThrowIfContactExists() {
        NewTrustedContactRequest request = new NewTrustedContactRequest();
        request.setMemberUsername("+987654321");

        User keeper = new User();
        keeper.setId(0);
        keeper.setUsername("+123456789");
        keeper.setRole(keeperRole);

        User member = new User();
        member.setId(1);
        member.setUsername("+987654321");
        member.setRole(memberRole);

        when(sessionService.getCurrentUserId())
                .thenReturn(0L);

        when(userRepository.findById(0L))
                .thenReturn(Optional.of(keeper));

        when(userRepository.findByUsername("+987654321"))
                .thenReturn(Optional.of(member));

        when(trustedContactRepository.existsByKeeperIdAndMemberId(0L, 1L))
                .thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> trustedContactService.addTrustedContact(request));

        verify(trustedContactRepository, never()).save(any());
    }

    @Test
    void acceptTrustedContact_shouldThrowIfNotOwnerRequest() {
        Role keeperRole = new Role();
        keeperRole.setId(0);
        keeperRole.setName("KEEPER");

        Role memberRole = new Role();
        memberRole.setId(1);
        memberRole.setName("MEMBER");

        User keeper = new User();
        keeper.setId(0);
        keeper.setUsername("+123456789");
        keeper.setRole(keeperRole);

        User member = new User();
        member.setId(1);
        member.setUsername("+987654321");
        member.setRole(memberRole);

        TrustedContact trustedContact = new TrustedContact();
        trustedContact.setId(0L);
        trustedContact.setKeeper(keeper);
        trustedContact.setMember(member);
        trustedContact.setStatus(0);

        when(trustedContactRepository.findById(0L))
                .thenReturn(Optional.of(trustedContact));

        when(sessionService.getCurrentUserId())
                .thenReturn(2L);

        assertThrows(ResponseStatusException.class,
                () -> trustedContactService.acceptTrustedContact(0L));

        verify(trustedContactRepository, never()).save(any());
    }
}
