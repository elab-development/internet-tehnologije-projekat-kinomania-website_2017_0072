/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.domain.jdbc.classes;

import com.borak.kinweb.backend.domain.enums.Gender;
import com.borak.kinweb.backend.domain.enums.UserRole;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Mr Poyo
 */
public class UserJDBC implements Serializable {

    private Long id;

    private String firstName;

    private String lastName;

    private Gender gender;

    private String profileName;

    private String profileImage;

    private String username;

    private String email;

    private String password;

    private UserRole role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private CountryJDBC country;

    private List<MediaJDBC> medias = new ArrayList<>();

    private List<CritiqueJDBC> critiques = new ArrayList<>();

    public UserJDBC() {
    }

    public UserJDBC(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserJDBC(Long id, String firstName, String lastName, Gender gender, String profileName, String profileImage, String username, String email, String password, UserRole role, LocalDateTime createdAt, LocalDateTime updatedAt, CountryJDBC country) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.profileName = profileName;
        this.profileImage = profileImage;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CountryJDBC getCountry() {
        return country;
    }

    public void setCountry(CountryJDBC country) {
        this.country = country;
    }

    public List<MediaJDBC> getMedias() {
        return medias;
    }

    public void setMedias(List<MediaJDBC> medias) {
        if (medias == null) {
            this.medias = new ArrayList<>();
        } else {
            this.medias = medias;
        }
    }

    public List<CritiqueJDBC> getCritiques() {
        return critiques;
    }

    public void setCritiques(List<CritiqueJDBC> critiques) {
        if (critiques == null) {
            this.critiques = new ArrayList<>();
        } else {
            this.critiques = critiques;
        }
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Objects.hashCode(this.username);
        hash = 17 * hash + Objects.hashCode(this.password);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserJDBC other = (UserJDBC) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return Objects.equals(this.password, other.password);
    }

    
    
}
