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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TrustedContactService {
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final TrustedContactRepository trustedContactRepository;

    @Transactional(readOnly = true)
    public List<TrustedContactResponse> getUserTrustedContacts() {
        User user = userRepository.findById(sessionService.getCurrentUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неавторизованный запрос"));

        List<TrustedContact> trustedContacts = new ArrayList<>();

        if(user.getRole().getName().equals("KEEPER")) {
            trustedContacts = trustedContactRepository.findAllByKeeperId(user.getId());
        }
        if(user.getRole().getName().equals("MEMBER")) {
            trustedContacts = trustedContactRepository.findAllByMemberId(user.getId());
        }

        List<TrustedContactResponse> trustedContactResponses = new ArrayList<>();
        for(TrustedContact trustedContact : trustedContacts) {
            trustedContactResponses.add(new TrustedContactResponse(trustedContact));
        }
        return trustedContactResponses;
    }

    @Transactional
    public void addTrustedContact(NewTrustedContactRequest newTrustedContactRequest) {
        User keeper = userRepository.findById(sessionService.getCurrentUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неавторизованный запрос"));
        User member = userRepository.findByUsername(newTrustedContactRequest.getMemberUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующий контакт"));

        if(!member.getRole().getName().equals("MEMBER")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверная роль контакта");
        }

        TrustedContact trustedContact = new TrustedContact();

        trustedContact.setKeeper(keeper);
        trustedContact.setMember(member);
        trustedContact.setStatus(0);
        trustedContact.setCreatedAt(LocalDateTime.now());

        trustedContactRepository.save(trustedContact);
    }

    @Transactional
    public void acceptTrustedContact(long id) {
        TrustedContact trustedContact = trustedContactRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующий ID"));

        if(trustedContact.getMember().getId() != sessionService.getCurrentUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав принятие");
        }

        trustedContact.setStatus(1);
        trustedContact.setRespondedAt(LocalDateTime.now());

        trustedContactRepository.save(trustedContact);
    }

    @Transactional
    public void deleteTrustedContact(long id) {
        TrustedContact trustedContact = trustedContactRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Несуществующий ID"));

        if (trustedContact.getKeeper().getId() != sessionService.getCurrentUserId() && trustedContact.getMember().getId() != sessionService.getCurrentUserId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав удаление");
        }

        trustedContactRepository.deleteById(id);
    }
}
