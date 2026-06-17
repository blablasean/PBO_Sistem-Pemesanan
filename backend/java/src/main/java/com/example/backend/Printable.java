package com.example.backend;

public interface Printable {
    String printDetails();

    default String printInfo() {
        return printDetails();
    }
}
