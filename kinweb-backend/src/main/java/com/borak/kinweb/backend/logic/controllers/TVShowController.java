/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.controllers;

import com.borak.kinweb.backend.domain.classes.MyImage;
import com.borak.kinweb.backend.domain.dto.movie.MovieRequestDTO;
import com.borak.kinweb.backend.domain.dto.tv.TVShowRequestDTO;
import com.borak.kinweb.backend.exceptions.handler.ErrorDetail;
import com.borak.kinweb.backend.logic.services.movie.IMovieService;
import com.borak.kinweb.backend.logic.services.tv.ITVShowService;
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
import org.springframework.beans.factory.annotation.Autowired;
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
@Tag(name = "TV Shows")
@RestController
@RequestMapping(path = "api/tv")
@Validated
public class TVShowController {

    @Autowired
    private ITVShowService tvShowService;

    @Autowired
    private DomainValidationService domainValidator;

    //=========================GET MAPPINGS==================================  
    @Operation(
            summary = "Get tv shows data with their genres",
            description = "Retreives tv shows data with their genres, filtered by page number and page size",
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
                                                value = "[{\"id\":1181,\"title\":\"Malcolm in the Middle\",\"release_date\":\"9/01/2000\",\"cover_image_url\":\"http://localhost:8080/images/media/1181.jpg\",\"audience_rating\":85,\"genres\":[{\"id\":5,\"name\":\"Comedy\"},{\"id\":9,\"name\":\"Family\"}]},{\"id\":1183,\"title\":\"CSI: Crime Scene Investigation\",\"release_date\":\"6/10/2000\",\"cover_image_url\":\"http://localhost:8080/images/media/1183.jpg\",\"audience_rating\":76,\"genres\":[{\"id\":6,\"name\":\"Crime\"},{\"id\":8,\"name\":\"Drama\"},{\"id\":15,\"name\":\"Mystery\"}]}]"
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
    @GetMapping
    @JsonView(JsonVisibilityViews.Lite.class)
    public ResponseEntity getTVShows(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return tvShowService.getAllTVShowsWithGenresPaginated(page, size);
    }

    @Operation(
            summary = "Get popular tv shows data with their genres",
            description = "Retreives tv shows data with their genres, where each tv show has audience_rating greater than, or equal to 80, filtered by page number and page size",
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
                                                value = "[{\"id\":1181,\"title\":\"Malcolm in the Middle\",\"release_date\":\"9/01/2000\",\"cover_image_url\":\"http://localhost:8080/images/media/1181.jpg\",\"audience_rating\":85,\"genres\":[{\"id\":5,\"name\":\"Comedy\"},{\"id\":9,\"name\":\"Family\"}]},{\"id\":1184,\"title\":\"Fighting Spirit\",\"release_date\":\"3/10/2000\",\"cover_image_url\":\"http://localhost:8080/images/media/1184.jpg\",\"audience_rating\":87,\"genres\":[{\"id\":2,\"name\":\"Action & Adventure\"},{\"id\":4,\"name\":\"Animation\"},{\"id\":5,\"name\":\"Comedy\"},{\"id\":8,\"name\":\"Drama\"}]}]"
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
    @GetMapping(path = "/popular")
    @JsonView(JsonVisibilityViews.Lite.class)
    public ResponseEntity getTVShowsPopular(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return tvShowService.getAllTVShowsWithGenresPopularPaginated(page, size);
    }

    @Operation(
            summary = "Get current tv shows data with their genres",
            description = "Retreives tv shows data with their genres, where each tv show has its release_date year as the previous year, filtered by page number and page size",
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
                                                value = "[{\"id\":1642,\"title\":\"ONE PIECE\",\"release_date\":\"31/08/2023\",\"cover_image_url\":\"http://localhost:8080/images/media/1642.jpg\",\"audience_rating\":82,\"genres\":[{\"id\":2,\"name\":\"Action & Adventure\"},{\"id\":19,\"name\":\"Sci-Fi & Fantasy\"}]},{\"id\":1644,\"title\":\"Silo\",\"release_date\":\"4/05/2023\",\"cover_image_url\":\"http://localhost:8080/images/media/1644.jpg\",\"audience_rating\":82,\"genres\":[{\"id\":8,\"name\":\"Drama\"}]}]"
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
    @GetMapping(path = "/current")
    @JsonView(JsonVisibilityViews.Lite.class)
    public ResponseEntity getTVShowsCurrent(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return tvShowService.getAllTVShowsWithGenresCurrentPaginated(page, size);
    }

    @Operation(
            summary = "Get tv shows data with their details",
            description = "Retreives tv shows data with their genres, critiques, directors, writers, actors and their roles, filtered by page number and page size",
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
                                                value = "[{\"id\":1181,\"title\":\"Malcolm in the Middle\",\"release_date\":\"9/01/2000\",\"cover_image_url\":\"http://localhost:8080/images/media/1181.jpg\",\"description\":\"A gifted young teen tries to survive life with his dimwitted, dysfunctional family.\",\"audience_rating\":85,\"critics_rating\":null,\"number_of_seasons\":7,\"genres\":[{\"id\":5,\"name\":\"Comedy\"},{\"id\":9,\"name\":\"Family\"}],\"directors\":[{\"id\":32600,\"first_name\":\"Linwood\",\"last_name\":\"Boomer\",\"profile_photo_url\":\"http://localhost:8080/images/person/32600.jpg\",\"gender\":\"male\"}],\"writers\":[],\"actors\":[{\"id\":15544,\"first_name\":\"Erik\",\"last_name\":\"Per Sullivan\",\"profile_photo_url\":\"http://localhost:8080/images/person/15544.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Dewey\"}]},{\"id\":15732,\"first_name\":\"Bryan\",\"last_name\":\"Cranston\",\"profile_photo_url\":\"http://localhost:8080/images/person/15732.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Hal\"}]},{\"id\":23388,\"first_name\":\"Jane\",\"last_name\":\"Kaczmarek\",\"profile_photo_url\":\"http://localhost:8080/images/person/23388.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Lois\"}]},{\"id\":29227,\"first_name\":\"Christopher\",\"last_name\":\"Masterson\",\"profile_photo_url\":\"http://localhost:8080/images/person/29227.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Francis\"}]},{\"id\":37733,\"first_name\":\"James\",\"last_name\":\"Rodriguez\",\"profile_photo_url\":null,\"gender\":\"other\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Jamie\"}]},{\"id\":37739,\"first_name\":\"Lukas\",\"last_name\":\"Rodriguez\",\"profile_photo_url\":null,\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Jamie\"}]},{\"id\":39038,\"first_name\":\"Frankie\",\"last_name\":\"Muniz\",\"profile_photo_url\":\"http://localhost:8080/images/person/39038.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Malcolm\"}]},{\"id\":39039,\"first_name\":\"Justin\",\"last_name\":\"Berfield\",\"profile_photo_url\":\"http://localhost:8080/images/person/39039.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Reese\"}]}],\"critiques\":[]},{\"id\":1183,\"title\":\"CSI: Crime Scene Investigation\",\"release_date\":\"6/10/2000\",\"cover_image_url\":\"http://localhost:8080/images/media/1183.jpg\",\"description\":\"A Las Vegas team of forensic investigators are trained to solve criminal cases by scouring the crime scene, collecting irrefutable evidence and finding the missing pieces that solve the mystery.\",\"audience_rating\":76,\"critics_rating\":null,\"number_of_seasons\":15,\"genres\":[{\"id\":6,\"name\":\"Crime\"},{\"id\":8,\"name\":\"Drama\"},{\"id\":15,\"name\":\"Mystery\"}],\"directors\":[{\"id\":28573,\"first_name\":\"Ann\",\"last_name\":\"Donahue\",\"profile_photo_url\":null,\"gender\":\"female\"},{\"id\":42560,\"first_name\":\"Anthony\",\"last_name\":\"E. Zuiker\",\"profile_photo_url\":\"http://localhost:8080/images/person/42560.jpg\",\"gender\":\"male\"}],\"writers\":[{\"id\":42560,\"first_name\":\"Anthony\",\"last_name\":\"E. Zuiker\",\"profile_photo_url\":\"http://localhost:8080/images/person/42560.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":525,\"first_name\":\"Jorja\",\"last_name\":\"Fox\",\"profile_photo_url\":\"http://localhost:8080/images/person/525.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Sara Sidle\"}]},{\"id\":1918,\"first_name\":\"Elisabeth\",\"last_name\":\"Shue\",\"profile_photo_url\":\"http://localhost:8080/images/person/1918.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Julie Finlay\"}]},{\"id\":11667,\"first_name\":\"Elisabeth\",\"last_name\":\"Harnois\",\"profile_photo_url\":\"http://localhost:8080/images/person/11667.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Morgan Brody\"}]},{\"id\":11807,\"first_name\":\"Ted\",\"last_name\":\"Danson\",\"profile_photo_url\":\"http://localhost:8080/images/person/11807.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"D.B. Russell\"}]},{\"id\":15735,\"first_name\":\"Wallace\",\"last_name\":\"Langham\",\"profile_photo_url\":\"http://localhost:8080/images/person/15735.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"David Hodges\"}]},{\"id\":30747,\"first_name\":\"Robert\",\"last_name\":\"David Hall\",\"profile_photo_url\":\"http://localhost:8080/images/person/30747.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Al Robbins\"}]},{\"id\":31862,\"first_name\":\"Jon\",\"last_name\":\"Wellner\",\"profile_photo_url\":\"http://localhost:8080/images/person/31862.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Henry Andrews\"}]},{\"id\":34116,\"first_name\":\"Eric\",\"last_name\":\"Szmanda\",\"profile_photo_url\":\"http://localhost:8080/images/person/34116.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Greg Sanders\"}]},{\"id\":35430,\"first_name\":\"David\",\"last_name\":\"Berman\",\"profile_photo_url\":\"http://localhost:8080/images/person/35430.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"David Phillips\"}]},{\"id\":39425,\"first_name\":\"George\",\"last_name\":\"Eads\",\"profile_photo_url\":\"http://localhost:8080/images/person/39425.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Nick Stokes\"}]}],\"critiques\":[]}]"
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
    public ResponseEntity getTVShowsDetails(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return tvShowService.getAllTVShowsWithDetailsPaginated(page, size);
    }

    @Operation(
            summary = "Get tv show data with its genres",
            description = "Retreives tv show data with its genres by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of tv show (1..N)",
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
                                                description = "In case tv show with id as 1500 was found",
                                                value = "{\"id\":1500,\"title\":\"Ash vs Evil Dead\",\"release_date\":\"31/10/2015\",\"cover_image_url\":\"http://localhost:8080/images/media/1500.jpg\",\"description\":\"Bruce Campbell reprises his role as Ash Williams, an aging lothario and chainsaw-handed monster hunter who’s spent the last three decades avoiding maturity, and the terrors of the Evil Dead. But when a Deadite plague threatens to destroy all of mankind, he’s forced to face his demons — both metaphorical and literal.\",\"audience_rating\":77,\"critics_rating\":null,\"number_of_seasons\":3,\"genres\":[{\"id\":2,\"name\":\"Action & Adventure\"},{\"id\":5,\"name\":\"Comedy\"},{\"id\":19,\"name\":\"Sci-Fi & Fantasy\"}]}"
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
                                                value = "{\"timestamp\":1707296986650,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"TV show id must be greater than or equal to 1\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "TV show with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case tv show with id as 22 was not found",
                                                value = "{\"timestamp\":1707250797075,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No tv show found with id: 22\"]}"
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
    public ResponseEntity getTVShowById(@PathVariable @Min(value = 1, message = "TV show id must be greater than or equal to 1") long id) {
        return tvShowService.getTVShowWithGenres(id);
    }

    @Operation(
            summary = "Get tv show data with its details",
            description = "Retreives tv show data with its genres, critiques, directors, writers, actors and their roles, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of tv show (1..N)",
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
                                                description = "In case tv show with id as 1500 was found",
                                                value = "{\"id\":1500,\"title\":\"Ash vs Evil Dead\",\"release_date\":\"31/10/2015\",\"cover_image_url\":\"http://localhost:8080/images/media/1500.jpg\",\"description\":\"Bruce Campbell reprises his role as Ash Williams, an aging lothario and chainsaw-handed monster hunter who’s spent the last three decades avoiding maturity, and the terrors of the Evil Dead. But when a Deadite plague threatens to destroy all of mankind, he’s forced to face his demons — both metaphorical and literal.\",\"audience_rating\":77,\"critics_rating\":null,\"number_of_seasons\":3,\"genres\":[{\"id\":2,\"name\":\"Action & Adventure\"},{\"id\":5,\"name\":\"Comedy\"},{\"id\":19,\"name\":\"Sci-Fi & Fantasy\"}],\"directors\":[{\"id\":7135,\"first_name\":\"Sam\",\"last_name\":\"Raimi\",\"profile_photo_url\":\"http://localhost:8080/images/person/7135.jpg\",\"gender\":\"male\"}],\"writers\":[],\"actors\":[{\"id\":4888,\"first_name\":\"Dana\",\"last_name\":\"DeLorenzo\",\"profile_photo_url\":\"http://localhost:8080/images/person/4888.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Kelly Maxwell\"}]},{\"id\":10478,\"first_name\":\"Bruce\",\"last_name\":\"Campbell\",\"profile_photo_url\":\"http://localhost:8080/images/person/10478.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Ashley 'Ash' J. Williams\"}]},{\"id\":18290,\"first_name\":\"Lucy\",\"last_name\":\"Lawless\",\"profile_photo_url\":\"http://localhost:8080/images/person/18290.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Ruby Knowby\"}]},{\"id\":32597,\"first_name\":\"Arielle\",\"last_name\":\"O'Neill\",\"profile_photo_url\":\"http://localhost:8080/images/person/32597.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Brandy Barr\"}]},{\"id\":40066,\"first_name\":\"Ray\",\"last_name\":\"Santiago\",\"profile_photo_url\":\"http://localhost:8080/images/person/40066.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Pablo Simon Bolivar\"}]}],\"critiques\":[]}"
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
                                                value = "{\"timestamp\":1707296571664,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"TV show id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "TV show with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case tv show with id as 22 was not found",
                                                value = "{\"timestamp\":1707250994920,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No tv show found with id: 22\"]}"
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
    public ResponseEntity getTVShowByIdDetails(@PathVariable @Min(value = 1, message = "TV show id must be greater than or equal to 1") long id) {
        return tvShowService.getTVShowWithDetails(id);
    }

    @Operation(
            summary = "Get tv show directors",
            description = "Retreives tv show directors, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of tv show (1..N)",
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
                                                description = "In case tv show with id as 1500 was found",
                                                value = "[{\"id\":7135,\"first_name\":\"Sam\",\"last_name\":\"Raimi\",\"profile_photo_url\":\"http://localhost:8080/images/person/7135.jpg\",\"gender\":\"male\"}]"
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
                                                value = "{\"timestamp\":1707296571664,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"TV show id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "TV show with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case tv show with id as 22 was not found",
                                                value = "{\"timestamp\":1707251172099,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No tv show found with id: 22\"]}"
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
    @GetMapping(path = "/{id}/directors")
    public ResponseEntity getTVShowByIdDirectors(@PathVariable @Min(value = 1, message = "TV show id must be greater than or equal to 1") long id) {
        return tvShowService.getTVShowDirectors(id);
    }

    @Operation(
            summary = "Get tv show writers",
            description = "Retreives tv show writers, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of tv show (1..N)",
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
                                                description = "In case tv show with id as 1502 was found",
                                                value = "[{\"id\":4504,\"first_name\":\"Mike\",\"last_name\":\"Dringenberg\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":20261,\"first_name\":\"Neil\",\"last_name\":\"Gaiman\",\"profile_photo_url\":\"http://localhost:8080/images/person/20261.jpg\",\"gender\":\"male\"},{\"id\":24851,\"first_name\":\"Sam\",\"last_name\":\"Kieth\",\"profile_photo_url\":null,\"gender\":\"male\"}]"
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
                                                value = "{\"timestamp\":1707296571664,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"TV show id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "TV show with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case tv show with id as 22 was not found",
                                                value = "{\"timestamp\":1707251412489,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No tv show found with id: 22\"]}"
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
    @GetMapping(path = "/{id}/writers")
    public ResponseEntity getTVShowByIdWriters(@PathVariable @Min(value = 1, message = "TV show id must be greater than or equal to 1") long id) {
        return tvShowService.getTVShowWriters(id);
    }

    @Operation(
            summary = "Get tv show actors",
            description = "Retreives tv show actors, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of tv show (1..N)",
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
                                                description = "In case tv show with id as 1500 was found",
                                                value = "[{\"id\":4888,\"first_name\":\"Dana\",\"last_name\":\"DeLorenzo\",\"profile_photo_url\":\"http://localhost:8080/images/person/4888.jpg\",\"gender\":\"female\",\"is_star\":true},{\"id\":10478,\"first_name\":\"Bruce\",\"last_name\":\"Campbell\",\"profile_photo_url\":\"http://localhost:8080/images/person/10478.jpg\",\"gender\":\"male\",\"is_star\":true},{\"id\":18290,\"first_name\":\"Lucy\",\"last_name\":\"Lawless\",\"profile_photo_url\":\"http://localhost:8080/images/person/18290.jpg\",\"gender\":\"female\",\"is_star\":false},{\"id\":32597,\"first_name\":\"Arielle\",\"last_name\":\"O'Neill\",\"profile_photo_url\":\"http://localhost:8080/images/person/32597.jpg\",\"gender\":\"female\",\"is_star\":false},{\"id\":40066,\"first_name\":\"Ray\",\"last_name\":\"Santiago\",\"profile_photo_url\":\"http://localhost:8080/images/person/40066.jpg\",\"gender\":\"male\",\"is_star\":true}]"
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
                                                value = "{\"timestamp\":1707296571664,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"TV show id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "TV show with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case tv show with id as 22 was not found",
                                                value = "{\"timestamp\":1707251582749,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No tv show found with id: 22\"]}"
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
    @GetMapping(path = "/{id}/actors")
    @JsonView(JsonVisibilityViews.Lite.class)
    public ResponseEntity getTVShowByIdActors(@PathVariable @Min(value = 1, message = "TV show id must be greater than or equal to 1") long id) {
        return tvShowService.getTVShowActors(id);
    }

    @Operation(
            summary = "Get tv show actors with roles",
            description = "Retreives tv show actors with their roles, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of tv show (1..N)",
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
                                                description = "In case tv show with id as 1500 was found",
                                                value = "[{\"id\":4888,\"first_name\":\"Dana\",\"last_name\":\"DeLorenzo\",\"profile_photo_url\":\"http://localhost:8080/images/person/4888.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Kelly Maxwell\"}]},{\"id\":10478,\"first_name\":\"Bruce\",\"last_name\":\"Campbell\",\"profile_photo_url\":\"http://localhost:8080/images/person/10478.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Ashley 'Ash' J. Williams\"}]},{\"id\":18290,\"first_name\":\"Lucy\",\"last_name\":\"Lawless\",\"profile_photo_url\":\"http://localhost:8080/images/person/18290.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Ruby Knowby\"}]},{\"id\":32597,\"first_name\":\"Arielle\",\"last_name\":\"O'Neill\",\"profile_photo_url\":\"http://localhost:8080/images/person/32597.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Brandy Barr\"}]},{\"id\":40066,\"first_name\":\"Ray\",\"last_name\":\"Santiago\",\"profile_photo_url\":\"http://localhost:8080/images/person/40066.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Pablo Simon Bolivar\"}]}]"
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
                                                value = "{\"timestamp\":1707296571664,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"TV show id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "TV show with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case tv show with id as 22 was not found",
                                                value = "{\"timestamp\":1707251582749,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No tv show found with id: 22\"]}"
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
    @GetMapping(path = "/{id}/actors/roles")
    @JsonView(JsonVisibilityViews.Heavy.class)
    public ResponseEntity getTVShowByIdActorsWithRoles(@PathVariable @Min(value = 1, message = "TV show id must be greater than or equal to 1") long id) {
        return tvShowService.getTVShowActorsWithRoles(id);
    }

    //=========================POST MAPPINGS==================================
    @SecurityRequirement(name = "jwtAuth")
    @Operation(
            summary = "Create new tv show",
            description = "Create new tv show entry along with its genres, directors, writers, actors and their roles. Cover image file can optionaly be saved aswell. Allowed extensions are [png,jpg,jpeg] and max file size is 8MB",
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
                                                description = "In case tv show was saved successfully",
                                                value = "{\"id\":1663,\"title\":\"title\",\"release_date\":\"18/10/1966\",\"cover_image_url\":null,\"description\":\"description\",\"audience_rating\":82,\"critics_rating\":null,\"number_of_seasons\":2,\"genres\":[{\"id\":8,\"name\":\"Drama\"},{\"id\":11,\"name\":\"History\"},{\"id\":12,\"name\":\"Horror\"}],\"directors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":6,\"first_name\":\"José\",\"last_name\":\"Luis González de León\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"writers\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":2,\"first_name\":\"Dodie\",\"last_name\":\"Smith\",\"profile_photo_url\":null,\"gender\":\"other\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"},{\"id\":3,\"name\":\"role3\"}]},{\"id\":4,\"first_name\":\"Harrison\",\"last_name\":\"Ford\",\"profile_photo_url\":\"http://localhost:8080/images/person/4.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"}]}],\"critiques\":[]}"
                                        )
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "TV show json structure validation error occured",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case tv show with empty title was sent",
                                                value = "{\"timestamp\":1707295697870,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ValidationException\",\"details\":[\"TV show title must not be null or empty!\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "TV show dependency object was not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case provided genre id of 70 was not found",
                                                value = "{\"timestamp\":1707295877036,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Genre with id: 70 does not exist in database!\"]}"
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
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @JsonView(JsonVisibilityViews.Heavy.class)
    public ResponseEntity postTVShow(
            @Parameter(description = "TV show data in JSON format. Example: {\"title\":\"title\",\"release_date\":\"18/10/1966\",\"description\":\"description\",\"audience_rating\":82,\"number_of_seasons\":2,\"genres\":[8,11,12],\"directors\":[1,6,9],\"writers\":[1,2,9],\"actors\":[{\"id\":1,\"is_starring\":true,\"roles\":[\"role1\",\"role2\",\"role3\"]},{\"id\":4,\"is_starring\":false,\"roles\":[\"role1\"]}]}",
                    required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
            @RequestPart("tv_show") TVShowRequestDTO tvShow,
            @Parameter(description = "Cover Image", required = false, content = @Content(mediaType = "application/octet-stream"))
            @RequestPart(name = "cover_image", required = false) MultipartFile coverImage) {
        domainValidator.validate(tvShow, coverImage, RequestMethod.POST);
        if (coverImage != null) {
            tvShow.setCoverImage(new MyImage(coverImage));
        }
        return tvShowService.postTVShow(tvShow);
    }

    //=========================PUT MAPPINGS===================================
    @SecurityRequirement(name = "jwtAuth")
    @Operation(
            summary = "Update given tv show",
            description = "Update tv show entry along with its genres, directors, writers, actors and their roles, by specified tv show id. Cover image file can optionaly be updated aswell. Allowed extensions are [png,jpg,jpeg] and max file size is 8MB",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of tv show (1..N)",
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
                                                description = "In case tv show was updated successfully",
                                                value = "{\"id\":1664,\"title\":\"title\",\"release_date\":\"18/10/1966\",\"cover_image_url\":null,\"description\":\"description\",\"audience_rating\":82,\"critics_rating\":null,\"number_of_seasons\":2,\"genres\":[{\"id\":8,\"name\":\"Drama\"},{\"id\":11,\"name\":\"History\"},{\"id\":12,\"name\":\"Horror\"}],\"directors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":6,\"first_name\":\"José\",\"last_name\":\"Luis González de León\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"writers\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":2,\"first_name\":\"Dodie\",\"last_name\":\"Smith\",\"profile_photo_url\":null,\"gender\":\"other\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"},{\"id\":3,\"name\":\"role3\"}]},{\"id\":4,\"first_name\":\"Harrison\",\"last_name\":\"Ford\",\"profile_photo_url\":\"http://localhost:8080/images/person/4.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"}]}],\"critiques\":[]}"
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
                                                value = "{\"timestamp\":1707296571664,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"TV show id must be greater than or equal to 1\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case tv show with empty title was sent",
                                                value = "{\"timestamp\":1707296318268,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ValidationException\",\"details\":[\"TV show title must not be null or empty!\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "TV show dependency object or tv show itself was not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case tv show with id 1665 was not found",
                                                value = "{\"timestamp\":1707297824729,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"TV show with id: 1665 does not exist in database!\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case provided genre id of 70 was not found",
                                                value = "{\"timestamp\":1707296406984,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Genre with id: 70 does not exist in database!\"]}"
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
    public ResponseEntity putTVShow(
            @PathVariable @Min(value = 1, message = "TV show id must be greater than or equal to 1") long id,
            @Parameter(description = "TV show data in JSON format. Example: {\"title\":\"title\",\"release_date\":\"18/10/1966\",\"description\":\"description\",\"audience_rating\":82,\"number_of_seasons\":2,\"genres\":[8,11,12],\"directors\":[1,6,9],\"writers\":[1,2,9],\"actors\":[{\"id\":1,\"is_starring\":true,\"roles\":[\"role1\",\"role2\",\"role3\"]},{\"id\":4,\"is_starring\":false,\"roles\":[\"role1\"]}]}",
                    required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
            @RequestPart("tv_show") TVShowRequestDTO tvShow,
            @Parameter(description = "Cover Image", required = false, content = @Content(mediaType = "application/octet-stream"))
            @RequestPart(name = "cover_image", required = false) MultipartFile coverImage) {
        tvShow.setId(id);
        domainValidator.validate(tvShow, coverImage, RequestMethod.PUT);
        if (coverImage != null) {
            tvShow.setCoverImage(new MyImage(coverImage));
        }
        return tvShowService.putTVShow(tvShow);
    }

    //=========================DELETE MAPPINGS================================
    @Operation(
            summary = "Delete tv show with specified id",
            description = "Deletes tv show data by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of tv show (1..N)",
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
                                                description = "In case tv show with id as 1664 was deleted successfully",
                                                value = "{\"id\":1664,\"title\":\"title\",\"release_date\":\"18/10/1966\",\"cover_image_url\":null,\"description\":\"description\",\"audience_rating\":82,\"critics_rating\":null,\"number_of_seasons\":2,\"genres\":[{\"id\":8,\"name\":\"Drama\"},{\"id\":11,\"name\":\"History\"},{\"id\":12,\"name\":\"Horror\"}],\"directors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":6,\"first_name\":\"José\",\"last_name\":\"Luis González de León\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"writers\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":2,\"first_name\":\"Dodie\",\"last_name\":\"Smith\",\"profile_photo_url\":null,\"gender\":\"other\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"},{\"id\":3,\"name\":\"role3\"}]},{\"id\":4,\"first_name\":\"Harrison\",\"last_name\":\"Ford\",\"profile_photo_url\":\"http://localhost:8080/images/person/4.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"}]}],\"critiques\":[]}"
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
                                                value = "{\"timestamp\":1707296571664,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"TV show id must be greater than or equal to 1\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "TV show with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case tv show with id as 1665 was not found",
                                                value = "{\"timestamp\":1707296631170,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No tv show found with id: 1665\"]}"
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
    public ResponseEntity deleteTVShowById(@PathVariable @Min(value = 1, message = "TV show id must be greater than or equal to 1") long id) {
        return tvShowService.deleteTVShowById(id);
    }

}
