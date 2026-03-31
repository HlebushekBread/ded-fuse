package net.softloaf.ded_fuse.controller;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.HeartbeatLogResponse;
import net.softloaf.ded_fuse.dto.LatLonRequest;
import net.softloaf.ded_fuse.service.HeartbeatLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/heartbeat")
public class HeartbeatController {
    private final HeartbeatLogService heartbeatLogService;

    @GetMapping("/{id}")
    public HeartbeatLogResponse getHeartbeatLog(@PathVariable(name = "id") long id) {
        return heartbeatLogService.getHeartbeatLog(id);
    }

    @PutMapping("/tap")
    public ResponseEntity<?> tapHeartbeatLog(@RequestBody LatLonRequest latLonRequest) {
        heartbeatLogService.tapHeartbeat(latLonRequest);
        return ResponseEntity.noContent().build();
    }
}
