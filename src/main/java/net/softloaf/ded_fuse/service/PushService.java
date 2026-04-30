package net.softloaf.ded_fuse.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.request.PushTokenRequest;
import net.softloaf.ded_fuse.model.HeartbeatLog;
import net.softloaf.ded_fuse.model.PushToken;
import net.softloaf.ded_fuse.model.TrustedContact;
import net.softloaf.ded_fuse.model.User;
import net.softloaf.ded_fuse.repository.HeartbeatLogRepository;
import net.softloaf.ded_fuse.repository.PushTokenRepository;
import net.softloaf.ded_fuse.repository.TrustedContactRepository;
import net.softloaf.ded_fuse.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PushService {
    private final PushTokenRepository pushTokenRepository;
    private final HeartbeatLogRepository heartbeatLogRepository;
    private final TrustedContactRepository trustedContactRepository;
    private final UserRepository userRepository;
    private final SessionService sessionService;

    @Transactional
    public void writeToken(PushTokenRequest pushTokenRequest) {
        User user = userRepository.findByUsername(pushTokenRequest.getUsername()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден"));
        PushToken pushToken = pushTokenRepository.findByUserIdAndPlatform(user.getId(), pushTokenRequest.getPlatform()).orElse(new PushToken());
        pushToken.setToken(pushToken.getToken());
        pushToken.setUser(user);
        pushToken.setPlatform(pushTokenRequest.getPlatform());
        pushToken.setUpdatedAt(LocalDateTime.now());

        pushTokenRepository.save(pushToken);
    }

    private String send(String token, String title, String body) throws FirebaseMessagingException {

        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional(readOnly = true)
    private void checkHeartbeats() throws FirebaseMessagingException {
        LocalDateTime cutoff1h = LocalDateTime.now().minusHours(1);
        LocalDateTime cutoff3h = LocalDateTime.now().minusHours(3);

        List<HeartbeatLog> heartbeatLogs = heartbeatLogRepository.findByTappedAtBefore(cutoff1h);

        for(HeartbeatLog heartbeatLog : heartbeatLogs) {
            List<PushToken> pushTokens = pushTokenRepository.findByUserId(heartbeatLog.getUser().getId());

            if(heartbeatLog.getTappedAt().isBefore(cutoff3h)) {
                List<TrustedContact> trustedContacts = trustedContactRepository.findAllByMemberId(heartbeatLog.getUser().getId());
                List<Long> contactIds = trustedContacts.stream().map(trustedContact -> trustedContact.getKeeper().getId()).toList();
                List<PushToken> contactPushTokens = pushTokenRepository.findByUserIdIn(contactIds);

                for(PushToken contactPushToken : contactPushTokens) {
                    send(contactPushToken.getToken(), "Контакт неактивен", "Контакт неактивен уже более 3 часов, свяжитесь с ним.");
                }
            }

            for(PushToken pushToken : pushTokens) {
                if (heartbeatLog.getTappedAt().isBefore(cutoff3h)) {
                    send(pushToken.getToken(), "Нажмите на пульс", "Не забудьте отметиться в приложении.");
                } else {
                    send(pushToken.getToken(), "Контакты уведомлены", "Вашим контактам отправлено сообщение о вашей неактивности.");
                };
            }
        }
    }
}
