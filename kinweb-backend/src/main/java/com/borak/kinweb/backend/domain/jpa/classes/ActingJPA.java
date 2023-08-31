/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.borak.kinweb.backend.domain.jpa.classes;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Mr. Poyo
 */
@Entity(name = "Acting")
@Table(name = "acting")
@Access(AccessType.FIELD)
public class ActingJPA implements JPA{

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id")
    private MediaJPA media;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private ActorJPA actor;
    
    @Column(name = "is_starring")
    private boolean starring;  

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,mappedBy = "acting")
    private List<ActingRoleJPA> roles = new ArrayList<>();

    public ActingJPA() {
    }

    public ActingJPA(MediaJPA media, ActorJPA actor) {
        this.media = media;
        this.actor = actor;
    }

    public MediaJPA getMedia() {
        return media;
    }

    public void setMedia(MediaJPA media) {
        this.media = media;
    }

    public ActorJPA getActor() {
        return actor;
    }

    public void setActor(ActorJPA actor) {
        this.actor = actor;
    }

    public List<ActingRoleJPA> getRoles() {
        return roles;
    }

    public void setRoles(List<ActingRoleJPA> roles) {
        this.roles = roles;
    }

    public boolean isStarring() {
        return starring;
    }

    public void setStarring(boolean starring) {
        this.starring = starring;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(this.media);
        hash = 83 * hash + Objects.hashCode(this.actor);
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
        final ActingJPA other = (ActingJPA) obj;
        if (!Objects.equals(this.media, other.media)) {
            return false;
        }
        return Objects.equals(this.actor, other.actor);
    }
    
    

}
