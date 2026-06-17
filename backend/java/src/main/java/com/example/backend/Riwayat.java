package com.example.backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Riwayat implements Printable {
    private final List<Transaksi> transaksiList = new ArrayList<>();

    public void addTransaksi(Transaksi transaksi) {
        transaksiList.add(transaksi);
    }

    public List<Transaksi> getTransaksiList() {
        return Collections.unmodifiableList(transaksiList);
    }

    @Override
    public String printDetails() {
        StringBuilder builder = new StringBuilder("Riwayat peminjaman:\n");
        for (Transaksi transaksi : transaksiList) {
            builder.append(transaksi.printDetails()).append("\n");
        }
        return builder.toString();
    }
}
