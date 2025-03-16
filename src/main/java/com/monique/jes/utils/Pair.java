package com.monique.jes.utils;

public class Pair<V1, V2> {
    private V1 first; // u8
    private V2 second; // u8

    public Pair(V1 first, V2 second) {
        this.first = first;
        this.second = second;
    }

    public static <V1, V2> Pair<V1, V2> of(V1 first, V2 second) {
        return new Pair<>(first, second);
    }

    public V1 getFirst() {
        return first;
    }

    public void setFirst(V1 first) {
        this.first = first;
    }

    public V2 getSecond() {
        return second;
    }

    public void setSecond(V2 second) {
        this.second = second;
    }
}
