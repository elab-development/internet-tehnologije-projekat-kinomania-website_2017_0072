/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.controllers;

import com.borak.kinweb.backend.domain.dto.user.UserLoginDTO;
import com.borak.kinweb.backend.domain.dto.user.UserRegisterDTO;
import com.borak.kinweb.backend.exceptions.handler.ErrorDetail;
import com.borak.kinweb.backend.logic.services.validation.DomainValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.borak.kinweb.backend.logic.services.auth.IAuthService;
import com.borak.kinweb.backend.logic.services.user.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Mr. Poyo
 */
//@CrossOrigin(originPatterns = {"http://localhost:*"},
//        maxAge = 3600,
//        allowCredentials = "true",
//        allowedHeaders = "*",
//        methods = {RequestMethod.PUT, RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
//        exposedHeaders = "*")
@Tag(name = "Users")
@RestController
@RequestMapping(path = "api/users")
@Validated
public class UserController {

    @Autowired
    private IUserService<Long> userService;

    //=========================POST MAPPINGS==================================
    @Operation(
            summary = "Add media to library",
            description = "Adds given media to users library based on its media id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of media (1..N)",
                        in = ParameterIn.PATH,
                        required = true,
                        example = "1",
                        schema = @Schema(type = "long", minimum = "1")
                )
            },
            responses = {
                @ApiResponse(
                        responseCode = "204",
                        description = "Success",
                        content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Invalid url path variable",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case path variable set as neggative value",
                                                value = "{\"timestamp\":1707298943423,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Media id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is not logged in",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Media with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case media with id as 1 was not found",
                                                value = "{\"timestamp\":1707299117443,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Media with id: 1 does not exist in database!\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Media with specified id is already in users library",
                        responseCode = "409",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case media with id as 22 was already in users library",
                                                value = "{\"timestamp\":1707300064257,\"title\":\"Duplicate resource\",\"status\":409,\"developer_message\":\"com.borak.kinweb.backend.exceptions.DuplicateResourceException\",\"details\":[\"Duplicate user library entry! Media with id: 22 already present!\"]}"
                                        )
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
    @SecurityRequirement(name = "jwtAuth")
    @PostMapping(path = "/library/{id}")
    public ResponseEntity postMediaIntoLibrary(@PathVariable @Min(value = 1, message = "Media id must be greater than or equal to 1") long id) {
        return userService.postMediaIntoLibrary(id);
    }

    //=========================PUT MAPPINGS==================================  
    //=========================DELETE MAPPINGS==================================
    @Operation(
            summary = "Remove media from library",
            description = "Removes users media from its library, based on its media id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of media (1..N)",
                        in = ParameterIn.PATH,
                        required = true,
                        example = "1",
                        schema = @Schema(type = "long", minimum = "1")
                )
            },
            responses = {
                @ApiResponse(
                        responseCode = "204",
                        description = "Success",
                        content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Invalid url path variable",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case path variable set as neggative value",
                                                value = "{\"timestamp\":1707298943423,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Media id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is not logged in",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Media with specified id is not in users library, or it doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case media with id as 22 was not in users library",
                                                value = "{\"timestamp\":1707299995205,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Media with id: 22 not present in users library!\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case media with id as 1 was not found",
                                                value = "{\"timestamp\":1707299117443,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Media with id: 1 does not exist in database!\"]}"
                                        )
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
    @SecurityRequirement(name = "jwtAuth")
    @DeleteMapping(path = "/library/{id}")
    public ResponseEntity deleteMediaFromLibrary(@PathVariable @Min(value = 1, message = "Media id must be greater than or equal to 1") long id) {
        return userService.deleteMediaFromLibrary(id);
    }

}
