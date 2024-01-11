/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.repository.jdbc;

import com.borak.kinweb.backend.domain.enums.Gender;
import com.borak.kinweb.backend.domain.enums.UserRole;
import com.borak.kinweb.backend.domain.jdbc.classes.ActingJDBC;
import com.borak.kinweb.backend.domain.jdbc.classes.ActingRoleJDBC;
import com.borak.kinweb.backend.domain.jdbc.classes.CritiqueJDBC;
import com.borak.kinweb.backend.domain.jdbc.classes.DirectorJDBC;
import com.borak.kinweb.backend.domain.jdbc.classes.GenreJDBC;
import com.borak.kinweb.backend.domain.jdbc.classes.MediaJDBC;
import com.borak.kinweb.backend.domain.jdbc.classes.MovieJDBC;
import com.borak.kinweb.backend.domain.jdbc.classes.UserJDBC;
import com.borak.kinweb.backend.domain.jdbc.classes.WriterJDBC;
import com.borak.kinweb.backend.domain.security.SecurityUser;
import com.borak.kinweb.backend.exceptions.DatabaseException;
import com.borak.kinweb.backend.repository.api.IUserRepository;
import com.borak.kinweb.backend.repository.sql.SQLMovie;
import com.borak.kinweb.backend.repository.sql.SQLUser;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Mr. Poyo
 */
@Repository
public class UserRepositoryJDBC implements IUserRepository<UserJDBC, Long> {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Optional<UserJDBC> findByUsername(String username) throws DatabaseException, IllegalArgumentException {
        try {
            if (username == null) {
                throw new IllegalArgumentException("Invalid parameter: username must be non-null");
            }
            UserJDBC user = jdbcTemplate.queryForObject(SQLUser.FIND_BY_USERNAME_PS, new Object[]{username}, new int[]{Types.VARCHAR}, SQLUser.userRM);
            return Optional.of(user);
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new DatabaseException("Error while searching for user with username: " + username, e);
        }
    }

    @Override
    public UserJDBC insert(UserJDBC entity) throws DatabaseException, IllegalArgumentException {
        try {
            if (entity == null) {
                throw new IllegalArgumentException("Invalid parameter: entity must be non-null");
            }
            performInsert(entity);
            return entity;
        } catch (NullPointerException | DataAccessException e) {
            throw new DatabaseException("Error while inserting user", e);
        }
    }

    @Override
    public void update(UserJDBC entity) throws DatabaseException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Optional<UserJDBC> findById(Long id) throws DatabaseException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean existsById(Long id) throws DatabaseException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<UserJDBC> findAll() throws DatabaseException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<UserJDBC> findAllPaginated(int page, int size) throws DatabaseException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public long count() throws DatabaseException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteById(Long id) throws DatabaseException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean existsUsername(String username) throws DatabaseException, IllegalArgumentException {
        try {
            if (username == null) {
                throw new IllegalArgumentException("Invalid parameter: username must be non-null");
            }
            jdbcTemplate.queryForObject(SQLUser.FIND_ID_BY_USERNAME_PS, new Object[]{username}, new int[]{Types.VARCHAR}, Long.class);
            return true;
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error while checking if username: " + username + " exists", e);
        }
    }

    @Override
    public boolean existsEmail(String email) throws DatabaseException, IllegalArgumentException {
        try {
            if (email == null) {
                throw new IllegalArgumentException("Invalid parameter: email must be non-null");
            }
            jdbcTemplate.queryForObject(SQLUser.FIND_ID_BY_EMAIL_PS, new Object[]{email}, new int[]{Types.VARCHAR}, Long.class);
            return true;
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error while checking if email: " + email + " exists", e);
        }
    }

    @Override
    public boolean existsProfileName(String profileName) throws DatabaseException, IllegalArgumentException {
        try {
            if (profileName == null) {
                throw new IllegalArgumentException("Invalid parameter: profile name must be non-null");
            }
            jdbcTemplate.queryForObject(SQLUser.FIND_ID_BY_PROFILE_NAME_PS, new Object[]{profileName}, new int[]{Types.VARCHAR}, Long.class);
            return true;
        } catch (IncorrectResultSizeDataAccessException e) {
            return false;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error while checking if profile name: " + profileName + " exists", e);
        }
    }

    @Override
    public Optional<UserJDBC> findByIdWithRelations(Long id) throws DatabaseException, IllegalArgumentException {
        try {
            if (id == null || id < 1) {
                throw new IllegalArgumentException("Invalid parameter: id must be non-null and greater than 0");
            }
            UserJDBC user = jdbcTemplate.queryForObject(SQLUser.FIND_BY_ID_PS, new Object[]{id}, new int[]{Types.BIGINT}, SQLUser.userRM);
            List<MediaJDBC> medias = jdbcTemplate.query(SQLUser.FIND_ALL_MEDIA_BY_USER_ID_PS, new Object[]{id}, new int[]{Types.BIGINT}, SQLUser.mediaRM);
            for (MediaJDBC media : medias) {
                List<GenreJDBC> genres = jdbcTemplate.query(SQLUser.FIND_ALL_GENRES_PS, new Object[]{media.getId()}, new int[]{Types.BIGINT}, SQLUser.genreRM);
                media.setGenres(genres);
            }
            List<CritiqueJDBC> critiques = jdbcTemplate.query(SQLUser.FIND_ALL_CRITIQUES_BY_USER_ID_PS, new Object[]{id}, new int[]{Types.BIGINT}, SQLUser.critiqueRM);
            for (CritiqueJDBC critique : critiques) {
                critique.setCritic(user);
                List<GenreJDBC> genres = jdbcTemplate.query(SQLUser.FIND_ALL_GENRES_PS, new Object[]{critique.getMedia().getId()}, new int[]{Types.BIGINT}, SQLUser.genreRM);
                critique.getMedia().setGenres(genres);
            }
            user.setMedias(medias);
            user.setCritiques(critiques);
            return Optional.of(user);
        } catch (IncorrectResultSizeDataAccessException ex) {
            return Optional.empty();
        } catch (DataAccessException e) {
            throw new DatabaseException("Error while searching for user with id: " + id, e);
        }
    }
//================================================================================================================

    private void performInsert(UserJDBC user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(SQLUser.INSERT_USER_PS, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, String.valueOf(user.getGender().getSymbol()));
            ps.setString(4, user.getProfileName());
            ps.setString(5, user.getProfileImage());
            ps.setString(6, user.getUsername());
            ps.setString(7, user.getEmail());
            ps.setString(8, user.getPassword());
            ps.setString(9, user.getRole().toString());
            ps.setTimestamp(10, Timestamp.valueOf(user.getCreatedAt()));
            ps.setTimestamp(11, Timestamp.valueOf(user.getUpdatedAt()));
            ps.setLong(12, user.getCountry().getId());
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
    }

}
