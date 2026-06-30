package models;

import traits.CarColor;
import traits.CarMake;

public class Sedan extends Vehicle{
    private final int doors;

    public Sedan(int id, CarMake make, CarColor color, double rate){
        super(id, make, color, rate);
        this.doors = 4;

    }

    public Sedan(int id, CarMake make, CarColor color, double rate, int doors){
        super(id, make, color, rate);
        this.doors = doors;
    }

    public void setLate(int late){
        super.setLateRate(late);
    }
    public void setSeats(int seats){
        super.setSeats(seats);
    }

    @Override
    public String toString(){
        String sedanString = "MODEL: SEDAN\nDOORS: " + this.doors;
        sedanString += super.toString();
        return sedanString;
    }
    public String exportString(){
        String sedanString = "";
        sedanString+= this.getID() + ", ";
        sedanString+= this.getMake().toString() + ", ";
        sedanString+= "SEDAN, ";
        sedanString+= this.getColor().toString() + ", ";
        sedanString+= this.getRate() + ", ";
        sedanString+= this.doors + ",\n";

        return sedanString;
    }

}
