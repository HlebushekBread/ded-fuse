package net.softloaf.ded_fuse.controller;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.NewTrustedContactRequest;
import net.softloaf.ded_fuse.dto.TrustedContactResponse;
import net.softloaf.ded_fuse.service.TrustedContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {
    private final TrustedContactService trustedContactService;

    @GetMapping("/get")
    public List<TrustedContactResponse> getTrustedContacts() {
        return trustedContactService.getUserTrustedContacts();
    }

    @PostMapping("/add")
    public ResponseEntity<?> addTrustedContact(@RequestBody NewTrustedContactRequest trustedContactDto) {
        trustedContactService.addTrustedContact(trustedContactDto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/respond")
    public ResponseEntity<?> acceptTrustedContact(@PathVariable(name = "id") long id) {
        trustedContactService.acceptTrustedContact(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteTrustedContact(@PathVariable(name = "id") long id) {
        trustedContactService.deleteTrustedContact(id);
        return ResponseEntity.noContent().build();
    }
}
