package net.softloaf.ded_fuse.service;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.NewTrustedContactRequest;
import net.softloaf.ded_fuse.dto.TrustedContactResponse;
import net.softloaf.ded_fuse.model.TrustedContact;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.repository.TrustedContactRepository;
import net.softloaf.ded_fuse.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TrustedContactService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final TrustedContactRepository trustedContactRepository;

    public List<TrustedContactResponse> getUserTrustedContacts() {
        User user = userRepository.findById(authService.getCurrentUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неавторизованный запрос"));

        List<TrustedContact> trustedContacts = new ArrayList<>();

        if(user.getRole().getName().equals("KEEPER")) {
            trustedContacts = trustedContactRepository.findAllByOwnerId(user.getId());
        }
        if(user.getRole().getName().equals("MEMBER")) {
            trustedContacts = trustedContactRepository.findAllByContactId(user.getId());
        }

        List<TrustedContactResponse> trustedContactResponses = new ArrayList<>();
        for(TrustedContact trustedContact : trustedContacts) {
            trustedContactResponses.add(new TrustedContactResponse(trustedContact));
        }
        return trustedContactResponses;
    }

    public void addTrustedContact(NewTrustedContactRequest newTrustedContactRequest) {
        User owner = userRepository.findById(authService.getCurrentUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неавторизованный запрос"));
        User contact = userRepository.findByUsername(newTrustedContactRequest.getContactUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующий контакт"));

        if(!contact.getRole().getName().equals("MEMBER")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверная роль контакта");
        }

        TrustedContact trustedContact = new TrustedContact();

        trustedContact.setOwner(owner);
        trustedContact.setContact(contact);
        trustedContact.setStatus(0);
        trustedContact.setCreatedAt(LocalDateTime.now());

        trustedContactRepository.save(trustedContact);
    }

    public void acceptTrustedContact(long id) {
        TrustedContact trustedContact = trustedContactRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующий ID"));

        if(trustedContact.getContact().getId() != authService.getCurrentUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав принятие");
        }

        trustedContact.setStatus(1);
        trustedContact.setRespondedAt(LocalDateTime.now());

        trustedContactRepository.save(trustedContact);
    }

    public void deleteTrustedContact(long id) {
        TrustedContact trustedContact = trustedContactRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующий ID"));

        if (trustedContact.getOwner().getId() != authService.getCurrentUserId() && trustedContact.getContact().getId() != authService.getCurrentUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав удаление");
        }

        trustedContactRepository.deleteById(id);
    }
}
