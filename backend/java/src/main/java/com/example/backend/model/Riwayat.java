package com.example.backend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Riwayat implements Printable {
    private final List<Transaksi> daftarTransaksi = new ArrayList<>();

    public void tambahTransaksi(Transaksi transaksi) {
        daftarTransaksi.add(transaksi);
    }

    public void addTransaksi(Transaksi transaksi) {
        tambahTransaksi(transaksi);
    }

    public List<Transaksi> getDaftarTransaksi() {
        return Collections.unmodifiableList(daftarTransaksi);
    }

    @Override
    public String printInfo() {
        return printDetails();
    }

    @Override
    public String printDetails() {
        StringBuilder builder = new StringBuilder("Riwayat peminjaman:\n");
        for (Transaksi transaksi : daftarTransaksi) {
            builder.append(transaksi.printDetails()).append("\n");
        }
        return builder.toString();
    }
}
