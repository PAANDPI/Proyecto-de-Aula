package com.example.loginfirebasemail77.modelos;

public class paciente {
    private String idpatient;
    private String nameTutor;
    private String firstname;
    private String lastname;
    private String birthname;
    private String gender;
    private String imagBase64;
    private String decivename;
    private String macadress;
    private String state;
    private String idUsuario;

    public paciente() {

    }

    public paciente(String idpatient, String nameTutor, String firstname, String lastname, String birthname, String gender, String imagBase64, String decivename, String macadress, String state, String idUsuario) {
        this.idpatient = idpatient;
        this.nameTutor = nameTutor;
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthname = birthname;
        this.gender = gender;
        this.imagBase64 = imagBase64;
        this.decivename = decivename;
        this.macadress = macadress;
        this.state = state;
        this.idUsuario = idUsuario;
    }

    public String getIdpatient() {
        return idpatient;
    }

    public void setIdpatient(String idpatient) {
        this.idpatient = idpatient;
    }

    public String getNameTutor() {
        return nameTutor;
    }

    public void setNameTutor(String nameTutor) {
        this.nameTutor = nameTutor;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthname() {
        return birthname;
    }

    public void setBirthname(String birthname) {
        this.birthname = birthname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImagBase64() {
        return imagBase64;
    }

    public void setImagBase64(String imagBase64) {
        this.imagBase64 = imagBase64;
    }

    public String getDecivename() {
        return decivename;
    }

    public void setDecivename(String decivename) {
        this.decivename = decivename;
    }

    public String getMacadress() {
        return macadress;
    }

    public void setMacadress(String macadress) {
        this.macadress = macadress;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }
}
