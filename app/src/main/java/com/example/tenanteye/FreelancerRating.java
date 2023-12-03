package com.example.tenanteye;

public class FreelancerRating {
    private int oneStar, twoStar, threeStar, fourStar, fiveStar;

    public double getTotal() {
        return (oneStar + twoStar * 2.0 + threeStar * 3.0 + fourStar * 4.0 + fiveStar * 5.0) / (oneStar + twoStar + threeStar + fourStar + fiveStar);
    }

    public int getOneStar() {
        return oneStar;
    }

    public void setOneStar(int oneStar) {
        this.oneStar = oneStar;
    }

    public int getTwoStar() {
        return twoStar;
    }

    public void setTwoStar(int twoStar) {
        this.twoStar = twoStar;
    }

    public int getThreeStar() {
        return threeStar;
    }

    public void setThreeStar(int threeStar) {
        this.threeStar = threeStar;
    }

    public int getFourStar() {
        return fourStar;
    }

    public void setFourStar(int fourStar) {
        this.fourStar = fourStar;
    }

    public int getFiveStar() {
        return fiveStar;
    }

    public void setFiveStar(int fiveStar) {
        this.fiveStar = fiveStar;
    }
}
