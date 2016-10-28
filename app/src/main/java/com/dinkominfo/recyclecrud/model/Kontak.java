package com.dinkominfo.recyclecrud.model;

/**
 * Created by ryanthe on 10/6/16.
 */

public class Kontak {
    private int idkontak;
    private String nama, email, notelp, alamat, foto;

    public int getIdkontak() {
        return idkontak;
    }

    public void setIdkontak(int idkontak) {
        this.idkontak = idkontak;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNotelp() {
        return notelp;
    }

    public void setNotelp(String notelp) {
        this.notelp = notelp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
