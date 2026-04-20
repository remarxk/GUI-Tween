package com.remarxk.guitween.util;

public class Tuple<T, U> {
    private T a;
    private U b;

    public Tuple(T a, U b) {
        this.a = a;
        this.b = b;
    }

    public void setA(T a) {
        this.a = a;
    }

    public T getA() {
        return a;
    }

    public void setB(U b) {
        this.b = b;
    }

    public U getB() {
        return b;
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
