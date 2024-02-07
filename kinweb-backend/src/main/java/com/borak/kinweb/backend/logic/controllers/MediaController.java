/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.controllers;

import com.borak.kinweb.backend.exceptions.handler.ErrorDetail;
import com.borak.kinweb.backend.logic.services.media.IMediaService;
import com.borak.kinweb.backend.logic.transformers.serializers.views.JsonVisibilityViews;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mr. Poyo
 */
//@CrossOrigin(originPatterns = {"http://localhost:*"},maxAge = 3600,allowCredentials = "true")
@Tag(name = "Search")
@RestController
@RequestMapping(path = "api/medias")
@Validated
public class MediaController {

    @Autowired
    private IMediaService mediaService;

    //=========================GET MAPPINGS==================================
    @Operation(
            summary = "Search for media by their title",
            description = "Retreives media data with their genres, filtered by its title, page number and page size",
            parameters = {
                @Parameter(
                        name = "title",
                        description = "Title of media",
                        in = ParameterIn.QUERY,
                        required = false,
                        example = "",
                        schema = @Schema(type = "string", defaultValue = "")),
                @Parameter(
                        name = "page",
                        description = "Page index (1..N)",
                        in = ParameterIn.QUERY,
                        required = false,
                        example = "1",
                        schema = @Schema(type = "integer", defaultValue = "1", minimum = "1")
                ),
                @Parameter(
                        name = "size",
                        description = "Size of page (1..N)",
                        in = ParameterIn.QUERY,
                        required = false,
                        example = "10",
                        schema = @Schema(type = "integer", defaultValue = "10", minimum = "1"))
            },
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
                                                description = "In case size is set to 2 and title contains 'Mul'",
                                                value = "[{\"id\":114,\"title\":\"Two Mules for Sister Sara\",\"release_date\":\"2/03/1970\",\"cover_image_url\":\"http://localhost:8080/images/media/114.jpg\",\"audience_rating\":69,\"media_type\":\"movie\",\"genres\":[{\"id\":27,\"name\":\"Western\"}]},{\"id\":665,\"title\":\"Mulan\",\"release_date\":\"18/06/1998\",\"cover_image_url\":\"http://localhost:8080/images/media/665.jpg\",\"audience_rating\":79,\"media_type\":\"movie\",\"genres\":[{\"id\":3,\"name\":\"Adventure\"},{\"id\":4,\"name\":\"Animation\"},{\"id\":9,\"name\":\"Family\"}]}]")
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "Invalid url parameters",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case both query parameters are set as neggative",
                                                value = "{\"timestamp\":1707141245976,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Page number has to be greater than or equal to 1\",\"Size number has to be greater than or equal to 1\"]}"
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
    @GetMapping(path = "/search")
    @JsonView(JsonVisibilityViews.Lite.class)
    public ResponseEntity getMediasByTitle(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size,
            @RequestParam(name = "title", defaultValue = "", required = false) String title) {
        return mediaService.getAllMediasByTitleWithGenresPaginated(page, size, title);
    }

}
