package models;

import traits.CarColor;
import traits.CarMake;

public abstract class Vehicle {

    private final int ID;
    private final CarColor color;
    private final CarMake make;
    private final double dailyRate;
    private int lateRate;
    private int seats;


    Vehicle(int id, CarMake make, CarColor color, double rate){

        this.ID = id;
        this.color = color;
        this.make = make;
        this.dailyRate = rate;
        this.lateRate = (int) Math.floor(rate * 1.5);
        this.seats = 4;

    }

    protected void setSeats(int seats){
        this.seats = seats;
    }
    protected void setLateRate(int lateRate){
        this.lateRate = lateRate;
    }

    @Override
    public String toString(){
        String vehicleString = "ID: " + this.ID + "\n";

        vehicleString+= "MAKE: " + this.make + "\nCOLOR: " + this.color +"\nSEATS: " + this.seats + "\n";
        vehicleString+= "RATE: " + this.dailyRate + "\nLATE PENALTY: " + this.lateRate + "\n";

        return vehicleString;
    }

    public int getID(){return this.ID;}


    public double getRate(){
        return this.dailyRate;
    }

    public CarColor getColor() {
        return color;
    }

    public CarMake getMake() {
        return make;
    }

    public int getLateRate() {
        return lateRate;
    }

    public int getSeats() {
        return seats;
    }

    public String exportString() {
        String vehicleString = "ID: " + this.ID + "\n";

        vehicleString+= "MAKE: " + this.make + "\nCOLOR: " + this.color +"\nSEATS: " + this.seats + "\n";
        vehicleString+= "RATE: " + this.dailyRate + "\nLATE PENALTY: " + this.lateRate + "\n";

        return vehicleString;
    }

    @Override
    public boolean equals(Object o){
        Vehicle target = (Vehicle) o;
        return this.ID == target.getID();
    }
}