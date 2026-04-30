package net.softloaf.ded_fuse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.request.PushTokenRequest;
import net.softloaf.ded_fuse.dto.response.ErrorResponse;
import net.softloaf.ded_fuse.service.PushService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Пуш токены", description = "Управление токенами для уведомлений")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/push-token")
public class PushController {
    private final PushService pushService;

    @Operation(
            summary = "Отправка токена",
            description = "Обновляет или создает токен для пользователя (разные токены на разные платформы).",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Токен успешно удален"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "message": "Пользователь не найден",
                                          "timestamp": 0
                                        }
                                        """
                                    )
                            )
                    )
            }
    )
    @PostMapping("/send")
    public ResponseEntity<?> postToken(@RequestBody PushTokenRequest pushTokenRequest) {
        pushService.writeToken(pushTokenRequest);
        return ResponseEntity.noContent().build();
    }
}
