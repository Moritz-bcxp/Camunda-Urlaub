package com.camunda.urlaub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mitarbeiter")
public class Mitarbeiter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "verfuegbare_urlaubstage", nullable = false)
    private Integer verfuegbareUrlaubstage;

    @Column(name = "email")
    private String email;

    @Column(name = "abteilung")
    private String abteilung;

    @Column(name = "personalnummer")
    private String personalnummer;

    // Constructors
    public Mitarbeiter() {
    }

    public Mitarbeiter(String name, Integer verfuegbareUrlaubstage) {
        this.name = name;
        this.verfuegbareUrlaubstage = verfuegbareUrlaubstage;
    }

    public Mitarbeiter(String name, Integer verfuegbareUrlaubstage, String email, String abteilung,
            String personalnummer) {
        this.name = name;
        this.verfuegbareUrlaubstage = verfuegbareUrlaubstage;
        this.email = email;
        this.abteilung = abteilung;
        this.personalnummer = personalnummer;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVerfuegbareUrlaubstage() {
        return verfuegbareUrlaubstage;
    }

    public void setVerfuegbareUrlaubstage(Integer verfuegbareUrlaubstage) {
        this.verfuegbareUrlaubstage = verfuegbareUrlaubstage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbteilung() {
        return abteilung;
    }

    public void setAbteilung(String abteilung) {
        this.abteilung = abteilung;
    }

    public String getPersonalnummer() {
        return personalnummer;
    }

    public void setPersonalnummer(String personalnummer) {
        this.personalnummer = personalnummer;
    }

    @Override
    public String toString() {
        return "Mitarbeiter{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", verfuegbareUrlaubstage=" + verfuegbareUrlaubstage +
                ", email='" + email + '\'' +
                ", abteilung='" + abteilung + '\'' +
                ", personalnummer='" + personalnummer + '\'' +
                '}';
    }
}
