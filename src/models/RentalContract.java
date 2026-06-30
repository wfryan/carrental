package models;

import traits.ContractStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


public class RentalContract {
    private int contractId;
    private Vehicle rentalVehicle;
    private int duration;
    private LocalDate startDate;
    private LocalDate endDate;
    private ContractStatus status;
    private int cleaningFees;
    private final Customer rentalCustomer;

    public RentalContract(int id, Vehicle rental, int duration, LocalDate start, ContractStatus status, Customer rentalCustomer){
        this.contractId = id;
        this.rentalVehicle = rental;
        this.duration = duration;
        this.startDate = start;
        this.endDate = start.plusDays(duration);
        this.status =  status;
        this.cleaningFees = 20;
        this.rentalCustomer = rentalCustomer;
    }
    public RentalContract(int id, Vehicle rental, int duration, LocalDate start, ContractStatus status, int cleaningFees, Customer rentalCustomer){
        this.contractId = id;
        this.rentalVehicle = rental;
        this.duration = duration;
        this.startDate = start;
        this.endDate = start.plusDays(duration);
        this.status =  status;
        this.cleaningFees = cleaningFees;
        this.rentalCustomer = rentalCustomer;
    }

    private long howLate(LocalDate returnedDate){
        LocalDate deadline = startDate.plusDays(duration);
        if(returnedDate.isAfter(deadline)){
            return ChronoUnit.DAYS.between(deadline, returnedDate);
        }
        return 0;
    }

    private boolean isLate(){
        LocalDate deadline = startDate.plusDays(duration);
        LocalDate today = LocalDate.now();
        return today.isAfter(deadline);
    }

    public boolean isActive(){
        return (this.status == ContractStatus.ACTIVE || this.status == ContractStatus.LATE || this.status == ContractStatus.UPCOMING);
    }

    public boolean returnActive(){
        return (this.status == ContractStatus.ACTIVE || this.status == ContractStatus.LATE);}


    public boolean isUpcoming(){
        return this.status == ContractStatus.UPCOMING;
    }

    private boolean hasStarted(){
        LocalDate today = LocalDate.now();
        return (today.isAfter(this.startDate) && today.isBefore(this.endDate));
    }

    public void updateStatus(){
        if (this.status != ContractStatus.RETURNED){
            if (this.isLate()){
                this.status = ContractStatus.LATE;
            }
            if(this.hasStarted()){
                this.status = ContractStatus.ACTIVE;
            }
        }
    }

    public void returnCar(){
        this.status = ContractStatus.RETURNED;
    }

    public void cancelCar(){
        this.status = ContractStatus.CANCELED;
    }

    public void delay(){
        this.status = ContractStatus.DELAYED;
    }

    public int getVehicleID(){
        return this.rentalVehicle.getID();
    }

    /**
     * Checks if the rental is late,
     * @return the current status of the rental
     */
    public ContractStatus getStatus(){
        return this.status;
    }
    public String exportStr(){
        String exportString = this.contractId + ", ";
        exportString += rentalCustomer.getID() + ", ";
        exportString += rentalVehicle.getID() + ", ";
        exportString += duration + ", ";
        exportString += startDate.toString() + ", ";
        exportString += status.toString() +", ";
        exportString += cleaningFees + "\n";
        return exportString;
    }

    /*
    * Return contract information in an organized manner
    *
    * */
    @Override
    public String toString(){
        String contractString = "Rental Contract ID: " + this.contractId + "\n";
        contractString += "Customer: " + this.rentalCustomer.getName() + "\n";
        contractString += "Vehicle Details:\n" + this.rentalVehicle.toString() + "\n";
        contractString += "Duration: " + this.duration + " days\n";
        contractString += "Start Date: " + this.startDate + "\n";
        contractString += "End Date: " + this.endDate + "\n";
        contractString += "Status: " + this.status + "\n";
        contractString += "Cleaning Fees: $" + this.cleaningFees;

        return contractString;
    }

    /*
     * Get late fee rate from Vehicle base
     * Calculate the total late fee accrued
     * */
    public long calculateLateFee(){
        return this.howLate(LocalDate.now()) * this.rentalVehicle.getLateRate();
    }

    public Customer getRentalCustomer(){
        return this.rentalCustomer;
    }

    public int getContractId() {
        return this.contractId;
    }

    public LocalDate getStartDate() {
        return this.startDate;
    }
    public LocalDate getEndDate(){
        return this.endDate;
    }

    public int getCleaningFees() {
        return this.cleaningFees;
    }

    public void waiveClean(){
        this.cleaningFees = 0;
    }
}
