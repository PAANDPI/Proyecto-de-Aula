package com.example.loginfirebasemail77.modelos;

public class ubicaciones {
    private  String idUbicacion;
    private  String fechaUbicación;
    private  String coorX;
    private  String coorY;
    private  String macDispositivo;

    public ubicaciones() {
    }

    public ubicaciones(String idUbicacion, String fechaUbicación, String coorX, String coorY, String macDispositivo) {

        this.idUbicacion = idUbicacion;
        this.fechaUbicación = fechaUbicación;
        this.coorX = coorX;
        this.coorY = coorY;
        this.macDispositivo = macDispositivo;
    }

    public String getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(String idUbicacion) {
        this.idUbicacion = idUbicacion;
    }

    public String getFechaUbicación() {
        return fechaUbicación;
    }

    public void setFechaUbicación(String fechaUbicación) {
        this.fechaUbicación = fechaUbicación;
    }

    public String getCoorX() {
        return coorX;
    }

    public void setCoorX(String coorX) {
        this.coorX = coorX;
    }

    public String getCoorY() {
        return coorY;
    }

    public void setCoorY(String coorY) {
        this.coorY = coorY;
    }

    public String getMacDispositivo() {
        return macDispositivo;
    }

    public void setMacDispositivo(String macDispositivo) {
        this.macDispositivo = macDispositivo;
    }

    @Override
    public String toString() {
        return  "Registrada:" + fechaUbicación +" \n" +
                "Longitud: " + coorX +"\n"+
                "Latitud: " + coorY +"\n"+
                "Mac: " + macDispositivo +"";
    }

}
