/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.controllers;

import com.borak.kinweb.backend.domain.classes.MyImage;
import com.borak.kinweb.backend.domain.dto.user.UserLoginDTO;
import com.borak.kinweb.backend.domain.dto.user.UserRegisterDTO;
import com.borak.kinweb.backend.exceptions.handler.ErrorDetail;
import com.borak.kinweb.backend.logic.services.validation.DomainValidationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.borak.kinweb.backend.logic.services.auth.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;

/**
 *
 * @author Mr. Poyo
 */
//@CrossOrigin(origins = "*", maxAge = 3600)
//@CrossOrigin(originPatterns = {"http://localhost:*"},maxAge = 3600,allowCredentials = "true")
@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private IAuthService<UserRegisterDTO, UserLoginDTO> userService;

    @Autowired
    private DomainValidationService domainValidator;

    @Operation(
            summary = "Register user",
            description = "Register new user account. Profile image file can optionaly be saved aswell. Allowed extensions are [png,jpg,jpeg] and max file size is 8MB",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case user registered successfully",
                                                value = "{\"message\":\"User registered successfully!\"}"
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "In case user already exists",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case user with given username already exists",
                                                value = "{\"timestamp\":1707220278694,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.UsernameTakenException\",\"details\":[\"Username is already taken!\"]}")}
                            )}
                ),
                @ApiResponse(
                        description = "User data constraint violatione exception occured",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "User dependency object was not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case provided country id of 1000 was not found",
                                                value = "{\"timestamp\":1707220681685,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Country with id: 1000 does not exist in database!\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Internal server error",
                        responseCode = "500",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case unexpected error occured",
                                                value = "{\"timestamp\":1707141245976,\"title\":\"Internal Server Error\",\"status\":500,\"developer_message\":\"jakarta.validation.DatabaseException\",\"details\":[\"Unexpected exception\"]}"
                                        )
                                    }
                            )}
                )
            }
    )
    @PostMapping(path = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @Parameter(description = "User data in JSON format. Example: {\"first_name\":\"Petar\",\"last_name\":\"Petrovic\",\"gender\":\"male\",\"profile_name\":\"pera_petrov\",\"email\":\"pera@gmail.com\",\"username\":\"pera\",\"password\":\"pera\",\"role\":\"CRITIC\",\"country_id\":1}",
                    required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
            @Valid @RequestPart(name = "user") UserRegisterDTO registerForm,
            @Parameter(description = "Profile Image", required = false, content = @Content(mediaType = "application/octet-stream"))
            @RequestPart(name = "profile_image", required = false) MultipartFile profileImage) {
        domainValidator.validate(registerForm, profileImage);
        if (profileImage != null) {
            registerForm.setProfileImage(new MyImage(profileImage));
        }
        return userService.register(registerForm);
    }

    @Operation(
            summary = "Login user",
            description = "Login user account by sending its credentials",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "User credentials",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserLoginDTO.class)
                    )),
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case user logged in successfully",
                                                value = "{\"first_name\":\"Admin\",\"last_name\":\"Admin\",\"gender\":\"other\",\"profile_name\":\"Admin\",\"profile_image_url\":\"http://localhost:8080/images/user/default.png\",\"country\":{\"id\":198,\"name\":\"Serbia\",\"officialStateName\":\"The Republic of Serbia\",\"code\":\"RS\"},\"medias\":[],\"critiques\":[{\"media\":{\"id\":25,\"title\":\"How the Grinch Stole Christmas!\",\"release_date\":\"18/12/1966\",\"cover_image_url\":\"http://localhost:8080/images/media/25.jpg\",\"description\":\"Bitter and hateful, the Grinch is irritated at the thought of a nearby village having a happy time celebrating Christmas. Disguised as Santa Claus, with his dog made to look like a reindeer, he decides to raid the village to steal all the Christmas things.\",\"audience_rating\":75,\"critics_rating\":15,\"media_type\":\"movie\",\"genres\":[{\"id\":4,\"name\":\"Animation\"},{\"id\":5,\"name\":\"Comedy\"},{\"id\":9,\"name\":\"Family\"}]},\"description\":\"bbbbbbbb\",\"rating\":15},{\"media\":{\"id\":1166,\"title\":\"John Wick: Chapter 4\",\"release_date\":\"22/03/2023\",\"cover_image_url\":\"http://localhost:8080/images/media/1166.jpg\",\"description\":\"With the price on his head ever increasing, John Wick uncovers a path to defeating The High Table. But before he can earn his freedom, Wick must face off against a new enemy with powerful alliances across the globe and forces that turn old friends into foes.\",\"audience_rating\":78,\"critics_rating\":50,\"media_type\":\"movie\",\"genres\":[{\"id\":1,\"name\":\"Action\"},{\"id\":6,\"name\":\"Crime\"},{\"id\":24,\"name\":\"Thriller\"}]},\"description\":\"Super movie!\",\"rating\":50}],\"role\":\"ADMINISTRATOR\"}"
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "User with given credentials doesn't exist in database",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Internal server error",
                        responseCode = "500",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case unexpected error occured",
                                                value = "{\"timestamp\":1707141245976,\"title\":\"Internal Server Error\",\"status\":500,\"developer_message\":\"jakarta.validation.DatabaseException\",\"details\":[\"Unexpected exception\"]}"
                                        )
                                    }
                            )}
                )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO loginRequest) {
        return userService.login(loginRequest);
    }

    @SecurityRequirement(name = "jwtAuth")
    @Operation(
            summary = "Logout user",
            description = "Logout currently logged in user by sending an empty cookie",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Success",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case user logged out successfully",
                                                value = "{\"message\":\"You've been logged out!\"}"
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "User is not logged in",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                )

            }
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return userService.logout();
    }

}
