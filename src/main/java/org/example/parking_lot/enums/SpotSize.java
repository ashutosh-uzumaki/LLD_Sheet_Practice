package org.example.parking_lot.enums;

public enum SpotSize {
    SMALL(1),
    MEDIUM(2),
    LARGE(3);

    private final int rank;

    SpotSize(int rank){
        this.rank = rank;
    }

    public int getRank(){
        return rank;
    }
}
