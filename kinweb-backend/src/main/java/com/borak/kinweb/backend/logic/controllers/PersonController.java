/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.controllers;

import com.borak.kinweb.backend.domain.classes.MyImage;
import com.borak.kinweb.backend.domain.dto.person.PersonRequestDTO;
import com.borak.kinweb.backend.domain.dto.person.PersonResponseDTO;
import com.borak.kinweb.backend.domain.enums.Gender;
import com.borak.kinweb.backend.exceptions.DatabaseException;
import com.borak.kinweb.backend.exceptions.handler.ErrorDetail;
import com.borak.kinweb.backend.logic.services.person.IPersonService;
import com.borak.kinweb.backend.logic.services.validation.DomainValidationService;
import com.borak.kinweb.backend.logic.transformers.serializers.views.JsonVisibilityViews;
import com.fasterxml.jackson.annotation.JsonView;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Mr. Poyo
 */
//@CrossOrigin(originPatterns = {"http://localhost:*"},maxAge = 3600,allowCredentials = "true")
@Tag(name = "Persons")
@RestController
@RequestMapping(path = "api/persons")
@Validated
public class PersonController {

    @Autowired
    private IPersonService personService;

    @Autowired
    private DomainValidationService domainValidator;

    //=========================GET MAPPINGS==================================
    @Operation(
            summary = "Get persons data",
            description = "Retreives persons data, filtered by page number and page size",
            parameters = {
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
                                                description = "In case size is set to 2",
                                                value = "[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"gender\":\"male\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\"},{\"id\":2,\"first_name\":\"Dodie\",\"last_name\":\"Smith\",\"gender\":\"other\",\"profile_photo_url\":null}]")
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
    @GetMapping
    @JsonView(JsonVisibilityViews.Lite.class)
    public ResponseEntity getPersons(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return personService.getAllPersonsPaginated(page, size);
    }

    @Operation(
            summary = "Get persons details data",
            description = "Retreives persons data with their professions, what media they worked on and what they did, filtered by page number and page size",
            parameters = {
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
                                                description = "In case size is set to 2",
                                                value = "[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"gender\":\"male\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"professions\":[{\"name\":\"director\",\"worked_on\":[131,169,241,361,684,746,804,1257,1346,1662]},{\"name\":\"writer\",\"worked_on\":[131,169,241,302,321,361,382,478,483,684,746,804,872,1007,1030,1053,1079,1092,1177,1346,1561,1610,1633,1645,1662]},{\"name\":\"actor\",\"is_star\":false,\"worked_on\":[{\"media_id\":382,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Missionary (uncredited)\"}]},{\"media_id\":524,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Man Kissing on Bridge (uncredited)\"}]},{\"media_id\":642,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Alien on TV Monitor (uncredited)\"}]},{\"media_id\":804,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Baron Papanoida (uncredited)\"}]},{\"media_id\":1662,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"},{\"id\":3,\"name\":\"role3\"}]}]}]},{\"id\":2,\"first_name\":\"Dodie\",\"last_name\":\"Smith\",\"gender\":\"other\",\"profile_photo_url\":null,\"professions\":[{\"name\":\"writer\",\"worked_on\":[634,1129,1662]}]}]"
                                        )
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
    @GetMapping(path = "/details")
    @JsonView(JsonVisibilityViews.Heavy.class)
    public ResponseEntity getPersonsDetails(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return personService.getAllPersonsWithDetailsPaginated(page, size);
    }

    @Operation(
            summary = "Get person data",
            description = "Retreives person data with his professions by specified person id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of person (1..N)",
                        in = ParameterIn.PATH,
                        required = true,
                        example = "1",
                        schema = @Schema(type = "long", minimum = "1")
                )
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
                                                description = "In case person with id as 1 was found",
                                                value = "{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"gender\":\"male\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"professions\":[{\"name\":\"director\"},{\"name\":\"writer\"},{\"name\":\"actor\",\"is_star\":false}]}")
                                    }
                            )
                        }
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
                                                value = "{\"timestamp\":1707239243727,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Person id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Person with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case person with id as 100000 was not found",
                                                value = "{\"timestamp\":1707239326324,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No person found with id: 100000\"]}"
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
    @GetMapping(path = "/{id}")
    @JsonView(JsonVisibilityViews.Medium.class)
    public ResponseEntity getPersonById(@PathVariable @Min(value = 1, message = "Person id must be greater than or equal to 1") long id) {
        return personService.getPersonWithProfessions(id);
    }

    @Operation(
            summary = "Get person data with details",
            description = "Retreives person data with his professions, what media they worked on and what they did, by specified person id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of person (1..N)",
                        in = ParameterIn.PATH,
                        required = true,
                        example = "1",
                        schema = @Schema(type = "long", minimum = "1")
                )
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
                                                description = "In case person with id as 1 was found",
                                                value = "{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"gender\":\"male\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"professions\":[{\"name\":\"director\",\"worked_on\":[131,169,241,361,684,746,804,1257,1346,1662]},{\"name\":\"writer\",\"worked_on\":[131,169,241,302,321,361,382,478,483,684,746,804,872,1007,1030,1053,1079,1092,1177,1346,1561,1610,1633,1645,1662]},{\"name\":\"actor\",\"is_star\":false,\"worked_on\":[{\"media_id\":382,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Missionary (uncredited)\"}]},{\"media_id\":524,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Man Kissing on Bridge (uncredited)\"}]},{\"media_id\":642,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Alien on TV Monitor (uncredited)\"}]},{\"media_id\":804,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Baron Papanoida (uncredited)\"}]},{\"media_id\":1662,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"},{\"id\":3,\"name\":\"role3\"}]}]}]}"
                                        )
                                    }
                            )
                        }
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
                                                value = "{\"timestamp\":1707239609422,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Person id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Person with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case person with id as 100000 was not found",
                                                value = "{\"timestamp\":1707239640940,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No person found with id: 100000\"]}"
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
    @GetMapping(path = "/{id}/details")
    @JsonView(JsonVisibilityViews.Heavy.class)
    public ResponseEntity getPersonByIdDetails(@PathVariable @Min(value = 1, message = "Person id must be greater than or equal to 1") long id) {
        return personService.getPersonWithDetails(id);
    }

    //=========================POST MAPPINGS==================================
    @Operation(
            summary = "Create new person",
            description = "Create new person entry along with his profesions which includes whether or not he was a director, writer or actor, and on which media he worked on and what role he had. Profile photo file can optionaly be saved aswell. Allowed extensions are [png,jpg,jpeg] and max file size is 8MB",
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
                                                description = "In case person was saved successfully",
                                                value = "{\"id\":48618,\"first_name\":\"Pablo\",\"last_name\":\"Lucas\",\"gender\":\"male\",\"profile_photo_url\":null,\"professions\":[{\"name\":\"director\",\"worked_on\":[131,169]},{\"name\":\"writer\",\"worked_on\":[131,169]},{\"name\":\"actor\",\"is_star\":true,\"worked_on\":[{\"media_id\":382,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"}]},{\"media_id\":524,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"}]}]}]}"
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "Person json structure validation error occured",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case person with empty first name was sent",
                                                value = "{\"timestamp\":1707241644404,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ValidationException\",\"details\":[\"Person first name must not be null or empty!\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Person dependency object was not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case provided worked on media, as director, id of 100000 was not found",
                                                value = "{\"timestamp\":1707241890831,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Directors worked on media, with id: 100000 does not exist in database!\"]}"
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @JsonView(JsonVisibilityViews.Heavy.class)
    public ResponseEntity postPerson(@Parameter(description = "Person data in JSON format. Example: {\"first_name\":\"Pablo\",\"last_name\":\"Lucas\",\"gender\":\"male\",\"professions\":[{\"name\":\"director\",\"worked_on\":[131,169]},{\"name\":\"writer\",\"worked_on\":[131,169]},{\"name\":\"actor\",\"is_star\":true,\"worked_on\":[{\"media_id\":382,\"is_starring\":false,\"roles\":[\"role1\",\"role2\"]},{\"media_id\":524,\"is_starring\":false,\"roles\":[\"role1\"]}]}]}",
            required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
            @RequestPart("person") PersonRequestDTO person,
            @Parameter(description = "Profile photo", required = false, content = @Content(mediaType = "application/octet-stream"))
            @RequestPart(name = "profile_photo", required = false) MultipartFile profilePhoto) {
        domainValidator.validate(person, profilePhoto, RequestMethod.POST);
        if (profilePhoto != null) {
            person.setProfilePhoto(new MyImage(profilePhoto));
        }
        return personService.postPerson(person);
    }

    //=========================PUT MAPPINGS===================================
    @SecurityRequirement(name = "jwtAuth")
    @Operation(
            summary = "Update given person",
            description = "Update person entry along with his profesions, media he worked on and roles he had, by specified person id. Profile photo file can optionaly be updated aswell. Allowed extensions are [png,jpg,jpeg] and max file size is 8MB",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of person (1..N)",
                        in = ParameterIn.PATH,
                        required = true,
                        example = "1",
                        schema = @Schema(type = "long", minimum = "1")
                )
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
                                                description = "In case person was updated successfully",
                                                value = "{\"id\":48619,\"first_name\":\"Pablo\",\"last_name\":\"Lucas\",\"gender\":\"male\",\"profile_photo_url\":null,\"professions\":[{\"name\":\"director\",\"worked_on\":[10,169]},{\"name\":\"writer\",\"worked_on\":[131,169]},{\"name\":\"actor\",\"is_star\":true,\"worked_on\":[{\"media_id\":382,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"}]},{\"media_id\":524,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"}]}]}]}"
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "Invalid input error occured",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                         @ExampleObject(
                                                name = "Example 1",
                                                description = "In case path variable set as neggative value",
                                                value = "{\"timestamp\":1707239609422,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Person id must be greater than or equal to 1\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case person with empty first name was sent",
                                                value = "{\"timestamp\":1707242532448,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ValidationException\",\"details\":[\"Person first name must not be null or empty!\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Person dependency object or person itself was not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case person with id 48620 was not found",
                                                value = "{\"timestamp\":1707242606566,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Person with id: 48620 does not exist in database!\"]}"),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case provided worked on media, as director, id of 100000 was not found",
                                                value = "{\"timestamp\":1707242715078,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Directors worked on media, with id: 100000 does not exist in database!\"]}"
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
    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @JsonView(JsonVisibilityViews.Heavy.class)
    public ResponseEntity putPerson(
            @PathVariable @Min(value = 1, message = "Person id must be greater than or equal to 1") long id,
            @Parameter(description = "Person data in JSON format. Example: {\"first_name\":\"Pablo\",\"last_name\":\"Lucas\",\"gender\":\"male\",\"professions\":[{\"name\":\"director\",\"worked_on\":[131,169]},{\"name\":\"writer\",\"worked_on\":[131,169]},{\"name\":\"actor\",\"is_star\":true,\"worked_on\":[{\"media_id\":382,\"is_starring\":false,\"roles\":[\"role1\",\"role2\"]},{\"media_id\":524,\"is_starring\":false,\"roles\":[\"role1\"]}]}]}",
                    required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
            @RequestPart("person") PersonRequestDTO person,
            @Parameter(description = "Profile photo", required = false, content = @Content(mediaType = "application/octet-stream"))
            @RequestPart(name = "profile_photo", required = false) MultipartFile profilePhoto) {
        person.setId(id);
        domainValidator.validate(person, profilePhoto, RequestMethod.PUT);
        if (profilePhoto != null) {
            person.setProfilePhoto(new MyImage(profilePhoto));
        }
        return personService.putPerson(person);
    }

    //=========================DELETE MAPPINGS================================
    @Operation(
            summary = "Delete person with specified id",
            description = "Deletes person data by his specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of person (1..N)",
                        in = ParameterIn.PATH,
                        required = true,
                        example = "1",
                        schema = @Schema(type = "long", minimum = "1")
                )
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
                                                description = "In case person with id as 48619 was deleted successfully",
                                                value = "{\"id\":48619,\"first_name\":\"Pablo\",\"last_name\":\"Lucas\",\"gender\":\"male\",\"profile_photo_url\":null,\"professions\":[{\"name\":\"director\",\"worked_on\":[10,169]},{\"name\":\"writer\",\"worked_on\":[131,169]},{\"name\":\"actor\",\"is_star\":true,\"worked_on\":[{\"media_id\":382,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"}]},{\"media_id\":524,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"}]}]}]}"
                                        )
                                    }
                            )
                        }
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
                                                value = "{\"timestamp\":1707242901645,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Person id must be greater than or equal to 1\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Person with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case person with id as 48620 was not found",
                                                value = "{\"timestamp\":1707242965171,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No person found with id: 48620\"]}"
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
    @JsonView(JsonVisibilityViews.Heavy.class)
    public ResponseEntity deletePersonById(@PathVariable @Min(value = 1, message = "Person id must be greater than or equal to 1") long id) {
        return personService.deletePersonById(id);
    }

}
