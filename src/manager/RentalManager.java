package manager;

import Inventory.Inventory;
import models.*;
import traits.CarColor;
import traits.CarMake;
import traits.ContractStatus;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class RentalManager {

    private Map<Integer, List<RentalContract>> activeContracts;
    private ArrayList<RentalContract> lateRentals;
    private ArrayList<RentalContract> allContracts;
    private Inventory carInventory;
    private Map<Integer, Customer> customers;
    private Admin rentalAdmin;

    public RentalManager(){
        this.rentalAdmin = new Admin("Admin", "Admin");
        start();
    }

    public RentalManager(String filepath){
        this.rentalAdmin = new Admin("Admin", "Admin");
        startCustom(filepath);
    }

    // Start the rental system
    public void start(){
        this.carInventory = new Inventory();
        StateManager loader = new StateManager();
        this.customers = loader.loadCustomers();
        this.allContracts = loader.loadRentals(this.carInventory, this.customers);
        this.activeContracts = loader.scanActives(this.allContracts);
        this.lateRentals = loader.scanLates(this.allContracts);
        loader = null;

    }

    public void startCustom(String filepath){
        this.carInventory = new Inventory(filepath);
        StateManager loader = new StateManager();
        this.customers = loader.loadCustomers();
        this.allContracts = loader.loadRentals(this.carInventory, this.customers);
        this.activeContracts = loader.scanActives(this.allContracts);
        this.lateRentals = loader.scanLates(this.allContracts);
        loader = null;
    }

    // quit the rental system, saving inventory changes
    public void quit(){
        this.carInventory.saveInventory();
        StateManager saver = new StateManager();
        try{
            saver.saveCustomers(customers);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        try{
            saver.saveRentals(allContracts);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /*
     * Calculate price of a rental for a given duration
     * Rental period is per day, not hourly
     * */
    public double calculateRentalPrice(Vehicle rental, int rentalPeriod){
        return rental.getRate() * rentalPeriod;
    }

    public void viewInventory(int key){
        System.out.println(carInventory.getVehicle(key));

    }

    public int getVehicleCount(){
        return carInventory.getSize();
    }

    public Vehicle getVehicle(int id) {
        return carInventory.getVehicle(id);
    }

    /**
     * Make a new contract
     * @param rental - the vehicle being rented
     * @param renter - the customer renting the vehicle
     */
    public RentalContract rentVehicle(Vehicle rental, Customer renter, int duration, LocalDate date){
        if(rental == null || renter == null){
            System.out.println("Invalid rental parameters");
            return null;
        }
        if (duration <= 0) {
            System.out.println("Invalid duration");
            return null;
        }
        if (date.isBefore(LocalDate.now())) {
            System.out.println("Cannot rent in the past");
            return null;
        }
        RentalContract newContract;
        int nextID = allContracts.size() + 1;
        /**
         *         Rentals a week or longer have an increased cleaning fee than default due to potential greater risk
         *
         */
         if (duration >= 7){
            if(date.isEqual(LocalDate.now())){
                newContract = new RentalContract(nextID, rental, duration, date, ContractStatus.ACTIVE, 50,  renter);
            }
            else{
                newContract = new RentalContract(nextID, rental, duration, date, ContractStatus.UPCOMING, 50, renter);
            }
        }
        else{
            if(date.isEqual(LocalDate.now())){
                newContract = new RentalContract(nextID, rental, duration, date, ContractStatus.ACTIVE, renter);
            }
            else {
                newContract = new RentalContract(nextID, rental, duration, date, ContractStatus.UPCOMING, renter);
            }
        }
        if(addActive(newContract)){
            allContracts.add(newContract);
            System.out.println("Inital rental quote: $" + calculateRentalPrice(rental, duration));
            return newContract;
        }
        else{
            System.out.println("Overlaps with another reservation");
            return null;
        }
    }

    protected boolean addActive(RentalContract newContract){
        List<RentalContract> schedule = this.activeContracts.computeIfAbsent(
                newContract.getVehicleID(),
                vehicleList -> new ArrayList<>()
        );
        for (RentalContract contract: schedule){
            contract.updateStatus();
            if(newContract.getStartDate().isBefore(contract.getEndDate()) &&
                    newContract.getEndDate().isAfter(contract.getStartDate())){
                return false;
            }
        }
        schedule.add(newContract);
        schedule.sort((contractOne, contractTwo) ->
                contractOne.getStartDate().compareTo(contractTwo.getStartDate()));
        return true;
    }

    public boolean cancel(Customer canceler){
        this.updateContracts();
        if(customers.get(canceler.getID()) == null){
            System.out.println("Customer does not exist");
            return false;
        }
        VehicleReturnManager returnWorker = new VehicleReturnManager(canceler);
        if (!returnWorker.parseCancel(this.allContracts, this.activeContracts)){
            System.out.println("No upcoming rental!");
            return false;
        }
        this.activeContracts.get(returnWorker.getVehicle()).remove(returnWorker.activeIdx);
        this.allContracts.get(returnWorker.ledgerIdx).cancelCar();
        System.out.println("** YOU HAVE SUCCESFULLY CANCELED A RESERVATION");
        System.out.println(this.allContracts.get(returnWorker.ledgerIdx));
        return true;
    }

    public void updateContracts(){
        this.activeContracts.values().forEach(rentalList -> {
            for (int i = 0; i < rentalList.size(); i++){
                rentalList.get(i).updateStatus();
                if(rentalList.get(i).getStatus() == ContractStatus.LATE && !lateRentals.contains(rentalList.get(i))){
                    lateRentals.add(rentalList.get(i));
                    if (i + 1 < rentalList.size()){
                        if(rentalList.get(i+1).getStartDate()
                                .isEqual(LocalDate.now()))
                        rentalList.get(i+1).delay();
                    }
                }
                else if(lateRentals.contains(rentalList.get(i))){
                    if (i + 1 < rentalList.size()){
                        if(rentalList.get(i+1).getStartDate()
                                .isEqual(LocalDate.now()))
                            rentalList.get(i+1).delay();
                    }
                }
            }
        });

        for(RentalContract contract : lateRentals){
            if(contract.getStatus() == ContractStatus.RETURNED){
                lateRentals.remove(contract);
            }
        }
    }

    /**
     * Return the vehicle
     */
    public boolean returnVehicle(Customer returner){
        this.updateContracts();
        if(customers.get(returner.getID()) == null){
            System.out.println("Customer does not exist");
            return false;
        }
        VehicleReturnManager returnWorker = new VehicleReturnManager(returner);
        returnWorker.parseAll(this.allContracts, this.activeContracts, this.lateRentals);
        if (returnWorker.getLocation() == ContractStatus.LATE){
            RentalContract contract = lateRentals.remove(returnWorker.activeIdx);
            contract.returnCar();
            this.allContracts.set(returnWorker.ledgerIdx, contract);
            this.activeContracts.get(returnWorker.getVehicle()).removeFirst();
            System.out.println("Sorry about this, it seems you're return was late." +
                    " Please pay the late fee at your convenience\n\t" +
                    "Accrued late fees: " + contract.calculateLateFee());
            return true;
        }
        else if (returnWorker.getLocation() == ContractStatus.ACTIVE){
            RentalContract contract = activeContracts.get(returnWorker.getVehicle()).removeFirst();
            contract.returnCar();
            if(Math.random() > 0.13){
                System.out.println("Hooray! You returned on time! And its clean too, so you saved yourself the cleaning fee of $"
                        + contract.getCleaningFees() +"!" +
                        " Hope you enjoyed the rental!");
                contract.waiveClean();
                this.allContracts.set(returnWorker.ledgerIdx , contract);
                return true;
            }
            else{
                System.out.println("Eh, we've seen cleaner. Cleaning fee applies: $" + contract.getCleaningFees());
                return true;
            }

        }
        else{
            System.out.println("Unable to return car. Vehicle is not currently being rented by this customer");
            return false;
        }
    }

    public Map<Integer, Customer> getCustomers(){
        return this.customers;
    }

    public Inventory getCarInventory(){
        return this.carInventory;
    }

    public int getNextCustomerID(){
        return customers.size() + 1;
    }

    public Customer login(String first, String last) {
        String name = last + ", " + first + ", ";
        for (Customer customer: customers.values()){
            if(customer.getName().toLowerCase().equals(name.toLowerCase())){
                return customer;
            }
        }
        return null;
    }
    public void addCustomer(Customer newCustomer){
        customers.put(newCustomer.getID(), newCustomer);
    }

    public boolean viewUpcoming(Customer renter) {
        boolean found = false;
        for (RentalContract contract : allContracts) {
            if (contract.getRentalCustomer().equals(renter) && contract.getStatus() == ContractStatus.UPCOMING) {
                System.out.print("\t\t");
                System.out.println(contract);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No upcoming rentals found for " + renter.getName());
        }
        return found;
    }

    protected boolean validateModel(String model){
        switch (model)
        {
            case "SEDAN":
                return true;
            case "SUV":
                return true;
            case "VAN":
                return true;
            default:
                return false;
        }
    }

    public boolean addVehicle(String model) {
        if (!validateModel(model)) {
            System.out.println("ERROR: INVALID TYPE");
            return false;
        }
        CarMake ranMake = CarMake.values()[(int) (Math.random() * CarMake.values().length)];
        CarColor ranColor = CarColor.values()[(int) (Math.random() * CarColor.values().length)];
        double ranRate = Math.floor(Math.random() * 100.00);
        int id = carInventory.getNextID();
        Vehicle tmp;
        switch (model) {
            case "SEDAN":
                tmp = new Sedan(id, ranMake, ranColor, ranRate);
                carInventory.addVehicle(id, tmp);
                break;
            case "SUV":
                tmp = new SUV(id, ranMake, ranColor, ranRate);
                carInventory.addVehicle(id, tmp);
                break;
            case "VAN":
                tmp = new Van(id, ranMake, ranColor, ranRate);
                carInventory.addVehicle(id, tmp);
                break;
        }
        System.out.println("Vehicle added!!!");
        return true;
    }


    /**
     * Purely a harness to control test environments
     */
    public void testHarnessSetup(List<Vehicle> testVehicles){
        this.activeContracts = new HashMap<>();
        this.allContracts = new ArrayList<>();
        this.lateRentals = new ArrayList<>();
        this.customers = new HashMap<>();
        this.carInventory.testHarness();
        for(Vehicle vehicle : testVehicles){
            this.carInventory.addVehicle(vehicle.getID(), vehicle);
        }

    }

    public void testHarnessRental(RentalContract rent){
        allContracts.add(rent);
        ArrayList<RentalContract> list = new ArrayList<>();
        list.add(rent);
        activeContracts.put(rent.getVehicleID(), list);
    }

    public boolean returnHarness(int testId){
        return this.activeContracts.get(testId) == null || this.activeContracts.get(testId).isEmpty();
    }

    public ArrayList<RentalContract> getAllContracts() {
        return allContracts;
    }

    public ArrayList<RentalContract> getLateRentals() {
        return lateRentals;
    }

    public Map<Integer, List<RentalContract>> getActiveContracts() {
        return activeContracts;
    }

}
