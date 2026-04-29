package net.softloaf.ded_fuse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.response.ErrorResponse;
import net.softloaf.ded_fuse.dto.response.HeartbeatLogResponse;
import net.softloaf.ded_fuse.dto.request.LatLonRequest;
import net.softloaf.ded_fuse.service.SessionService;
import net.softloaf.ded_fuse.service.HeartbeatLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Пульс", description = "Управление логами пульса пользователей")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/heartbeat")
public class HeartbeatController {

    private final HeartbeatLogService heartbeatLogService;
    private final SessionService sessionService;

    @Operation(
            summary = "Получение своего лога пульса",
            description = "Возвращает лог пульса текущего авторизованного пользователя с ролью MEMBER.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Лог успешно получен",
                            content = @Content(
                                    schema = @Schema(implementation = HeartbeatLogResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неавторизован",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 401,
                                          "message": "Неавторизованный запрос",
                                          "timestamp": 0
                                        }
                                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверная роль пользователя",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "message": "Неверная роль пользователя",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    )
            }
    )
    @GetMapping("/self")
    public HeartbeatLogResponse getSelfHeartbeatLog() {
        return heartbeatLogService.getHeartbeatLog(sessionService.getCurrentUserId());
    }

    @Operation(
            summary = "Получение лога пульса пользователя",
            description = "Возвращает лог пульса пользователя с ролью MEMBER. Доступно самому пользователю или подтвержденному контакту KEEPER.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Лог успешно получен",
                            content = @Content(
                                    schema = @Schema(implementation = HeartbeatLogResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверная роль пользователя",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "message": "Неверная роль пользователя",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неавторизован",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 401,
                                          "message": "Неавторизованный запрос",
                                          "timestamp": 0
                                        }
                                        """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Нет прав на получение лога",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 403,
                                          "message": "Нет прав на получение лога",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "message": "Неверный ID контакта",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public HeartbeatLogResponse getHeartbeatLog(
            @Parameter(description = "ID пользователя")
            @PathVariable(name = "id") long id
    ) {
        return heartbeatLogService.getHeartbeatLog(id);
    }

    @Operation(
            summary = "Отметка пульса",
            description = "Обновляет время последнего нажатия кнопки пульса. При наличии координат обновляет местоположение.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Лог успешно обновлен"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Неавторизован",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 401,
                                          "message": "Неавторизованный запрос",
                                          "timestamp": 0
                                        }
                                        """
                                    )
                            )
                    )
            }
    )
    @PutMapping("/tap")
    public ResponseEntity<?> tapHeartbeatLog(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Координаты пользователя. Поля могут быть null.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LatLonRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "С координатами",
                                            value = """
                                                {
                                                  "lat": 55.751244,
                                                  "lon": 37.618423
                                                }
                                                """
                                    ),
                                    @ExampleObject(
                                            name = "Без координат",
                                            value = """
                                                {
                                                  "lat": null,
                                                  "lon": null
                                                }
                                                """
                                    )
                            }
                    )
            )
            @RequestBody LatLonRequest latLonRequest
    ) {
        heartbeatLogService.tapHeartbeat(latLonRequest);
        return ResponseEntity.noContent().build();
    }
}
