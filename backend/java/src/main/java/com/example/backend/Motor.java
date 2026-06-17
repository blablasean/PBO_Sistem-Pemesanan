package com.example.backend;

import java.util.Map;

public class Motor extends Barang {
    private final int cc;
    private final String tipe;

    public Motor(String id, String nama, double harga, int cc, String tipe) {
        super(id, nama, "Unknown", harga, "Tersedia");
        this.cc = cc;
        this.tipe = tipe;
    }

    public Motor(String id, String nama, double harga, int cc) {
        this(id, nama, harga, cc, "Standard");
    }

    public Motor(String id, String nama, String merk, double hargaSewa, String statusPeminjaman, int cc, String tipe) {
        super(id, nama, merk, hargaSewa, statusPeminjaman);
        this.cc = cc;
        this.tipe = tipe;
    }

    public int getCc() {
        return cc;
    }

    public String getTipe() {
        return tipe;
    }

    @Override
    public String getCategory() {
        return "Motor";
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("cc", cc);
        map.put("tipe", tipe);
        return map;
    }

    @Override
    public String printInfo() {
        return String.format("%s, cc=%d, tipe=%s", super.printDetails(), cc, tipe);
    }

    @Override
    public String printDetails() {
        return printInfo();
    }
}
