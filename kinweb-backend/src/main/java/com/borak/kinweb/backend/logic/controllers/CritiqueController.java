/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.controllers;

import com.borak.kinweb.backend.domain.dto.critique.CritiqueRequestDTO;
import com.borak.kinweb.backend.exceptions.handler.ErrorDetail;
import com.borak.kinweb.backend.logic.services.critique.ICritiqueService;
import com.borak.kinweb.backend.logic.services.validation.DomainValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mr. Poyo
 */
//@CrossOrigin(originPatterns = {"http://localhost:*"},maxAge = 3600,allowCredentials = "true")
@Tag(name = "Critiques")
@RestController
@RequestMapping(path = "api/critiques")
@Validated
public class CritiqueController {

    @Autowired
    private ICritiqueService critiqueService;

    @Autowired
    private DomainValidationService domainValidator;

    //=========================POST MAPPINGS==================================
    @Operation(
            summary = "Add critique",
            description = "Add new critique for given media by its id",
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
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Critique data",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CritiqueRequestDTO.class)
                    )),
            responses = {
                @ApiResponse(
                        responseCode = "205",
                        description = "Successfully added critique",
                        content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Critique json or media id invalid values sent",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case critique with an rating above 100 was sent",
                                                value = "{\"timestamp\":1707227170014,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ValidationException\",\"details\":[\"Critique rating must be between 0 and 100!\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case neggative media id value sent",
                                                value = "{\"timestamp\":1707227747338,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Media id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no critic or administrator privilege",
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
                                                value = "{\"timestamp\":1707226393201,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Media with id: 1 does not exist in database!\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Media with specified id already has a critique by given user",
                        responseCode = "409",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case media with id as 150 already has critique of given user",
                                                value = "{\"timestamp\":1707227444190,\"title\":\"Duplicate resource\",\"status\":409,\"developer_message\":\"com.borak.kinweb.backend.exceptions.DuplicateResourceException\",\"details\":[\"Duplicate critique for media with id: 150\"]}"
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
    @PostMapping(path = "/{id}")
    public ResponseEntity postCritique(
            @PathVariable @Min(value = 1, message = "Media id must be greater than or equal to 1") long id,
            @RequestBody CritiqueRequestDTO critiqueRequest) {
        domainValidator.validate(critiqueRequest);
        critiqueRequest.setMediaId(id);
        return critiqueService.postCritique(critiqueRequest);
    }

    //=========================PUT MAPPINGS==================================
    @Operation(
            summary = "Update critique",
            description = "Update existing critique for given media by its id",
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
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Critique data",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CritiqueRequestDTO.class)
                    )),
            responses = {
                @ApiResponse(
                        responseCode = "205",
                        description = "Successfully updated critique",
                        content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Critique json or media id invalid values sent",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case critique with an rating above 100 was sent",
                                                value = "{\"timestamp\":1707228130600,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ValidationException\",\"details\":[\"Critique rating must be between 0 and 100!\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case neggative media id value sent",
                                                value = "{\"timestamp\":1707228165857,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Media id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no critic or administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Media or critique of given media was not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case media with id as 1 was not found",
                                                value = "{\"timestamp\":1707228452811,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Media with id: 1 does not exist in database!\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case user doesn't have a critique of media with id 150",
                                                value = "{\"timestamp\":1707228488721,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Users critique for media with id: 150 does not exist in database!\"]}"
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
    @PutMapping(path = "/{id}")
    public ResponseEntity putMovie(
            @PathVariable @Min(value = 1, message = "Media id must be greater than or equal to 1") long id,
            @RequestBody CritiqueRequestDTO critiqueRequest) {
        domainValidator.validate(critiqueRequest);
        critiqueRequest.setMediaId(id);
        return critiqueService.putCritique(critiqueRequest);
    }

    //=========================DELETE MAPPINGS==================================
    @Operation(
            summary = "Delete critique of given media",
            description = "Deletes critique of given media by its id",
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
                        responseCode = "205",
                        description = "Successfully deleted critique",
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
                                                value = "{\"timestamp\":1707226330032,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Media id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no critic or administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Media or critique of given media was not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case media with id as 1 was not found",
                                                value = "{\"timestamp\":1707226393201,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Media with id: 1 does not exist in database!\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case user doesn't have a critique of media with id 150",
                                                value = "{\"timestamp\":1707227906429,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Users critique for media with id: 150 does not exist in database!\"]}"
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
    @DeleteMapping(path = "/{id}")
    public ResponseEntity deleteCritique(@PathVariable @Min(value = 1, message = "Media id must be greater than or equal to 1") long id) {
        return critiqueService.deleteCritique(id);
    }

}
