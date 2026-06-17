package com.example.backend;

import java.util.Map;

public class Kamera extends Barang {
    private final double megapixel;
    private final String resolusi;

    public Kamera(String id, String nama, double harga, double megapixel, String resolusi) {
        super(id, nama, "Unknown", harga, "Tersedia");
        this.megapixel = megapixel;
        this.resolusi = resolusi;
    }

    public Kamera(String id, String nama, double harga, double megapixel) {
        this(id, nama, harga, megapixel, "Unknown");
    }

    public Kamera(String id, String nama, String merk, double hargaSewa, String statusPeminjaman, double megapixel, String resolusi) {
        super(id, nama, merk, hargaSewa, statusPeminjaman);
        this.megapixel = megapixel;
        this.resolusi = resolusi;
    }

    public double getMegapixel() {
        return megapixel;
    }

    public String getResolusi() {
        return resolusi;
    }

    @Override
    public String getCategory() {
        return "Kamera";
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> map = super.toMap();
        map.put("megapixel", megapixel);
        map.put("resolusi", resolusi);
        return map;
    }

    @Override
    public String printInfo() {
        return String.format("%s, megapixel=%.1f, resolusi=%s", super.printDetails(), megapixel, resolusi);
    }

    @Override
    public String printDetails() {
        return printInfo();
    }
}
