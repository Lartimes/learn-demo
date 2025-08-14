package org.bankai;

public class Main {
    public static void main(String[] args) {
        int a =  0;
        System.out.println(~a & 1);
        a = 1;
        System.out.println(~a & 1);
    }
}
