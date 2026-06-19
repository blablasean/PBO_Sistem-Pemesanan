package com.example.backend.model;

import java.util.HashMap;
import java.util.Map;

public abstract class Barang implements Printable {
    private final String idBarang;
    private final String namaBarang;
    private final String merk;
    private final double hargaSewa;
    private String statusPeminjaman;
    private String imageUrl;
    private String imageData;

    public Barang(String idBarang, String namaBarang, String merk, double hargaSewa, String statusPeminjaman) {
        this.idBarang = idBarang;
        this.namaBarang = namaBarang;
        this.merk = merk;
        this.hargaSewa = hargaSewa;
        this.statusPeminjaman = statusPeminjaman;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public String getMerk() {
        return merk;
    }

    public double getHargaSewa() {
        return hargaSewa;
    }

    public String getStatusPeminjaman() {
        return statusPeminjaman;
    }

    public void setStatusPeminjaman(String statusPeminjaman) {
        this.statusPeminjaman = statusPeminjaman;
    }

    public String getId() {
        return idBarang;
    }

    public String getNama() {
        return namaBarang;
    }

    public double getHarga() {
        return hargaSewa;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public abstract String getCategory();

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", idBarang);
        map.put("name", namaBarang);
        map.put("merk", merk);
        map.put("category", getCategory());
        map.put("status", statusPeminjaman);
        map.put("price", hargaSewa);
        map.put("image_url", imageData != null && !imageData.isBlank() ? imageData : imageUrl);
        map.put("image_data", imageData);
        return map;
    }

    @Override
    public String printInfo() {
        return printDetails();
    }

    @Override
    public String printDetails() {
        return String.format("%s[idBarang=%s, namaBarang=%s, merk=%s, hargaSewa=%.2f, statusPeminjaman=%s]",
                getCategory(), idBarang, namaBarang, merk, hargaSewa, statusPeminjaman);
    }
}
