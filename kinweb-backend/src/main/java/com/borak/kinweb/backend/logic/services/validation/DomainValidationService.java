/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.logic.services.validation;

import com.borak.kinweb.backend.domain.dto.movie.MovieRequestDTO;
import com.borak.kinweb.backend.domain.classes.MyImage;
import com.borak.kinweb.backend.exceptions.ValidationException;
import com.borak.kinweb.backend.logic.util.Util;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Mr. Poyo
 */
@Service
public class DomainValidationService {

    @Autowired
    private Util util;

    public void validatePOST(MovieRequestDTO movie, MultipartFile coverImage) throws ValidationException {
        if (movie == null) {
            throw new ValidationException("Movie must not be null!");
        }
        List<String> messages = new LinkedList<>();
        if (movie.getTitle() == null || movie.getTitle().isBlank()) {
            messages.add("Movie title must not be null or empty!");
        } else if (movie.getTitle().length() > 300) {
            messages.add("Movie title must have less than 300 characters!");
        }
        if (movie.getReleaseDate() == null) {
            messages.add("Movie release date must not be null!");
        }
        if (coverImage != null) {
            if (coverImage.getOriginalFilename() == null || coverImage.getOriginalFilename().isBlank()) {
                messages.add("Movie cover image unknown type! Valid types are: " + Arrays.toString(MyImage.VALID_EXTENSIONS));
            } else {
                int index = coverImage.getOriginalFilename().lastIndexOf(".");
                if (index == -1 || index == (coverImage.getOriginalFilename().length() - 1)) {
                    messages.add("Movie cover image invalid type! Valid types are: " + Arrays.toString(MyImage.VALID_EXTENSIONS));
                } else {
                    String extension = coverImage.getOriginalFilename().substring(index + 1).trim();
                    if (!Arrays.asList(MyImage.VALID_EXTENSIONS).contains(extension)) {
                        messages.add("Movie cover image invalid type! Valid types are: " + Arrays.toString(MyImage.VALID_EXTENSIONS));
                    }
                }
            }
            if (coverImage.getSize() > MyImage.IMAGE_MAX_SIZE) {
                messages.add("Movie cover image size too big! Max size is: " + MyImage.IMAGE_MAX_SIZE + " bytes");
            }
        }
        if (movie.getDescription() == null || movie.getDescription().isBlank()) {
            messages.add("Movie description must not be null or empty!");
        } else if (movie.getDescription().length() > 500) {
            messages.add("Movie description must have less than 500 characters!");
        }
        if (movie.getAudienceRating() == null) {
            messages.add("Movie audience rating must not be null!");
        } else if (movie.getAudienceRating() < 0 || movie.getAudienceRating() > 100) {
            messages.add("Movie audience rating must be between 0 and 100!");
        }
        if (movie.getLength() == null) {
            messages.add("Movie length must not be null!");
        } else if (movie.getLength() <= 0) {
            messages.add("Movie length must be greater than 0!");
        }
        if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
            messages.add("Genres field must not be null or empty!");
        } else {
            if (util.doesDuplicatesExistIgnoreNonNatural(movie.getGenres())) {
                messages.add("Genres field must not contain duplicate id values!");
            }
            int i = 1;
            for (Long genre : movie.getGenres()) {
                if (genre == null) {
                    messages.add("Invalid genre id at genre number: " + i + ". Genre id must not be null!");
                } else if (genre <= 0) {
                    messages.add("Invalid genre id at genre number: " + i + ". Genre id must be greater than 0!");
                }
                i++;
            }
        }
        if (movie.getDirectors() == null || movie.getDirectors().isEmpty()) {
            messages.add("Directors field must not be null or empty!");
        } else {
            if (util.doesDuplicatesExistIgnoreNonNatural(movie.getDirectors())) {
                messages.add("Directors field must not contain duplicate id values!");
            }
            int i = 1;
            for (Long director : movie.getDirectors()) {
                if (director == null) {
                    messages.add("Invalid director id at director number: " + i + ". Director id must not be null!");
                } else if (director <= 0) {
                    messages.add("Invalid director id at director number: " + i + ". Director id must be greater than 0!");
                }
                i++;
            }
        }
        if (movie.getWriters() == null || movie.getWriters().isEmpty()) {
            messages.add("Writers field must not be null or empty!");
        } else {
            if (util.doesDuplicatesExistIgnoreNonNatural(movie.getWriters())) {
                messages.add("Writers field must not contain duplicate id values!");
            }
            int i = 1;
            for (Long writer : movie.getWriters()) {
                if (writer == null) {
                    messages.add("Invalid writer id at writer number: " + i + ". Writer id must not be null!");
                } else if (writer <= 0) {
                    messages.add("Invalid writer id at writer number: " + i + ". Writer id must be greater than 0!");
                }
                i++;
            }
        }
        if (movie.getActors() == null || movie.getActors().isEmpty()) {
            messages.add("Actors field must not be null or empty!");
        } else {
            if (util.doesDuplicatesExistIgnoreNonNatural(movie.getActors().stream().map(MovieRequestDTO.Actor::getId).collect(Collectors.toList()))) {
                messages.add("Actors field must not contain duplicate id values!");
            }
            int i = 1;
            for (MovieRequestDTO.Actor actor : movie.getActors()) {
                if (actor == null) {
                    messages.add("Invalid actor at actor number: " + i + ". Actor must not be null!");
                } else {
                    if (actor.getId() == null) {
                        messages.add("Invalid actor id at actor number: " + i + ". Actor id must not be null!");
                    } else if (actor.getId() <= 0) {
                        messages.add("Invalid actor id at actor number: " + i + ". Actor id must be greater than 0!");
                    }
                    if (actor.getStarring() == null) {
                        messages.add("Invalid actor starring value at actor number: " + i + ". Actor starring value must not be null!");
                    }
                    if (actor.getRoles() == null || actor.getRoles().isEmpty()) {
                        messages.add("Invalid actor roles field at actor number: " + i + ". Actor roles field must not be null or empty!");
                    } else {
                        int j = 1;
                        for (String role : actor.getRoles()) {
                            if (role == null || role.isBlank()) {
                                messages.add("Invalid name of role number: " + j + " at actor number: " + i + ". Actor role name must not be null or empty!");
                            } else if (role.length() > 300) {
                                messages.add("Invalid name of role number: " + j + " at actor number: " + i + ". Actor role name must have less than 300 characters!");
                            }
                            j++;
                        }
                    }
                }
                i++;
            }
        }

