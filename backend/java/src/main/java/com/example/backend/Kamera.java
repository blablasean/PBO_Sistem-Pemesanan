package com.example.backend;

public class Kamera extends Barang {
    private final double megapixel;

    public Kamera(String id, String nama, double harga, double megapixel) {
        super(id, nama, harga);
        this.megapixel = megapixel;
    }

    public double getMegapixel() {
        return megapixel;
    }

    @Override
    public String getCategory() {
        return "Kamera";
    }

    @Override
    public String printDetails() {
        return String.format("%s, megapixel=%.1f", super.printDetails(), megapixel);
    }
}
