package models;

import traits.CarColor;
import traits.CarMake;

public class SUV extends Vehicle{

    private boolean hasTrailerHitch;
    private double towCapacity;

    public SUV(int id, CarMake make, CarColor color, double rate) {
        super(id, make, color, rate);
        super.setLateRate(20);
        super.setSeats(5);
        this.hasTrailerHitch = false;
        this.towCapacity = 0.0;
    }

    public SUV(int id, CarMake make, CarColor color, double rate, double towCapacity, boolean hasTrailerHitch){
        super(id, make, color, rate);
        super.setLateRate(20);
        super.setSeats(5);
        this.hasTrailerHitch = hasTrailerHitch;
        this.towCapacity = towCapacity;
    }

    @Override
    public String toString(){
        String suvString = "MODEL: SUV\nTRAILER_HITCH: " + this.hasTrailerHitch + "\nTOW CAPACITY: " + this.towCapacity + "\n";
        suvString += super.toString();
        return suvString;
    }

    public String exportString(){
        String SUVString = "";
        SUVString+= this.getID() + ", ";
        SUVString+= this.getMake().toString() + ", ";
        SUVString+= "SUV, ";
        SUVString+= this.getColor().toString() + ", ";
        SUVString+= this.getRate() + ", ";
        SUVString+= this.towCapacity + ", ";
        SUVString+= this.hasTrailerHitch + ",\n";

        return SUVString;
    }
}
