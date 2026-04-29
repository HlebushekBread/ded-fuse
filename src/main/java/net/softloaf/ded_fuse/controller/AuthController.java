package net.softloaf.ded_fuse.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.softloaf.ded_fuse.dto.request.NewUserRequest;
import net.softloaf.ded_fuse.dto.response.ErrorResponse;
import net.softloaf.ded_fuse.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;
import org.springframework.security.web.authentication.ott.OneTimeTokenGenerationSuccessHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Аутентификация", description = "Регистрация и вход")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;
    private final OneTimeTokenService oneTimeTokenService;
    private final OneTimeTokenGenerationSuccessHandler oneTimeTokenGenerationSuccessHandler;

    @Operation(
            summary = "Регистрация пользователя",
            description = "Создает нового пользователя по номеру телефона, имени и роли. После успешной регистрации отправляет одноразовый код для входа.",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Пользователь успешно зарегистрирован, код отправлен"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Username не является номером телефона",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "message": "Username должен быть номером телефона",
                                      "timestamp": 0
                                    }
                                    """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Роль не существует",
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
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Пользователь уже существует",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "status": 409,
                                      "message": "Пользователь уже существует",
                                      "timestamp": 0
                                    }
                                    """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Username равен null",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "status": 422,
                                      "message": "Username не может быть null",
                                      "timestamp": 0
                                    }
                                    """)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Ошибка отправки одноразового кода",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponse.class),
                                    examples = @ExampleObject(value = """
                                    {
                                      "status": 500,
                                      "message": "Ошибка отправки кода",
                                      "timestamp": 0
                                    }
                                    """)
                            )
                    )
            }
    )
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные нового пользователя",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = NewUserRequest.class),
                            examples = @ExampleObject(value = """
                            {
                              "username": "+79991234567",
                              "fullName": "Иван Иванов",
                              "role": "MEMBER"
                            }
                            """)
                    )
            )
            @RequestBody NewUserRequest newUserRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        userService.saveNewUser(newUserRequest);

        GenerateOneTimeTokenRequest ottRequest =
                new GenerateOneTimeTokenRequest(
                        newUserRequest.getUsername().replaceAll("[\\s\\-\\(\\)]", "")
                );

        OneTimeToken ott = oneTimeTokenService.generate(ottRequest);

        try {
            oneTimeTokenGenerationSuccessHandler.handle(request, response, ott);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка отправки кода");
        }

        return ResponseEntity.noContent().build();
    }
}
