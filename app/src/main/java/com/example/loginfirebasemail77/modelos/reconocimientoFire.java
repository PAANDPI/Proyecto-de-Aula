package com.example.loginfirebasemail77.modelos;

public class reconocimientoFire {
    private String idReconocimiento;
    private String url;
    private String macDispositivo;
    private String confianza;
    private String reconocimiento;

    public reconocimientoFire() {
    }

    public reconocimientoFire(String idReconocimiento, String url, String macDispositivo, String confianza, String reconocimiento) {
        this.idReconocimiento = idReconocimiento;
        this.url = url;
        this.macDispositivo = macDispositivo;
        this.confianza = confianza;
        this.reconocimiento = reconocimiento;
    }

    public reconocimientoFire(String confianza, String reconocimiento) {
        this.confianza = confianza;
        this.reconocimiento = reconocimiento;
    }

    public String getIdReconocimiento() {
        return idReconocimiento;
    }

    public void setIdReconocimiento(String idReconocimiento) {
        this.idReconocimiento = idReconocimiento;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMacDispositivo() {
        return macDispositivo;
    }

    public void setMacDispositivo(String macDispositivo) {
        this.macDispositivo = macDispositivo;
    }

    public String getConfianza() {
        return confianza;
    }

    public void setConfianza(String confianza) {
        this.confianza = confianza;
    }

    public String getReconocimiento() {
        return reconocimiento;
    }

    public void setReconocimiento(String reconocimiento) {
        this.reconocimiento = reconocimiento;
    }

    @Override
    public String toString() {
        return  "Confianza:        " + confianza + '\n' +
                "Reconocimiento=:  " + reconocimiento + '\n';
    }
}
