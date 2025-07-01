package com.example.demo;

import java.text.Normalizer;

public class Teste {

    public static void main(String[] args) {
        String x = " 02/07/2025 08:00 Nº";
        System.out.println(Normalizer.normalize(x, Normalizer.Form.NFC));
        System.out.println(Normalizer.normalize(x, Normalizer.Form.NFD));
        System.out.println(Normalizer.normalize(x, Normalizer.Form.NFKC));
        System.out.println(Normalizer.normalize(x, Normalizer.Form.NFKD));
    }
}
