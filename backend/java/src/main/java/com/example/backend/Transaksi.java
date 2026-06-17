package com.example.backend;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class Transaksi implements Printable {
    private final String idTransaksi;
    private final Date tanggalPinjam;
    private final Date tanggalKembali;
    private double totalBiaya;
    private final Barang barang;
    private final User user;
    private String status;

    public Transaksi(String idTransaksi, User user, Barang barang, Date tanggalPinjam, Date tanggalKembali, String status) {
        this.idTransaksi = idTransaksi;
        this.user = user;
        this.barang = barang;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
        this.totalBiaya = barang.getHargaSewa() * getDurasi();
    }

    public Transaksi(String idTransaksi, User user, Barang barang, String tanggalPinjam, String tanggalKembali, String status) throws ParseException {
        this(idTransaksi, user, barang,
                new SimpleDateFormat("yyyy-MM-dd").parse(tanggalPinjam),
                new SimpleDateFormat("yyyy-MM-dd").parse(tanggalKembali),
                status);
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public Date getTanggalPinjam() {
        return tanggalPinjam;
    }

    public Date getTanggalKembali() {
        return tanggalKembali;
    }

    public double getTotalBiaya() {
        return totalBiaya;
    }

    public Barang getBarang() {
        return barang;
    }

    public User getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }

    public int getDurasi() {
        long diffMillis = tanggalKembali.getTime() - tanggalPinjam.getTime();
        int days = (int) TimeUnit.DAYS.convert(diffMillis, TimeUnit.MILLISECONDS);
        return Math.max(days, 1);
    }

    public double hitungBiaya() {
        int durasi = getDurasi();
        totalBiaya = durasi * barang.getHarga();
        return totalBiaya;
    }

    public void bayar() {
        this.status = "Lunas";
        System.out.printf("Transaksi %s telah dibayar. Total biaya: %.2f%n", idTransaksi, totalBiaya);
    }

    @Override
    public String printInfo() {
        return printDetails();
    }

    @Override
    public String printDetails() {
        return String.format("Transaksi[id=%s, user=%s, barang=%s, pinjam=%s, kembali=%s, durasi=%d hari, totalBiaya=%.2f, status=%s]",
                idTransaksi, user.getNama(), barang.getNama(), tanggalPinjam, tanggalKembali, getDurasi(), totalBiaya, status);
    }
}
