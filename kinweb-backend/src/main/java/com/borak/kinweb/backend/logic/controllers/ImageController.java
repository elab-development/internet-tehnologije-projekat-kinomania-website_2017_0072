/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.controllers;

import com.borak.kinweb.backend.domain.classes.MyImage;
import com.borak.kinweb.backend.exceptions.InvalidInputException;
import com.borak.kinweb.backend.exceptions.ResourceNotFoundException;
import com.borak.kinweb.backend.exceptions.handler.ErrorDetail;
import com.borak.kinweb.backend.repository.util.FileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mr. Poyo
 */
@Tag(name = "Images")
@RestController
@RequestMapping(path = "images")
public class ImageController {

    @Autowired
    private FileRepository fileRepo;

    @Operation(
            summary = "Get media image",
            description = "Fetches a media image by its filename",
            parameters = {
                @Parameter(
                        name = "filename",
                        description = "Name of file including allowed extensions: [png,jpg,jpeg]",
                        in = ParameterIn.PATH,
                        required = true,
                        example = "1.jpg",
                        schema = @Schema(type = "string")
                )},
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Media image found",
                        content = {
                            @Content(mediaType = "image/*",
                                    schema = @Schema(
                                            implementation = Resource.class
                                    )
                            )
                        }
                ),
                @ApiResponse(
                        description = "Invalid filename",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case invalid filename structure was passed",
                                                value = "{\"timestamp\":1707231711063,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.InvalidInputException\",\"details\":[\"Invalid image name!\"]}"
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "Media image not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case media image with filename 1.jpg was not found",
                                                value = "{\"timestamp\":1707230619037,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No such image found!\"]}"
                                        )
                                    }
                            )
                        }
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
    @GetMapping("/media/{filename:.+}")
    public ResponseEntity getMediaImage(@PathVariable String filename) {
        String[] pom;
        try {
            pom = MyImage.extractNameAndExtension(filename);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid image name!");
        }
        Resource file = fileRepo.getMediaCoverImage(filename);
        if (file.exists() || file.isReadable()) {
            return ResponseEntity.ok().contentType(MyImage.parseContentType(pom[1])).body(file);
        } else {
            throw new ResourceNotFoundException("No such image found!");
        }
    }

    @Operation(
            summary = "Get person image",
            description = "Fetches a person image by its filename",
            parameters = {
                @Parameter(
                        name = "filename",
                        description = "Name of file including allowed extensions: [png,jpg,jpeg]",
                        in = ParameterIn.PATH,
                        required = true,
                        example = "1.jpg",
                        schema = @Schema(type = "string")
                )},
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Person image found",
                        content = {
                            @Content(mediaType = "image/*",
                                    schema = @Schema(
                                            implementation = Resource.class
                                    )
                            )
                        }
                ),
                @ApiResponse(
                        description = "Invalid filename",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case invalid filename structure was passed",
                                                value = "{\"timestamp\":1707231711063,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.InvalidInputException\",\"details\":[\"Invalid image name!\"]}"
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "Person image not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case person image with filename 1.jpg was not found",
                                                value = "{\"timestamp\":1707230619037,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No such image found!\"]}"
                                        )
                                    }
                            )
                        }
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
    @GetMapping("/person/{filename:.+}")
    public ResponseEntity getPersonImage(@PathVariable String filename) {
        String[] pom;
        try {
            pom = MyImage.extractNameAndExtension(filename);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid image name!");
        }
        Resource file = fileRepo.getPersonProfilePhoto(filename);
        if (file.exists() || file.isReadable()) {
            return ResponseEntity.ok().contentType(MyImage.parseContentType(pom[1])).body(file);
        } else {
            throw new ResourceNotFoundException("No such image found!");
        }
    }

    @Operation(
            summary = "Get user image",
            description = "Fetches a user image by its filename",
            parameters = {
                @Parameter(
                        name = "filename",
                        description = "Name of file including allowed extensions: [png,jpg,jpeg]",
                        in = ParameterIn.PATH,
                        required = true,
                        example = "default.png",
                        schema = @Schema(type = "string")
                )},
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "User image found",
                        content = {
                            @Content(mediaType = "image/*",
                                    schema = @Schema(
                                            implementation = Resource.class
                                    )
                            )
                        }
                ),
                @ApiResponse(
                        description = "Invalid filename",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case invalid filename structure was passed",
                                                value = "{\"timestamp\":1707231711063,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.InvalidInputException\",\"details\":[\"Invalid image name!\"]}"
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "User image not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case user image with filename 1.jpg was not found",
                                                value = "{\"timestamp\":1707230619037,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No such image found!\"]}"
                                        )
                                    }
                            )
                        }
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
    @GetMapping("/user/{filename:.+}")
    public ResponseEntity getUserImage(@PathVariable String filename) {
        String[] pom;
        try {
            pom = MyImage.extractNameAndExtension(filename);
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid image name!");
        }
        Resource file = fileRepo.getUserProfileImage(filename);
        if (file.exists() || file.isReadable()) {
            return ResponseEntity.ok().contentType(MyImage.parseContentType(pom[1])).body(file);
        } else {
            throw new ResourceNotFoundException("No such image found!");
        }
    }

}
