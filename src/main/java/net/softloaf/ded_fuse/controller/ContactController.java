package net.softloaf.ded_fuse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.request.NewTrustedContactRequest;
import net.softloaf.ded_fuse.dto.response.ErrorResponse;
import net.softloaf.ded_fuse.dto.response.TrustedContactResponse;
import net.softloaf.ded_fuse.service.TrustedContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Контакты", description = "Управление доверенными контактами пользователя")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final TrustedContactService trustedContactService;

    @Operation(
            summary = "Получение списка контактов",
            description = "Возвращает список доверенных контактов текущего авторизованного пользователя.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список контактов успешно получен",
                            content = @Content(
                                    schema = @Schema(implementation = TrustedContactResponse.class)
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
    @GetMapping("/get")
    public List<TrustedContactResponse> getTrustedContacts() {
        return trustedContactService.getUserTrustedContacts();
    }

    @Operation(
            summary = "Добавление контакта",
            description = "Создает новый доверенный контакт между текущим пользователем и пользователем с ролью MEMBER.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Контакт успешно создан"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Неверная роль контакта",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "message": "Неверная роль контакта",
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
                            responseCode = "404",
                            description = "Контакт не найден",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "message": "Несуществующий контакт",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Контакт уже существует",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 409,
                                          "message": "Контакт уже существует",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    )
            }
    )
    @PostMapping("/add")
    public ResponseEntity<?> addTrustedContact(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Username пользователя с ролью MEMBER",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = NewTrustedContactRequest.class),
                            examples = @ExampleObject(value = """
                                {
                                  "memberUsername": "+79991234567"
                                }
                                """)
                    )
            )
            @RequestBody NewTrustedContactRequest trustedContactDto
    ) {
        trustedContactService.addTrustedContact(trustedContactDto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Подтверждение контакта",
            description = "Подтверждает доверенный контакт со стороны пользователя MEMBER.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Контакт успешно подтвержден"
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
                            description = "Нет прав на подтверждение",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                        {
                                          "status": 403,
                                          "message": "Нет прав принятие",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Контакт не найден",
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
    @PatchMapping("/{id}/respond")
    public ResponseEntity<?> acceptTrustedContact(
            @Parameter(description = "ID контакта")
            @PathVariable(name = "id") long id
    ) {
        trustedContactService.acceptTrustedContact(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Удаление контакта",
            description = "Удаляет доверенный контакт. Доступно участникам контакта.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Контакт успешно удален"
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
                                          "message": "Нет прав удаление",
                                          "timestamp": 0
                                        }
                                        """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Контакт не найден",
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
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteTrustedContact(
            @Parameter(description = "ID контакта")
            @PathVariable(name = "id") long id
    ) {
        trustedContactService.deleteTrustedContact(id);
        return ResponseEntity.noContent().build();
    }
}
