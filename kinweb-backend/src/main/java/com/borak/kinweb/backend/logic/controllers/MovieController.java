/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.controllers;

import com.borak.kinweb.backend.domain.dto.movie.MovieRequestDTO;
import com.borak.kinweb.backend.domain.classes.MyImage;
import com.borak.kinweb.backend.domain.dto.movie.MovieResponseDTO;
import com.borak.kinweb.backend.exceptions.handler.ErrorDetail;
import com.borak.kinweb.backend.logic.services.movie.IMovieService;
import com.borak.kinweb.backend.logic.services.validation.DomainValidationService;
import com.borak.kinweb.backend.logic.transformers.serializers.views.JsonVisibilityViews;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
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
@Tag(name = "Movies")
@RestController
@RequestMapping(path = "api/movies")
@Validated
public class MovieController {

    @Autowired
    private IMovieService movieService;

    @Autowired
    private DomainValidationService domainValidator;

    //=========================GET MAPPINGS================================== 
    @Operation(
            summary = "Get movies data with their genres",
            description = "Retreives movies data with their genres, filtered by page number and page size",
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
                                                value = "[{\"id\":3,\"title\":\"Thunderball\",\"release_date\":\"11/12/1965\",\"cover_image_url\":\"http://localhost:8080/images/media/3.jpg\",\"audience_rating\":67,\"genres\":[{\"id\":1,\"name\":\"Action\"},{\"id\":3,\"name\":\"Adventure\"},{\"id\":24,\"name\":\"Thriller\"}]},{\"id\":4,\"title\":\"Doctor Zhivago\",\"release_date\":\"22/12/1965\",\"cover_image_url\":\"http://localhost:8080/images/media/4.jpg\",\"audience_rating\":75,\"genres\":[{\"id\":8,\"name\":\"Drama\"},{\"id\":18,\"name\":\"Romance\"},{\"id\":25,\"name\":\"War\"}]}]"
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
    public ResponseEntity getMovies(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return movieService.getAllMoviesWithGenresPaginated(page, size);
    }

    @Operation(
            summary = "Get popular movies data with their genres",
            description = "Retreives movies data with their genres, where each movie has audience_rating greater than, or equal to 80, filtered by page number and page size",
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
                                                value = "[{\"id\":22,\"title\":\"Persona\",\"release_date\":\"18/10/1966\",\"cover_image_url\":\"http://localhost:8080/images/media/22.jpg\",\"audience_rating\":82,\"genres\":[{\"id\":8,\"name\":\"Drama\"}]},{\"id\":30,\"title\":\"Andrei Rublev\",\"release_date\":\"16/12/1966\",\"cover_image_url\":\"http://localhost:8080/images/media/30.jpg\",\"audience_rating\":80,\"genres\":[{\"id\":8,\"name\":\"Drama\"},{\"id\":11,\"name\":\"History\"}]}]"
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
    public ResponseEntity getMoviesPopular(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return movieService.getAllMoviesWithGenresPopularPaginated(page, size);
    }

    @Operation(
            summary = "Get current movies data with their genres",
            description = "Retreives movies data with their genres, where each movie has its release_date year as the previous year, filtered by page number and page size",
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
                                                value = "[{\"id\":1164,\"title\":\"Guardians of the Galaxy Vol. 3\",\"release_date\":\"3/05/2023\",\"cover_image_url\":\"http://localhost:8080/images/media/1164.jpg\",\"audience_rating\":80,\"genres\":[{\"id\":1,\"name\":\"Action\"},{\"id\":3,\"name\":\"Adventure\"},{\"id\":20,\"name\":\"Science Fiction\"}]},{\"id\":1165,\"title\":\"Spider-Man: Across the Spider-Verse\",\"release_date\":\"31/05/2023\",\"cover_image_url\":\"http://localhost:8080/images/media/1165.jpg\",\"audience_rating\":84,\"genres\":[{\"id\":1,\"name\":\"Action\"},{\"id\":3,\"name\":\"Adventure\"},{\"id\":4,\"name\":\"Animation\"},{\"id\":20,\"name\":\"Science Fiction\"}]}]"
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
    public ResponseEntity getMoviesCurrent(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return movieService.getAllMoviesWithGenresCurrentPaginated(page, size);
    }

    @Operation(
            summary = "Get movies data with their details",
            description = "Retreives movies data with their genres, critiques, directors, writers, actors and their roles, filtered by page number and page size",
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
                                                value = "[{\"id\":3,\"title\":\"Thunderball\",\"release_date\":\"11/12/1965\",\"cover_image_url\":\"http://localhost:8080/images/media/3.jpg\",\"description\":\"A criminal organization has obtained two nuclear bombs and are asking for a 100 million pound ransom in the form of diamonds in seven days or they will use the weapons. The secret service sends James Bond to the Bahamas to once again save the world.\",\"audience_rating\":67,\"critics_rating\":null,\"length\":130,\"genres\":[{\"id\":1,\"name\":\"Action\"},{\"id\":3,\"name\":\"Adventure\"},{\"id\":24,\"name\":\"Thriller\"}],\"directors\":[{\"id\":718,\"first_name\":\"Robert\",\"last_name\":\"Watts\",\"profile_photo_url\":\"http://localhost:8080/images/person/718.jpg\",\"gender\":\"male\"},{\"id\":9035,\"first_name\":\"Terence\",\"last_name\":\"Young\",\"profile_photo_url\":\"http://localhost:8080/images/person/9035.jpg\",\"gender\":\"male\"},{\"id\":11327,\"first_name\":\"Joan\",\"last_name\":\"Davis\",\"profile_photo_url\":null,\"gender\":\"female\"},{\"id\":17144,\"first_name\":\"Gus\",\"last_name\":\"Agosti\",\"profile_photo_url\":null,\"gender\":\"other\"}],\"writers\":[{\"id\":9036,\"first_name\":\"Ian\",\"last_name\":\"Fleming\",\"profile_photo_url\":\"http://localhost:8080/images/person/9036.jpg\",\"gender\":\"male\"},{\"id\":9039,\"first_name\":\"Richard\",\"last_name\":\"Maibaum\",\"profile_photo_url\":\"http://localhost:8080/images/person/9039.jpg\",\"gender\":\"male\"},{\"id\":9118,\"first_name\":\"Kevin\",\"last_name\":\"McClory\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":9119,\"first_name\":\"Jack\",\"last_name\":\"Whittingham\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":9120,\"first_name\":\"John\",\"last_name\":\"Hopkins\",\"profile_photo_url\":\"http://localhost:8080/images/person/9120.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":704,\"first_name\":\"Philip\",\"last_name\":\"Stone\",\"profile_photo_url\":\"http://localhost:8080/images/person/704.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"SPECTRE Number 5 (uncredited)\"}]},{\"id\":743,\"first_name\":\"Sean\",\"last_name\":\"Connery\",\"profile_photo_url\":\"http://localhost:8080/images/person/743.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"James Bond\"}]},{\"id\":2251,\"first_name\":\"Earl\",\"last_name\":\"Cameron\",\"profile_photo_url\":\"http://localhost:8080/images/person/2251.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Pinder\"}]},{\"id\":6959,\"first_name\":\"Rose\",\"last_name\":\"Alba\",\"profile_photo_url\":\"http://localhost:8080/images/person/6959.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Madame Boitier\"}]},{\"id\":7193,\"first_name\":\"Anthony\",\"last_name\":\"Dawson\",\"profile_photo_url\":\"http://localhost:8080/images/person/7193.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Ernst Stavro Blofeld (uncredited)\"}]},{\"id\":9049,\"first_name\":\"Bernard\",\"last_name\":\"Lee\",\"profile_photo_url\":\"http://localhost:8080/images/person/9049.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"M\"}]},{\"id\":9051,\"first_name\":\"Lois\",\"last_name\":\"Maxwell\",\"profile_photo_url\":\"http://localhost:8080/images/person/9051.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Miss Moneypenny\"}]},{\"id\":9073,\"first_name\":\"Desmond\",\"last_name\":\"Llewelyn\",\"profile_photo_url\":\"http://localhost:8080/images/person/9073.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Q\"}]},{\"id\":9089,\"first_name\":\"Claudine\",\"last_name\":\"Auger\",\"profile_photo_url\":\"http://localhost:8080/images/person/9089.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Dominique 'Domino' Derval\"}]},{\"id\":9091,\"first_name\":\"Adolfo\",\"last_name\":\"Celi\",\"profile_photo_url\":\"http://localhost:8080/images/person/9091.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Emilio Largo\"}]},{\"id\":9092,\"first_name\":\"Luciana\",\"last_name\":\"Paluzzi\",\"profile_photo_url\":\"http://localhost:8080/images/person/9092.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Fiona Volpe\"}]},{\"id\":9093,\"first_name\":\"Rik\",\"last_name\":\"Van Nutter\",\"profile_photo_url\":\"http://localhost:8080/images/person/9093.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Felix Leiter\"}]},{\"id\":9094,\"first_name\":\"Guy\",\"last_name\":\"Doleman\",\"profile_photo_url\":\"http://localhost:8080/images/person/9094.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Count Lippe\"}]},{\"id\":9096,\"first_name\":\"Molly\",\"last_name\":\"Peters\",\"profile_photo_url\":\"http://localhost:8080/images/person/9096.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Patricia Fearing\"}]},{\"id\":9098,\"first_name\":\"Martine\",\"last_name\":\"Beswick\",\"profile_photo_url\":\"http://localhost:8080/images/person/9098.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Paula Caplan\"}]},{\"id\":9099,\"first_name\":\"Roland\",\"last_name\":\"Culver\",\"profile_photo_url\":\"http://localhost:8080/images/person/9099.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Foreign Secretary\"}]},{\"id\":9118,\"first_name\":\"Kevin\",\"last_name\":\"McClory\",\"profile_photo_url\":null,\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Man Smoking at Nassau Casino (uncredited)\"}]},{\"id\":12173,\"first_name\":\"Jack\",\"last_name\":\"Gwillim\",\"profile_photo_url\":\"http://localhost:8080/images/person/12173.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Senior RAF Staff Officer (uncredited)\"}]},{\"id\":19412,\"first_name\":\"Leonard\",\"last_name\":\"Sachs\",\"profile_photo_url\":\"http://localhost:8080/images/person/19412.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Group Captain (uncredited)\"}]},{\"id\":20413,\"first_name\":\"Edward\",\"last_name\":\"Underdown\",\"profile_photo_url\":\"http://localhost:8080/images/person/20413.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Air Vice Marshall (uncredited)\"}]},{\"id\":23053,\"first_name\":\"Philip\",\"last_name\":\"Locke\",\"profile_photo_url\":null,\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Vargas\"}]},{\"id\":23280,\"first_name\":\"Reginald\",\"last_name\":\"Beckwith\",\"profile_photo_url\":\"http://localhost:8080/images/person/23280.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Kenniston\"}]},{\"id\":23569,\"first_name\":\"Suzy\",\"last_name\":\"Kendall\",\"profile_photo_url\":\"http://localhost:8080/images/person/23569.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Prue (uncredited)\"}]},{\"id\":25128,\"first_name\":\"George\",\"last_name\":\"Pravda\",\"profile_photo_url\":\"http://localhost:8080/images/person/25128.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Kutze\"}]},{\"id\":31706,\"first_name\":\"Harold\",\"last_name\":\"Sanderson\",\"profile_photo_url\":null,\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Hydrofoil Captain (uncredited)\"}]},{\"id\":34499,\"first_name\":\"Richard\",\"last_name\":\"Graydon\",\"profile_photo_url\":\"http://localhost:8080/images/person/34499.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Largo's Henchman (uncredited)\"}]},{\"id\":39907,\"first_name\":\"Bob\",\"last_name\":\"Simmons\",\"profile_photo_url\":\"http://localhost:8080/images/person/39907.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Colonel Jacques Bouvar - SPECTRE #6 (uncredited)\"}]},{\"id\":41006,\"first_name\":\"Michael\",\"last_name\":\"Brennan\",\"profile_photo_url\":null,\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Janni\"}]},{\"id\":45492,\"first_name\":\"Paul\",\"last_name\":\"Stassino\",\"profile_photo_url\":\"http://localhost:8080/images/person/45492.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Palazzi\"}]},{\"id\":46569,\"first_name\":\"Mitsouko\",\"last_name\":\"\",\"profile_photo_url\":\"http://localhost:8080/images/person/46569.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Madame La Porte (uncredited)\"}]}],\"critiques\":[]},{\"id\":4,\"title\":\"Doctor Zhivago\",\"release_date\":\"22/12/1965\",\"cover_image_url\":\"http://localhost:8080/images/media/4.jpg\",\"description\":\"The life of a Russian physician and poet who, although married to another, falls in love with a political activist's wife and experiences hardship during World War I and then the October Revolution.\",\"audience_rating\":75,\"critics_rating\":null,\"length\":200,\"genres\":[{\"id\":8,\"name\":\"Drama\"},{\"id\":18,\"name\":\"Romance\"},{\"id\":25,\"name\":\"War\"}],\"directors\":[{\"id\":889,\"first_name\":\"Roy\",\"last_name\":\"Rossotti\",\"profile_photo_url\":null,\"gender\":\"other\"},{\"id\":11257,\"first_name\":\"David\",\"last_name\":\"Lean\",\"profile_photo_url\":\"http://localhost:8080/images/person/11257.jpg\",\"gender\":\"male\"},{\"id\":13872,\"first_name\":\"Barbara\",\"last_name\":\"Cole\",\"profile_photo_url\":null,\"gender\":\"other\"},{\"id\":28358,\"first_name\":\"Pedro\",\"last_name\":\"Vidal\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":29476,\"first_name\":\"Roy\",\"last_name\":\"Stevens\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":39985,\"first_name\":\"José\",\"last_name\":\"María Ochoa\",\"profile_photo_url\":null,\"gender\":\"male\"}],\"writers\":[{\"id\":12161,\"first_name\":\"Robert\",\"last_name\":\"Bolt\",\"profile_photo_url\":\"http://localhost:8080/images/person/12161.jpg\",\"gender\":\"male\"},{\"id\":12659,\"first_name\":\"Boris\",\"last_name\":\"Pasternak\",\"profile_photo_url\":\"http://localhost:8080/images/person/12659.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":394,\"first_name\":\"Geraldine\",\"last_name\":\"Chaplin\",\"profile_photo_url\":\"http://localhost:8080/images/person/394.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Tonya Gromeko\"}]},{\"id\":470,\"first_name\":\"Jeffrey\",\"last_name\":\"Rockland\",\"profile_photo_url\":null,\"gender\":\"other\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Sasha\"}]},{\"id\":504,\"first_name\":\"Rod\",\"last_name\":\"Steiger\",\"profile_photo_url\":\"http://localhost:8080/images/person/504.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Viktor Komarovsky\"}]},{\"id\":1618,\"first_name\":\"Julie\",\"last_name\":\"Christie\",\"profile_photo_url\":\"http://localhost:8080/images/person/1618.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Lara Antipova\"}]},{\"id\":2187,\"first_name\":\"Catherine\",\"last_name\":\"Ellison\",\"profile_photo_url\":null,\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Raped Woman (uncredited)\"}]},{\"id\":2205,\"first_name\":\"Tarek\",\"last_name\":\"Sharif\",\"profile_photo_url\":null,\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Yuri at 8 Years Old\"}]},{\"id\":2267,\"first_name\":\"Adrienne\",\"last_name\":\"Corri\",\"profile_photo_url\":\"http://localhost:8080/images/person/2267.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Amelia\"}]},{\"id\":2604,\"first_name\":\"Lucy\",\"last_name\":\"Westmore\",\"profile_photo_url\":null,\"gender\":\"other\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Katya\"}]},{\"id\":3370,\"first_name\":\"Peter\",\"last_name\":\"Madden\",\"profile_photo_url\":\"http://localhost:8080/images/person/3370.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Political Officer\"}]},{\"id\":4819,\"first_name\":\"Omar\",\"last_name\":\"Sharif\",\"profile_photo_url\":\"http://localhost:8080/images/person/4819.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Dr. Yuri Zhivago\"}]},{\"id\":8311,\"first_name\":\"María\",\"last_name\":\"Vico\",\"profile_photo_url\":\"http://localhost:8080/images/person/8311.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Demented Woman (uncredited)\"}]},{\"id\":8721,\"first_name\":\"Roger\",\"last_name\":\"Maxwell\",\"profile_photo_url\":null,\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Beef-Faced Colonel\"}]},{\"id\":9602,\"first_name\":\"Geoffrey\",\"last_name\":\"Keen\",\"profile_photo_url\":\"http://localhost:8080/images/person/9602.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Medical Professor\"}]},{\"id\":10888,\"first_name\":\"Robert\",\"last_name\":\"Rietti\",\"profile_photo_url\":\"http://localhost:8080/images/person/10888.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Kostoyed (voice) (uncredited)\"}]},{\"id\":11261,\"first_name\":\"Alec\",\"last_name\":\"Guinness\",\"profile_photo_url\":\"http://localhost:8080/images/person/11261.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Gen. Yevgraf Zhivago\"}]},{\"id\":11688,\"first_name\":\"Ralph\",\"last_name\":\"Richardson\",\"profile_photo_url\":\"http://localhost:8080/images/person/11688.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Alexander Gromeko\"}]},{\"id\":12665,\"first_name\":\"Tom\",\"last_name\":\"Courtenay\",\"profile_photo_url\":\"http://localhost:8080/images/person/12665.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Pasha Antipov\"},{\"id\":2,\"name\":\"Strelnikov\"}]},{\"id\":12666,\"first_name\":\"Siobhán\",\"last_name\":\"McKenna\",\"profile_photo_url\":\"http://localhost:8080/images/person/12666.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Anna\"}]},{\"id\":12667,\"first_name\":\"Gérard\",\"last_name\":\"Tichy\",\"profile_photo_url\":\"http://localhost:8080/images/person/12667.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Liberius\"}]},{\"id\":12669,\"first_name\":\"Noel\",\"last_name\":\"Willman\",\"profile_photo_url\":\"http://localhost:8080/images/person/12669.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Razin, Liberius' Lieutenant\"}]},{\"id\":12671,\"first_name\":\"Jack\",\"last_name\":\"MacGowran\",\"profile_photo_url\":\"http://localhost:8080/images/person/12671.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Petya\"}]},{\"id\":12672,\"first_name\":\"Mark\",\"last_name\":\"Eden\",\"profile_photo_url\":\"http://localhost:8080/images/person/12672.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Engineer at dam\"}]},{\"id\":12674,\"first_name\":\"Erik\",\"last_name\":\"Chitty\",\"profile_photo_url\":\"http://localhost:8080/images/person/12674.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Sergei (Old Soldier)\"}]},{\"id\":12876,\"first_name\":\"Klaus\",\"last_name\":\"Kinski\",\"profile_photo_url\":\"http://localhost:8080/images/person/12876.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Kostoyed Amourski\"}]},{\"id\":13368,\"first_name\":\"José\",\"last_name\":\"Nieto\",\"profile_photo_url\":\"http://localhost:8080/images/person/13368.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Priest (uncredited)\"}]},{\"id\":16383,\"first_name\":\"Ingrid\",\"last_name\":\"Pitt\",\"profile_photo_url\":\"http://localhost:8080/images/person/16383.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Extra (uncredited)\"}]},{\"id\":20498,\"first_name\":\"Luana\",\"last_name\":\"Alcañiz\",\"profile_photo_url\":\"http://localhost:8080/images/person/20498.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Mrs. Sventytski (uncredited)\"}]},{\"id\":21351,\"first_name\":\"Wolf\",\"last_name\":\"Frees\",\"profile_photo_url\":null,\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Delegate\"}]},{\"id\":26537,\"first_name\":\"Ricardo\",\"last_name\":\"Palacios\",\"profile_photo_url\":\"http://localhost:8080/images/person/26537.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Extra (uncredited)\"}]},{\"id\":27028,\"first_name\":\"Víctor\",\"last_name\":\"Israel\",\"profile_photo_url\":\"http://localhost:8080/images/person/27028.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Hospital Inmate (uncredited)\"}]},{\"id\":28242,\"first_name\":\"María\",\"last_name\":\"Martín\",\"profile_photo_url\":\"http://localhost:8080/images/person/28242.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Gentlewoman (uncredited)\"}]},{\"id\":28959,\"first_name\":\"José\",\"last_name\":\"María Caffarel\",\"profile_photo_url\":\"http://localhost:8080/images/person/28959.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Militiaman (uncredited)\"}]},{\"id\":29757,\"first_name\":\"Lili\",\"last_name\":\"Muráti\",\"profile_photo_url\":\"http://localhost:8080/images/person/29757.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"The Train Jumper\"}]},{\"id\":30572,\"first_name\":\"Emilio\",\"last_name\":\"Carrer\",\"profile_photo_url\":null,\"gender\":\"other\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Mr. Sventytski (uncredited)\"}]},{\"id\":30695,\"first_name\":\"Virgilio\",\"last_name\":\"Teixeira\",\"profile_photo_url\":\"http://localhost:8080/images/person/30695.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Captain (uncredited)\"}]},{\"id\":30775,\"first_name\":\"Inigo\",\"last_name\":\"Jackson\",\"profile_photo_url\":null,\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Major (uncredited)\"}]},{\"id\":36028,\"first_name\":\"Gwen\",\"last_name\":\"Nelson\",\"profile_photo_url\":null,\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Female Janitor\"}]},{\"id\":37025,\"first_name\":\"Rita\",\"last_name\":\"Tushingham\",\"profile_photo_url\":\"http://localhost:8080/images/person/37025.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"The Girl\"}]},{\"id\":38108,\"first_name\":\"Aldo\",\"last_name\":\"Sambrell\",\"profile_photo_url\":\"http://localhost:8080/images/person/38108.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"(uncredited)\"}]},{\"id\":41979,\"first_name\":\"Pilar\",\"last_name\":\"Gómez Ferrer\",\"profile_photo_url\":\"http://localhost:8080/images/person/41979.jpg\",\"gender\":\"female\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"(uncredited)\"}]},{\"id\":45367,\"first_name\":\"Leo\",\"last_name\":\"Lähteenmäki\",\"profile_photo_url\":\"http://localhost:8080/images/person/45367.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Siberian Husband (uncredited)\"}]},{\"id\":47252,\"first_name\":\"Bernard\",\"last_name\":\"Kay\",\"profile_photo_url\":\"http://localhost:8080/images/person/47252.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"The Bolshevik\"}]}],\"critiques\":[]}]"
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
    public ResponseEntity getMoviesDetails(
            @RequestParam(name = "page", defaultValue = "1", required = false) @Min(value = 1, message = "Page number has to be greater than or equal to 1") int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) @Min(value = 1, message = "Size number has to be greater than or equal to 1") int size) {
        return movieService.getAllMoviesWithDetailsPaginated(page, size);
    }

    @Operation(
            summary = "Get movie data with its genres",
            description = "Retreives movie data with its genres by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of movie (1..N)",
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
                                                description = "In case movie with id as 22 was found",
                                                value = "{\"id\":22,\"title\":\"Persona\",\"release_date\":\"18/10/1966\",\"cover_image_url\":\"http://localhost:8080/images/media/22.jpg\",\"description\":\"A young nurse, Alma, is put in charge of Elisabeth Vogler: an actress who is seemingly healthy in all respects, but will not talk. As they spend time together, Alma speaks to Elisabeth constantly, never receiving any answer. The time they spend together only strengthens the crushing realization that one does not exist.\",\"audience_rating\":82,\"critics_rating\":null,\"length\":83,\"genres\":[{\"id\":8,\"name\":\"Drama\"}]}"
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
                                                value = "{\"timestamp\":1707145607351,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Movie id must be greater than or equal to 1\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Movie with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case movie with id as 22 was not found",
                                                value = "{\"timestamp\":1707145783796,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No movie found with id: 22\"]}"
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
    public ResponseEntity getMovieById(@PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") long id) {
        return movieService.getMovieWithGenres(id);
    }

    @Operation(
            summary = "Get movie data with its details",
            description = "Retreives movie data with its genres, critiques, directors, writers, actors and their roles, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of movie (1..N)",
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
                                                description = "In case movie with id as 22 was found",
                                                value = "{\"id\":22,\"title\":\"Persona\",\"release_date\":\"18/10/1966\",\"cover_image_url\":\"http://localhost:8080/images/media/22.jpg\",\"description\":\"A young nurse, Alma, is put in charge of Elisabeth Vogler: an actress who is seemingly healthy in all respects, but will not talk. As they spend time together, Alma speaks to Elisabeth constantly, never receiving any answer. The time they spend together only strengthens the crushing realization that one does not exist.\",\"audience_rating\":82,\"critics_rating\":null,\"length\":83,\"genres\":[{\"id\":8,\"name\":\"Drama\"}],\"directors\":[{\"id\":6293,\"first_name\":\"Ingmar\",\"last_name\":\"Bergman\",\"profile_photo_url\":\"http://localhost:8080/images/person/6293.jpg\",\"gender\":\"male\"},{\"id\":11210,\"first_name\":\"Bo\",\"last_name\":\"Arne Vibenius\",\"profile_photo_url\":\"http://localhost:8080/images/person/11210.jpg\",\"gender\":\"male\"},{\"id\":17123,\"first_name\":\"Kerstin\",\"last_name\":\"Berg\",\"profile_photo_url\":null,\"gender\":\"other\"}],\"writers\":[{\"id\":6293,\"first_name\":\"Ingmar\",\"last_name\":\"Bergman\",\"profile_photo_url\":\"http://localhost:8080/images/person/6293.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":6294,\"first_name\":\"Gunnar\",\"last_name\":\"Björnstrand\",\"profile_photo_url\":\"http://localhost:8080/images/person/6294.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Herr Vogler\"}]},{\"id\":6300,\"first_name\":\"Bibi\",\"last_name\":\"Andersson\",\"profile_photo_url\":\"http://localhost:8080/images/person/6300.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Alma\"}]},{\"id\":10996,\"first_name\":\"Liv\",\"last_name\":\"Ullmann\",\"profile_photo_url\":\"http://localhost:8080/images/person/10996.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Elisabet Vogler\"}]},{\"id\":10998,\"first_name\":\"Margaretha\",\"last_name\":\"Krook\",\"profile_photo_url\":\"http://localhost:8080/images/person/10998.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"The Doctor\"}]},{\"id\":11000,\"first_name\":\"Jörgen\",\"last_name\":\"Lindström\",\"profile_photo_url\":\"http://localhost:8080/images/person/11000.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Elisabet's Son (uncredited)\"}]}],\"critiques\":[]}"
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
                                                value = "{\"timestamp\":1707145607351,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Movie id must be greater than or equal to 1\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Movie with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case movie with id as 22 was not found",
                                                value = "{\"timestamp\":1707145783796,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No movie found with id: 22\"]}"
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
    public ResponseEntity getMovieByIdDetails(@PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") long id) {
        return movieService.getMovieWithDetails(id);
    }

    @Operation(
            summary = "Get movie directors",
            description = "Retreives movie directors, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of movie (1..N)",
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
                                                description = "In case movie with id as 22 was found",
                                                value = "[{\"id\":6293,\"first_name\":\"Ingmar\",\"last_name\":\"Bergman\",\"profile_photo_url\":\"http://localhost:8080/images/person/6293.jpg\",\"gender\":\"male\"},{\"id\":11210,\"first_name\":\"Bo\",\"last_name\":\"Arne Vibenius\",\"profile_photo_url\":\"http://localhost:8080/images/person/11210.jpg\",\"gender\":\"male\"},{\"id\":17123,\"first_name\":\"Kerstin\",\"last_name\":\"Berg\",\"profile_photo_url\":null,\"gender\":\"other\"}]"
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
                                                value = "{\"timestamp\":1707145607351,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Movie id must be greater than or equal to 1\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Movie with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case movie with id as 22 was not found",
                                                value = "{\"timestamp\":1707145783796,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No movie found with id: 22\"]}"
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
    public ResponseEntity getMovieByIdDirectors(@PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") long id) {
        return movieService.getMovieDirectors(id);
    }

    @Operation(
            summary = "Get movie writers",
            description = "Retreives movie writers, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of movie (1..N)",
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
                                                description = "In case movie with id as 22 was found",
                                                value = "[{\"id\":6293,\"first_name\":\"Ingmar\",\"last_name\":\"Bergman\",\"profile_photo_url\":\"http://localhost:8080/images/person/6293.jpg\",\"gender\":\"male\"}]"
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
                                                value = "{\"timestamp\":1707145607351,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Movie id must be greater than or equal to 1\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Movie with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case movie with id as 22 was not found",
                                                value = "{\"timestamp\":1707145783796,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No movie found with id: 22\"]}"
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
    public ResponseEntity getMovieByIdWriters(@PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") long id) {
        return movieService.getMovieWriters(id);
    }

    @Operation(
            summary = "Get movie actors",
            description = "Retreives movie actors, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of movie (1..N)",
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
                                                description = "In case movie with id as 22 was found",
                                                value = "[{\"id\":6294,\"first_name\":\"Gunnar\",\"last_name\":\"Björnstrand\",\"profile_photo_url\":\"http://localhost:8080/images/person/6294.jpg\",\"gender\":\"male\",\"is_star\":false},{\"id\":6300,\"first_name\":\"Bibi\",\"last_name\":\"Andersson\",\"profile_photo_url\":\"http://localhost:8080/images/person/6300.jpg\",\"gender\":\"female\",\"is_star\":true},{\"id\":10996,\"first_name\":\"Liv\",\"last_name\":\"Ullmann\",\"profile_photo_url\":\"http://localhost:8080/images/person/10996.jpg\",\"gender\":\"female\",\"is_star\":true},{\"id\":10998,\"first_name\":\"Margaretha\",\"last_name\":\"Krook\",\"profile_photo_url\":\"http://localhost:8080/images/person/10998.jpg\",\"gender\":\"female\",\"is_star\":true},{\"id\":11000,\"first_name\":\"Jörgen\",\"last_name\":\"Lindström\",\"profile_photo_url\":\"http://localhost:8080/images/person/11000.jpg\",\"gender\":\"male\",\"is_star\":false}]"
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
                                                value = "{\"timestamp\":1707145607351,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Movie id must be greater than or equal to 1\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Movie with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case movie with id as 22 was not found",
                                                value = "{\"timestamp\":1707145783796,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No movie found with id: 22\"]}"
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
    public ResponseEntity getMovieByIdActors(@PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") long id) {
        return movieService.getMovieActors(id);
    }

    @Operation(
            summary = "Get movie actors with roles",
            description = "Retreives movie actors with their roles, by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of movie (1..N)",
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
                                                description = "In case movie with id as 22 was found",
                                                value = "[{\"id\":6294,\"first_name\":\"Gunnar\",\"last_name\":\"Björnstrand\",\"profile_photo_url\":\"http://localhost:8080/images/person/6294.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Herr Vogler\"}]},{\"id\":6300,\"first_name\":\"Bibi\",\"last_name\":\"Andersson\",\"profile_photo_url\":\"http://localhost:8080/images/person/6300.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Alma\"}]},{\"id\":10996,\"first_name\":\"Liv\",\"last_name\":\"Ullmann\",\"profile_photo_url\":\"http://localhost:8080/images/person/10996.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"Elisabet Vogler\"}]},{\"id\":10998,\"first_name\":\"Margaretha\",\"last_name\":\"Krook\",\"profile_photo_url\":\"http://localhost:8080/images/person/10998.jpg\",\"gender\":\"female\",\"is_star\":true,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"The Doctor\"}]},{\"id\":11000,\"first_name\":\"Jörgen\",\"last_name\":\"Lindström\",\"profile_photo_url\":\"http://localhost:8080/images/person/11000.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"Elisabet's Son (uncredited)\"}]}]"
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
                                                value = "{\"timestamp\":1707145607351,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Movie id must be greater than or equal to 1\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "Movie with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case movie with id as 22 was not found",
                                                value = "{\"timestamp\":1707145783796,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No movie found with id: 22\"]}"
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
    public ResponseEntity getMovieByIdActorsWithRoles(@PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") long id) {
        return movieService.getMovieActorsWithRoles(id);
    }

    //=========================POST MAPPINGS==================================
    @SecurityRequirement(name = "jwtAuth")
    @Operation(
            summary = "Create new movie",
            description = "Create new movie entry along with its genres, directors, writers, actors and their roles. Cover image file can optionaly be saved aswell. Allowed extensions are [png,jpg,jpeg] and max file size is 8MB",
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
                                                description = "In case movie was saved successfully",
                                                value = "{\"id\":1662,\"title\":\"title\",\"release_date\":\"18/10/1966\",\"cover_image_url\":null,\"description\":\"description\",\"audience_rating\":82,\"critics_rating\":null,\"length\":83,\"genres\":[{\"id\":8,\"name\":\"Drama\"},{\"id\":11,\"name\":\"History\"},{\"id\":12,\"name\":\"Horror\"}],\"directors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":6,\"first_name\":\"José\",\"last_name\":\"Luis González de León\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"writers\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":2,\"first_name\":\"Dodie\",\"last_name\":\"Smith\",\"profile_photo_url\":null,\"gender\":\"other\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"},{\"id\":3,\"name\":\"role3\"}]},{\"id\":4,\"first_name\":\"Harrison\",\"last_name\":\"Ford\",\"profile_photo_url\":\"http://localhost:8080/images/person/4.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"}]}],\"critiques\":[]}")
                                    }
                            )
                        }
                ),
                @ApiResponse(
                        description = "Movie json structure validation error occured",
                        responseCode = "400",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case movie with empty title was sent",
                                                value = "{\"timestamp\":1707153565041,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ValidationException\",\"details\":[\"Movie title must not be null or empty!\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Movie dependency object was not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case provided genre id of 70 was not found",
                                                value = "{\"timestamp\":1707153718619,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Genre with id: 70 does not exist in database!\"]}")
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
    public ResponseEntity postMovie(@Parameter(description = "Movie data in JSON format. Example: {\"title\":\"title\",\"release_date\":\"18/10/1966\",\"description\":\"description\",\"audience_rating\":82,\"length\":83,\"genres\":[8,11,12],\"directors\":[1,6,9],\"writers\":[1,2,9],\"actors\":[{\"id\":1,\"is_starring\":true,\"roles\":[\"role1\",\"role2\",\"role3\"]},{\"id\":4,\"is_starring\":false,\"roles\":[\"role1\"]}]}",
            required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
            @RequestPart("movie") MovieRequestDTO movie,
            @Parameter(description = "Cover Image", required = false, content = @Content(mediaType = "application/octet-stream"))
            @RequestPart(name = "cover_image", required = false) MultipartFile coverImage) {
        domainValidator.validate(movie, coverImage, RequestMethod.POST);
        if (coverImage != null) {
            movie.setCoverImage(new MyImage(coverImage));
        }
        return movieService.postMovie(movie);
    }

    //=========================PUT MAPPINGS===================================
    @SecurityRequirement(name = "jwtAuth")
    @Operation(
            summary = "Update given movie",
            description = "Update movie entry along with its genres, directors, writers, actors and their roles, by specified movie id. Cover image file can optionaly be updated aswell. Allowed extensions are [png,jpg,jpeg] and max file size is 8MB",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of movie (1..N)",
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
                                                description = "In case movie was updated successfully",
                                                value = "{\"id\":1662,\"title\":\"title\",\"release_date\":\"18/10/1966\",\"cover_image_url\":null,\"description\":\"description\",\"audience_rating\":82,\"critics_rating\":null,\"length\":83,\"genres\":[{\"id\":8,\"name\":\"Drama\"},{\"id\":11,\"name\":\"History\"},{\"id\":12,\"name\":\"Horror\"}],\"directors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":6,\"first_name\":\"José\",\"last_name\":\"Luis González de León\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"writers\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":2,\"first_name\":\"Dodie\",\"last_name\":\"Smith\",\"profile_photo_url\":null,\"gender\":\"other\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"},{\"id\":3,\"name\":\"role3\"}]},{\"id\":4,\"first_name\":\"Harrison\",\"last_name\":\"Ford\",\"profile_photo_url\":\"http://localhost:8080/images/person/4.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"}]}],\"critiques\":[]}"
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
                                                value = "{\"timestamp\":1707298120799,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Movie id must be greater than or equal to 1\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case movie with empty title was sent",
                                                value = "{\"timestamp\":1707154014067,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ValidationException\",\"details\":[\"Movie title must not be null or empty!\"]}"
                                        )
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Movie dependency object or movie itself was not found",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case movie with id 1665 was not found",
                                                value = "{\"timestamp\":1707237353404,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Movie with id: 1665 does not exist in database!\"]}"
                                        ),
                                        @ExampleObject(
                                                name = "Example 2",
                                                description = "In case provided genre id of 70 was not found",
                                                value = "{\"timestamp\":1707154074129,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"Genre with id: 70 does not exist in database!\"]}"
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
    public ResponseEntity putMovie(
            @PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") long id,
            @Parameter(description = "Movie data in JSON format. Example: {\"title\":\"title\",\"release_date\":\"18/10/1966\",\"description\":\"description\",\"audience_rating\":82,\"length\":83,\"genres\":[8,11,12],\"directors\":[1,6,9],\"writers\":[1,2,9],\"actors\":[{\"id\":1,\"is_starring\":true,\"roles\":[\"role1\",\"role2\",\"role3\"]},{\"id\":4,\"is_starring\":false,\"roles\":[\"role1\"]}]}",
                    required = true, content = @Content(mediaType = "application/json", schema = @Schema(type = "string")))
            @RequestPart("movie") MovieRequestDTO movie,
            @Parameter(description = "Cover Image", required = false, content = @Content(mediaType = "application/octet-stream"))
            @RequestPart(name = "cover_image", required = false) MultipartFile coverImage) {
        movie.setId(id);
        domainValidator.validate(movie, coverImage, RequestMethod.PUT);
        if (coverImage != null) {
            movie.setCoverImage(new MyImage(coverImage));
        }
        return movieService.putMovie(movie);
    }

    //=========================DELETE MAPPINGS================================
    @Operation(
            summary = "Delete movie with specified id",
            description = "Deletes movie data by its specified id",
            parameters = {
                @Parameter(
                        name = "id",
                        description = "The id of movie (1..N)",
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
                                                description = "In case movie with id as 1661 was deleted successfully",
                                                value = "{\"id\":1661,\"title\":\"title\",\"release_date\":\"18/10/1966\",\"cover_image_url\":null,\"description\":\"description\",\"audience_rating\":82,\"critics_rating\":null,\"length\":83,\"genres\":[{\"id\":8,\"name\":\"Drama\"},{\"id\":11,\"name\":\"History\"},{\"id\":12,\"name\":\"Horror\"}],\"directors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":6,\"first_name\":\"José\",\"last_name\":\"Luis González de León\",\"profile_photo_url\":null,\"gender\":\"male\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"writers\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\"},{\"id\":2,\"first_name\":\"Dodie\",\"last_name\":\"Smith\",\"profile_photo_url\":null,\"gender\":\"other\"},{\"id\":9,\"first_name\":\"Andrew\",\"last_name\":\"Stanton\",\"profile_photo_url\":\"http://localhost:8080/images/person/9.jpg\",\"gender\":\"male\"}],\"actors\":[{\"id\":1,\"first_name\":\"George\",\"last_name\":\"Lucas\",\"profile_photo_url\":\"http://localhost:8080/images/person/1.jpg\",\"gender\":\"male\",\"is_star\":false,\"is_starring\":true,\"roles\":[{\"id\":1,\"name\":\"role1\"},{\"id\":2,\"name\":\"role2\"},{\"id\":3,\"name\":\"role3\"}]},{\"id\":4,\"first_name\":\"Harrison\",\"last_name\":\"Ford\",\"profile_photo_url\":\"http://localhost:8080/images/person/4.jpg\",\"gender\":\"male\",\"is_star\":true,\"is_starring\":false,\"roles\":[{\"id\":1,\"name\":\"role1\"}]}],\"critiques\":[]}"
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
                                                value = "{\"timestamp\":1707145607351,\"title\":\"Bad Request\",\"status\":400,\"developer_message\":\"jakarta.validation.ConstraintViolationException\",\"details\":[\"Movie id must be greater than or equal to 1\"]}")
                                    }
                            )}
                ),
                @ApiResponse(
                        description = "User is either not logged in, or is logged in but has no administrator privilege",
                        responseCode = "401", content = @Content(schema = @Schema(implementation = Void.class))
                ),
                @ApiResponse(
                        description = "Movie with specified id doesn't exist",
                        responseCode = "404",
                        content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorDetail.class),
                                    examples = {
                                        @ExampleObject(
                                                name = "Example 1",
                                                description = "In case movie with id as 1661 was not found",
                                                value = "{\"timestamp\":1707145783796,\"title\":\"Resource Not Found\",\"status\":404,\"developer_message\":\"com.borak.kinweb.backend.exceptions.ResourceNotFoundException\",\"details\":[\"No movie found with id: 1661\"]}"
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
    public ResponseEntity deleteMovieById(@PathVariable @Min(value = 1, message = "Movie id must be greater than or equal to 1") long id) {
        return movieService.deleteMovieById(id);
    }

}
