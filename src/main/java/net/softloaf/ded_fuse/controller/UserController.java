package net.softloaf.ded_fuse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.request.LatLonRequest;
import net.softloaf.ded_fuse.dto.response.ErrorResponse;
import net.softloaf.ded_fuse.dto.response.UserBasicResponse;
import net.softloaf.ded_fuse.dto.response.UserDetailedResponse;
import net.softloaf.ded_fuse.service.SessionService;
import net.softloaf.ded_fuse.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Пользователи", description = "Управление пользователями системы")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;

    @Operation(
            summary = "Получение пользователя по ID",
            description = "Возвращает детальную информацию о пользователе.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь найден",
                            content = @Content(
                                    schema = @Schema(implementation = UserDetailedResponse.class)
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
                                          "message": "Несуществующий ID",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    )
            }
    )
    @GetMapping("/{id}")
    public UserDetailedResponse getUser(
            @Parameter(description = "ID пользователя")
            @PathVariable(name = "id") long id
    ) {
        return userService.findById(id);
    }

    @Operation(
            summary = "Получение текущего пользователя",
            description = "Возвращает детальную информацию о текущем авторизованном пользователе.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Информация успешно получена",
                            content = @Content(
                                    schema = @Schema(implementation = UserDetailedResponse.class)
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
                    )
            }
    )
    @GetMapping("/self")
    public UserDetailedResponse getSelf() {
        return userService.findById(sessionService.getCurrentUserId());
    }

    @Operation(
            summary = "Получение списка MEMBER пользователей",
            description = "Возвращает список всех пользователей с ролью MEMBER.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список пользователей получен",
                            content = @Content(
                                    schema = @Schema(implementation = UserBasicResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Роль MEMBER не найдена",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "message": "Несуществующая кодировка роли",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    )
            }
    )
    @GetMapping("/members")
    public List<UserBasicResponse> getMembers() {
        return userService.findAllByRoleName("MEMBER");
    }

    @Operation(
            summary = "Удаление текущего пользователя",
            description = "Удаляет аккаунт текущего авторизованного пользователя.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Пользователь успешно удален"
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
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteSelf() {
        userService.deleteUser(sessionService.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Удаление пользователя по ID",
            description = "Удаляет пользователя по ID. Сейчас доступно только удаление самого себя.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Пользователь успешно удален"
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
                            description = "Нет прав на удаление",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 403,
                                          "message": "Нет прав на удаление",
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
                                          "message": "Несуществующий ID",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    )
            }
    )
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID пользователя")
            @PathVariable(name = "id") long id
    ) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Обновление позиции пользователя",
            description = "Обновляет последнее известное местоположение пользователя. Если координаты null — позиция не изменяется.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Позиция успешно обновлена"
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
    @PatchMapping("/pos")
    public ResponseEntity<?> backgroundUpdate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Координаты пользователя",
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
        userService.backgroundUpdate(latLonRequest);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Обновление активности пользователя",
            description = "Обновляет время последнего открытия приложения пользователем.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Активность успешно обновлена"
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
    @PatchMapping("/active")
    public ResponseEntity<?> appOpenUpdate() {
        userService.appOpenUpdate();
        return ResponseEntity.noContent().build();
    }
}