package com.example.loginfirebasemail77.modelos;

public class sensores {
    private String DistanciaDerecha;
    private  String DistanciaFrente;
    private  String DistanciaIzquierda;
    private  String cayo;

    public sensores() {
    }

    public sensores(String distanciaDerecha, String distanciaFrente, String distanciaIzquierda, String cayo) {
        this.DistanciaDerecha = distanciaDerecha;
        this.DistanciaFrente = distanciaFrente;
        this.DistanciaIzquierda = distanciaIzquierda;
        this.cayo = cayo;
    }

    public String getDistanciaDerecha() {
        return DistanciaDerecha;
    }

    public void setDistanciaDerecha(String distanciaDerecha) {
        DistanciaDerecha = distanciaDerecha;
    }

    public String getDistanciaFrente() {
        return DistanciaFrente;
    }

    public void setDistanciaFrente(String distanciaFrente) {
        DistanciaFrente = distanciaFrente;
    }

    public String getDistanciaIzquierda() {
        return DistanciaIzquierda;
    }

    public void setDistanciaIzquierda(String distanciaIzquierda) {
        DistanciaIzquierda = distanciaIzquierda;
    }

    public String getCayo() {
        return cayo;
    }

    public void setCayo(String cayo) {
        this.cayo = cayo;
    }

    @Override
    public String toString() {
        return "sensores{" +
                "DistanciaDerecha='" + DistanciaDerecha + '\'' +
                ", DistanciaFrente='" + DistanciaFrente + '\'' +
                ", DistanciaIzquierda='" + DistanciaIzquierda + '\'' +
                ", cayo='" + cayo + '\'' +
                '}';
    }
}
