package com.darteaga.teammanager.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.darteaga.teammanager.domain.util.CustomDateTimeDeserializer;
import com.darteaga.teammanager.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

import com.darteaga.teammanager.domain.enumeration.Roles;

/**
 * A User.
 */
@Entity
@Table(name = "user")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @NotNull        
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "password")
    private String password;

    @Pattern(regexp = ".pattner")        
    @Column(name = "email")
    private String email;

    @Min(value = 0)        
    @Column(name = "edad")
    private Integer edad;

    @NotNull        
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "fecha_nacimiento", nullable = false)
    private DateTime fechaNacimiento;
    
    @Lob
    @Column(name = "description")
    private byte[] description;

    @Column(name = "description_content_type", nullable = false)
    private String descriptionContentType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Roles role;
    
    @Column(name = "posision")
    private String posision;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Entrenamiento> entrenamientos = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "user_manytomany",
               joinColumns = @JoinColumn(name="users_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="manytomanys_id", referencedColumnName="ID"))
    private Set<Manytomany> manytomanys = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Manytomanynoowner> manytomanynoowners = new HashSet<>();

    @ManyToOne
    private Manytoone manytoone;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Onetoone onetoone;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public DateTime getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(DateTime fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public byte[] getDescription() {
        return description;
    }

    public void setDescription(byte[] description) {
        this.description = description;
    }

    public String getDescriptionContentType() {
        return descriptionContentType;
    }

    public void setDescriptionContentType(String descriptionContentType) {
        this.descriptionContentType = descriptionContentType;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public String getPosision() {
        return posision;
    }

    public void setPosision(String posision) {
        this.posision = posision;
    }

    public Set<Entrenamiento> getEntrenamientos() {
        return entrenamientos;
    }

    public void setEntrenamientos(Set<Entrenamiento> entrenamientos) {
        this.entrenamientos = entrenamientos;
    }

    public Set<Manytomany> getManytomanys() {
        return manytomanys;
    }

    public void setManytomanys(Set<Manytomany> manytomanys) {
        this.manytomanys = manytomanys;
    }

    public Set<Manytomanynoowner> getManytomanynoowners() {
        return manytomanynoowners;
    }

    public void setManytomanynoowners(Set<Manytomanynoowner> manytomanynoowners) {
        this.manytomanynoowners = manytomanynoowners;
    }

    public Manytoone getManytoone() {
        return manytoone;
    }

    public void setManytoone(Manytoone manytoone) {
        this.manytoone = manytoone;
    }

    public Onetoone getOnetoone() {
        return onetoone;
    }

    public void setOnetoone(Onetoone onetoone) {
        this.onetoone = onetoone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if ( ! Objects.equals(id, user.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + "'" +
                ", password='" + password + "'" +
                ", email='" + email + "'" +
                ", edad='" + edad + "'" +
                ", fechaNacimiento='" + fechaNacimiento + "'" +
                ", description='" + description + "'" +
                ", descriptionContentType='" + descriptionContentType + "'" +
                ", role='" + role + "'" +
                ", posision='" + posision + "'" +
                '}';
    }
}
