package net.softloaf.ded_fuse.service;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.NewTrustedContactDto;
import net.softloaf.ded_fuse.model.TrustedContact;
import net.softloaf.ded_fuse.repository.TrustedContactRepository;
import net.softloaf.ded_fuse.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TrustedContactService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final TrustedContactRepository trustedContactRepository;

    public List<TrustedContact> getUserTrustedContacts() {
        return trustedContactRepository.findAllByOwnerId(authService.getCurrentUserId());
    }

    public void addTrustedContact(NewTrustedContactDto trustedContactDto) {
        TrustedContact trustedContact = new TrustedContact();

        trustedContact.setOwner(userRepository.findById(authService.getCurrentUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неавторизованный запрос")));
        trustedContact.setContact(userRepository.findByUsername(trustedContactDto.getContactUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Неверный ID контакта")));
        trustedContact.setStatus(0);
        trustedContact.setCreatedAt(LocalDateTime.now());

        trustedContactRepository.save(trustedContact);
    }

    public void acceptTrustedContact(long id) {
        TrustedContact trustedContact = trustedContactRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Неверный ID"));

        if(trustedContact.getOwner().getId() != authService.getCurrentUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на редактирование");
        }

        trustedContact.setStatus(1);
        trustedContact.setRespondedAt(LocalDateTime.now());

        trustedContactRepository.save(trustedContact);
    }

    public void deleteTrustedContact(long id) {
        TrustedContact trustedContact = trustedContactRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Неверный ID"));
        trustedContactRepository.deleteById(id);
    }
}
