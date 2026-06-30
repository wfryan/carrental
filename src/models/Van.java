package models;

import traits.CarColor;
import traits.CarMake;

public class Van extends Vehicle{

    private int cargoWeight;


    public Van(int id, CarMake make, CarColor color, double rate, int weight) {
        super(id, make, color, rate);
        super.setSeats(4);
        this.cargoWeight = weight;
    }
    public Van(int id, CarMake make, CarColor color, double rate) {
        super(id, make, color, rate);
        this.cargoWeight = 300;
    }

    public void setLate(int late){
        super.setLateRate(late);
    }
    public void setSeats(int seats){
        super.setSeats(seats);
    }

    @Override
    public String toString(){
        String vanString = "MODEL: VAN\nCARGO_LIMIT: " + this.cargoWeight + "\n";
        vanString += super.toString();
        return vanString;
    }

    public String exportString(){
        String vanString = "";
        vanString+= this.getID() + ", ";
        vanString+= this.getMake().toString() + ", ";
        vanString+= "VAN, ";
        vanString+= this.getColor().toString() + ", ";
        vanString+= this.getRate() + ", ";
        vanString+= this.cargoWeight + ",\n";

        return vanString;
    }
}
