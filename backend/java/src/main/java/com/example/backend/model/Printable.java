package com.example.backend.model;

public interface Printable {
    String printInfo();

    default String printDetails() {
        return printInfo();
    }
}
