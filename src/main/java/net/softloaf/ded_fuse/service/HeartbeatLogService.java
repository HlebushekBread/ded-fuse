package net.softloaf.ded_fuse.service;

import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.HeartbeatLogResponse;
import net.softloaf.ded_fuse.dto.LatLonRequest;
import net.softloaf.ded_fuse.dto.TrustedContactResponse;
import net.softloaf.ded_fuse.model.HeartbeatLog;
import net.softloaf.ded_fuse.model.TrustedContact;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.repository.HeartbeatLogRepository;
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
public class HeartbeatLogService {
    private final AuthService authService;
    private final UserRepository userRepository;
    private final HeartbeatLogRepository heartbeatLogRepository;
    private final TrustedContactRepository trustedContactRepository;

    @Transactional
    public void tapHeartbeat(LatLonRequest latLonRequest) {
        User user = userRepository.findById(authService.getCurrentUserId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неавторизованный запрос"));

        HeartbeatLog heartbeatLog = heartbeatLogRepository.findByUserId(user.getId()).orElse(null);

        if(heartbeatLog == null) {
            heartbeatLog = new HeartbeatLog();
            heartbeatLog.setUser(user);
        }

        heartbeatLog.setTappedAt(LocalDateTime.now());
        if(latLonRequest.getLat() != null) heartbeatLog.setLat(latLonRequest.getLat());
        if(latLonRequest.getLon() != null) heartbeatLog.setLon(latLonRequest.getLon());

        heartbeatLogRepository.save(heartbeatLog);
    }

    public HeartbeatLogResponse getHeartbeatLog(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Неверный ID контакта"));

        if(!user.getRole().getName().equals("MEMBER")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверная роль пользователя");
        }

        if(user.getId() != authService.getCurrentUserId()) {
            List<TrustedContact> trustedContacts = trustedContactRepository.findAllByMemberId(user.getId());
            long requestingId = authService.getCurrentUserId();
            boolean flag = false;
            for(TrustedContact trustedContact : trustedContacts) {
                if(trustedContact.getKeeper().getId() == requestingId && trustedContact.getStatus() == 1) {
                    flag = true;
                    break;
                }
            }
            if(!flag) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нет прав на получение лога");
        }

        HeartbeatLog heartbeatLog = heartbeatLogRepository.findByUserId(user.getId()).orElse(null);

        if(heartbeatLog == null) {
            heartbeatLog = new HeartbeatLog();
            heartbeatLog.setUser(user);
            heartbeatLog.setTappedAt(user.getRegisteredAt());
            heartbeatLog.setLat(null);
            heartbeatLog.setLon(null);
            heartbeatLogRepository.save(heartbeatLog);
        }

        return new HeartbeatLogResponse(heartbeatLog);
    }
}
