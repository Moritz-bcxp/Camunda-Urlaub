package com.camunda.urlaub.model;

public class UrlaubsAntrag {
    private String antragsteller;
    private Integer tageAnzahl;
    private String vonDatum;
    private String bisDatum;
    private String grund;
    private boolean approved;
    private Integer verfuegbareUrlaubstage;

    // Constructors
    public UrlaubsAntrag() {}

    public UrlaubsAntrag(String antragsteller, Integer tageAnzahl, String vonDatum, String bisDatum, String grund) {
        this.antragsteller = antragsteller;
        this.tageAnzahl = tageAnzahl;
        this.vonDatum = vonDatum;
        this.bisDatum = bisDatum;
        this.grund = grund;
        this.approved = false;
    }

    // Getters and Setters
    public String getAntragsteller() {
        return antragsteller;
    }

    public void setAntragsteller(String antragsteller) {
        this.antragsteller = antragsteller;
    }

    public Integer getTageAnzahl() {
        return tageAnzahl;
    }

    public void setTageAnzahl(Integer tageAnzahl) {
        this.tageAnzahl = tageAnzahl;
    }

    public String getVonDatum() {
        return vonDatum;
    }

    public void setVonDatum(String vonDatum) {
        this.vonDatum = vonDatum;
    }

    public String getBisDatum() {
        return bisDatum;
    }

    public void setBisDatum(String bisDatum) {
        this.bisDatum = bisDatum;
    }

    public String getGrund() {
        return grund;
    }

    public void setGrund(String grund) {
        this.grund = grund;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public Integer getVerfuegbareUrlaubstage() {
        return verfuegbareUrlaubstage;
    }

    public void setVerfuegbareUrlaubstage(Integer verfuegbareUrlaubstage) {
        this.verfuegbareUrlaubstage = verfuegbareUrlaubstage;
    }

    @Override
    public String toString() {
        return "UrlaubsAntrag{" +
                "antragsteller='" + antragsteller + '\'' +
                ", tageAnzahl=" + tageAnzahl +
                ", vonDatum='" + vonDatum + '\'' +
                ", bisDatum='" + bisDatum + '\'' +
                ", grund='" + grund + '\'' +
                ", approved=" + approved +
                ", verfuegbareUrlaubstage=" + verfuegbareUrlaubstage +
                '}';
    }
}
