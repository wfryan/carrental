package manager;

import models.Customer;
import models.RentalContract;
import traits.ContractStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VehicleReturnManager {
    private Customer returner;
    private int vehicleID;
    protected int activeIdx;
    protected int ledgerIdx;
    private RentalContract target;
    private ContractStatus location;

    public VehicleReturnManager(Customer returner){
        this.returner = returner;
    }

    /**
     * TODO: Refactor for the new Active contracts structure
     * @param all
     * @param active
     * @param late
     */
    public void parseAll(ArrayList<RentalContract> all, Map<Integer, List<RentalContract>> active, ArrayList<RentalContract> late)
    {
        boolean found = false;
        this.ledgerIdx = -1;
        for(int i = 0; i < all.size(); i++){
            RentalContract contract = all.get(i);
            if(contract.getRentalCustomer().equals(this.returner) && (contract.returnActive()) ){
                this.ledgerIdx = i;
                this.location = all.get(i).getStatus();
                this.vehicleID = all.get(i).getVehicleID();
                this.target = contract;
                found = true;
                break;
            }
        }
        if (!found){
            return;
        }
        switch(this.location){
            case ACTIVE:
                List<RentalContract> schedule = active.get(this.vehicleID);
                if (schedule != null){
                    for (int i = 0; i < schedule.size(); i++){
                        RentalContract contract = schedule.get(i);
                        if(contract.getRentalCustomer().equals(this.returner)){
                            this.target = contract;
                            this.activeIdx = i;
                            break;
                        }
                    }
                }
                break;
            case LATE:
                for(int i = 0; i < late.size(); i++){
                    if(late.get(i).getRentalCustomer().equals(this.returner)){
                        this.activeIdx = i;
                        this.target = late.get(i);
                        break;
                    }
                }
                break;
        }

    }

    public boolean parseCancel(ArrayList<RentalContract> all, Map<Integer, List<RentalContract>> active)
    {
        boolean found = false;
        this.ledgerIdx = -1;
        for(int i = 0; i < all.size(); i++){
            RentalContract contract = all.get(i);
            if(contract.getRentalCustomer().equals(this.returner) && (contract.isUpcoming()) ){
                this.ledgerIdx = i;
                this.location = all.get(i).getStatus();
                this.vehicleID = all.get(i).getVehicleID();
                this.target = contract;
                found = true;
                break;
            }
        }
        if (!found){
            return false;
        }
        if (this.location != ContractStatus.UPCOMING){
            return false;
        }
        List<RentalContract> schedule = active.get(this.vehicleID);
        if (schedule != null){
            for (int i = 0; i < schedule.size(); i++){
                RentalContract contract = schedule.get(i);
                if(contract.getRentalCustomer().equals(this.returner)){
                    this.target = contract;
                    this.activeIdx = i;
                    break;
                }
            }
        }
        return true;
    }

    public RentalContract getTarget(){
        System.out.println(this.target);
        return this.target;
    }
    public ContractStatus getLocation(){
        return this.location;
    }

    public int getVehicle() {
        return this.vehicleID;
    }

    public Customer getReturner(){
        return this.returner;
    }
}
