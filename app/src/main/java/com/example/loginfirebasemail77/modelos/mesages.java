package com.example.loginfirebasemail77.modelos;

public class mesages {
    private String idMensage;
    private String macdispositivo;
    private String ultrasonicoUno;
    private String ultrasonicoDos;
    private String ultrasonicotres;
    private String giroscopio;


    public mesages() {
    }

    public mesages(String idMensage, String macdispositivo, String ultrasonicoUno, String ultrasonicoDos, String ultrasonicotres, String giroscopio) {
        this.idMensage = idMensage;
        this.macdispositivo = macdispositivo;
        this.ultrasonicoUno = ultrasonicoUno;
        this.ultrasonicoDos = ultrasonicoDos;
        this.ultrasonicotres = ultrasonicotres;
        this.giroscopio = giroscopio;
    }

    public String getIdMensage() {
        return idMensage;
    }

    public void setIdMensage(String idMensage) {
        this.idMensage = idMensage;
    }

    public String getMacdispositivo() {
        return macdispositivo;
    }

    public void setMacdispositivo(String macdispositivo) {
        this.macdispositivo = macdispositivo;
    }

    public String getUltrasonicoUno() {
        return ultrasonicoUno;
    }

    public void setUltrasonicoUno(String ultrasonicoUno) {
        this.ultrasonicoUno = ultrasonicoUno;
    }

    public String getUltrasonicoDos() {
        return ultrasonicoDos;
    }

    public void setUltrasonicoDos(String ultrasonicoDos) {
        this.ultrasonicoDos = ultrasonicoDos;
    }

    public String getUltrasonicotres() {
        return ultrasonicotres;
    }

    public void setUltrasonicotres(String ultrasonicotres) {
        this.ultrasonicotres = ultrasonicotres;
    }

    public String getGiroscopio() {
        return giroscopio;
    }

    public void setGiroscopio(String giroscopio) {
        this.giroscopio = giroscopio;
    }

    @Override
    public String toString() {
        return "mesages{" +
                "idMensage='" + idMensage + '\'' +
                ", macdispositivo='" + macdispositivo + '\'' +
                ", ultrasonicoUno='" + ultrasonicoUno + '\'' +
                ", ultrasonicoDos='" + ultrasonicoDos + '\'' +
                ", ultrasonicotres='" + ultrasonicotres + '\'' +
                ", giroscopio='" + giroscopio + '\'' +
                '}';
    }
}
