package com.example.backend;

public abstract class Barang implements Printable {
    private final String idBarang;
    private final String namaBarang;
    private final String merk;
    private final double hargaSewa;
    private String statusPeminjaman;

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

    public abstract String getCategory();

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
