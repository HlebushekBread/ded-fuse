package net.softloaf.ded_fuse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping
    public ResponseEntity<?> getResponse() {
        return new ResponseEntity<>("JWT works!", HttpStatus.OK);
    }
}
