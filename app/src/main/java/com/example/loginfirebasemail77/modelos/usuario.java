package com.example.loginfirebasemail77.modelos;

public class usuario {
    private String idUsuario;
    private String username;
    private String email;
    private String numero;

    public usuario() {

    }
    public usuario(String idUsuario, String username, String email, String numero) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.email = email;
        this.numero = numero;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
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

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "usuario{" +
                "idUsuario='" + idUsuario + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", numero='" + numero + '\'' +
                '}';
    }
}