        if (!messages.isEmpty()) {
            throw new ValidationException(messages.toArray(String[]::new));
        }
    }

    public void validatePUT(MovieRequestDTO movie, MultipartFile coverImage) throws ValidationException {
        if (movie == null) {
            throw new ValidationException("Movie must not be null!");
        }
        List<String> messages = new LinkedList<>();
        if (movie.getId() == null) {
            messages.add("Movie id must not be null!");
        } else if (movie.getId() <= 0) {
            messages.add("Movie id must be greater than or equal to 1!");
        }

        if (movie.getTitle() == null || movie.getTitle().isBlank()) {
            messages.add("Movie title must not be null or empty!");
        } else if (movie.getTitle().length() > 300) {
            messages.add("Movie title must have less than 300 characters!");
        }
        if (movie.getReleaseDate() == null) {
            messages.add("Movie release date must not be null!");
        }
        if (coverImage != null) {
            if (coverImage.getOriginalFilename() == null || coverImage.getOriginalFilename().isBlank()) {
                messages.add("Movie cover image unknown type! Valid types are: " + Arrays.toString(MyImage.VALID_EXTENSIONS));
            } else {
                int index = coverImage.getOriginalFilename().lastIndexOf(".");
                if (index == -1 || index == (coverImage.getOriginalFilename().length() - 1)) {
                    messages.add("Movie cover image invalid type! Valid types are: " + Arrays.toString(MyImage.VALID_EXTENSIONS));
                } else {
                    String extension = coverImage.getOriginalFilename().substring(index + 1).trim();
                    if (!Arrays.asList(MyImage.VALID_EXTENSIONS).contains(extension)) {
                        messages.add("Movie cover image invalid type! Valid types are: " + Arrays.toString(MyImage.VALID_EXTENSIONS));
                    }
                }
            }
            if (coverImage.getSize() > MyImage.IMAGE_MAX_SIZE) {
                messages.add("Movie cover image size too big! Max size is: " + MyImage.IMAGE_MAX_SIZE + " bytes");
            }
        }
        if (movie.getDescription() == null || movie.getDescription().isBlank()) {
            messages.add("Movie description must not be null or empty!");
        } else if (movie.getDescription().length() > 500) {
            messages.add("Movie description must have less than 500 characters!");
        }
        if (movie.getAudienceRating() == null) {
            messages.add("Movie audience rating must not be null!");
        } else if (movie.getAudienceRating() < 0 || movie.getAudienceRating() > 100) {
            messages.add("Movie audience rating must be between 0 and 100!");
        }
        if (movie.getLength() == null) {
            messages.add("Movie length must not be null!");
        } else if (movie.getLength() <= 0) {
            messages.add("Movie length must be greater than 0!");
        }
        if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
            messages.add("Genres field must not be null or empty!");
        } else {
            if (util.doesDuplicatesExistIgnoreNonNatural(movie.getGenres())) {
                messages.add("Genres field must not contain duplicate id values!");
            }
            int i = 1;
            for (Long genre : movie.getGenres()) {
                if (genre == null) {
                    messages.add("Invalid genre id at genre number: " + i + ". Genre id must not be null!");
                } else if (genre <= 0) {
                    messages.add("Invalid genre id at genre number: " + i + ". Genre id must be greater than 0!");
                }
                i++;
            }
        }
        if (movie.getDirectors() == null || movie.getDirectors().isEmpty()) {
            messages.add("Directors field must not be null or empty!");
        } else {
            if (util.doesDuplicatesExistIgnoreNonNatural(movie.getDirectors())) {
                messages.add("Directors field must not contain duplicate id values!");
            }
            int i = 1;
            for (Long director : movie.getDirectors()) {
                if (director == null) {
                    messages.add("Invalid director id at director number: " + i + ". Director id must not be null!");
                } else if (director <= 0) {
                    messages.add("Invalid director id at director number: " + i + ". Director id must be greater than 0!");
                }
                i++;
            }
        }
        if (movie.getWriters() == null || movie.getWriters().isEmpty()) {
            messages.add("Writers field must not be null or empty!");
        } else {
            if (util.doesDuplicatesExistIgnoreNonNatural(movie.getWriters())) {
                messages.add("Writers field must not contain duplicate id values!");
            }
            int i = 1;
            for (Long writer : movie.getWriters()) {
                if (writer == null) {
                    messages.add("Invalid writer id at writer number: " + i + ". Writer id must not be null!");
                } else if (writer <= 0) {
                    messages.add("Invalid writer id at writer number: " + i + ". Writer id must be greater than 0!");
                }
                i++;
            }
        }
        if (movie.getActors() == null || movie.getActors().isEmpty()) {
            messages.add("Actors field must not be null or empty!");
        } else {
            if (util.doesDuplicatesExistIgnoreNonNatural(movie.getActors().stream().map(MovieRequestDTO.Actor::getId).collect(Collectors.toList()))) {
                messages.add("Actors field must not contain duplicate id values!");
            }
            int i = 1;
            for (MovieRequestDTO.Actor actor : movie.getActors()) {
                if (actor == null) {
                    messages.add("Invalid actor at actor number: " + i + ". Actor must not be null!");
                } else {
                    if (actor.getId() == null) {
                        messages.add("Invalid actor id at actor number: " + i + ". Actor id must not be null!");
                    } else if (actor.getId() <= 0) {
                        messages.add("Invalid actor id at actor number: " + i + ". Actor id must be greater than 0!");
                    }
                    if (actor.getStarring() == null) {
                        messages.add("Invalid actor starring value at actor number: " + i + ". Actor starring value must not be null!");
                    }
                    if (actor.getRoles() == null || actor.getRoles().isEmpty()) {
                        messages.add("Invalid actor roles field at actor number: " + i + ". Actor roles field must not be null or empty!");
                    } else {
                        int j = 1;
                        for (String role : actor.getRoles()) {
                            if (role == null || role.isBlank()) {
                                messages.add("Invalid name of role number: " + j + " at actor number: " + i + ". Actor role name must not be null or empty!");
                            } else if (role.length() > 300) {
                                messages.add("Invalid name of role number: " + j + " at actor number: " + i + ". Actor role name must have less than 300 characters!");
                            }
                            j++;
                        }
                    }
                }
                i++;
            }
        }

        if (!messages.isEmpty()) {
            throw new ValidationException(messages.toArray(String[]::new));
        }
    }

    
    
//---------------------------------------------------------------------------------------------------------
    
    
    
}
