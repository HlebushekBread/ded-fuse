package net.softloaf.ded_fuse.controller;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.NewTrustedContactDto;
import net.softloaf.ded_fuse.model.TrustedContact;
import net.softloaf.ded_fuse.service.TrustedContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {
    private final TrustedContactService trustedContactService;

    @GetMapping("")
    public List<TrustedContact> getTrustedContacts() {
        return trustedContactService.getUserTrustedContacts();
    }

    @PostMapping("")
    public ResponseEntity<?> addTrustedContact(@RequestBody NewTrustedContactDto trustedContactDto) {
        trustedContactService.addTrustedContact(trustedContactDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/respond")
    public ResponseEntity<?> acceptTrustedContact(@PathVariable long id) {
        trustedContactService.acceptTrustedContact(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteTrustedContact(@PathVariable long id) {
        trustedContactService.deleteTrustedContact(id);
        return ResponseEntity.noContent().build();
    }
}
