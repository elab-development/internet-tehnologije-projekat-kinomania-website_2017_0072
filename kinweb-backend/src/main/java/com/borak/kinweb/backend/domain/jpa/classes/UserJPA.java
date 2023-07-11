/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.domain.jpa.classes;


import com.borak.kinweb.backend.domain.enums.Gender;
import com.borak.kinweb.backend.domain.jpa.converters.GenderConverter;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Mr Poyo
 */
@Entity(name = "User")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "user")
@Access(AccessType.FIELD)
public abstract class UserJPA implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Convert(converter = GenderConverter.class)
    @Column(name = "gender", length = 1, nullable = false)
    private Gender gender;

    @Column(name = "profile_image_url", length = 500, nullable = false)
    private String profileImageUrl;

    @Column(name = "username", length = 300, nullable = false, unique = true)
    private String username;

    @Column(name = "email", length = 300, nullable = false, unique = true)
    private String email;

    @Column(name = "password", length = 300, nullable = false)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryJPA country;

    @ManyToMany
    @JoinTable(name = "user_media",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "media_id", referencedColumnName = "id"))
    private List<MediaJPA> library;

    public UserJPA() {
    }

    public UserJPA(Long id, String firstName, String lastName, Gender gender, String profileImageUrl, String username, String email, String password, CountryJPA country, List<MediaJPA> library) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.username = username;
        this.email = email;
        this.password = password;
        this.country = country;
        this.library = library;
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

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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

    public CountryJPA getCountry() {
        return country;
    }

    public void setCountry(CountryJPA country) {
        this.country = country;
    }

    public List<MediaJPA> getLibrary() {
        return library;
    }

    public void setLibrary(List<MediaJPA> library) {
        this.library = library;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.id);
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
        final UserJPA other = (UserJPA) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return "User{" + "firstName=" + firstName + ", lastName=" + lastName + '}';
    }
    
    

}
