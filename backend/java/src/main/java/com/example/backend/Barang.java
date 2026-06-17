package com.example.backend;

public abstract class Barang implements Printable {
    private final String id;
    private final String nama;
    private final double harga;

    public Barang(String id, String nama, double harga) {
        this.id = id;
        this.nama = nama;
        this.harga = harga;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public double getHarga() {
        return harga;
    }

    public abstract String getCategory();

    @Override
    public String printDetails() {
        return String.format("%s[id=%s, nama=%s, harga=%.2f]", getCategory(), id, nama, harga);
    }
}
