package manager;

import Inventory.Inventory;
import models.Customer;
import models.RentalContract;
import traits.ContractStatus;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Temporary state loader that'll load and save the customer and rental states to disk
public class StateManager {
    private String customerPath;
    private String contractPath;


    public StateManager(){
        this.contractPath = System.getProperty("user.dir") + "/data/contracts.csv";
        this.customerPath = System.getProperty("user.dir") + "/data/customers.csv";
    }

    public StateManager(String contract, String customer){
        this.contractPath = contract;
        this.customerPath = customer;
    }

    private RentalContract contractFromString(String contract, Inventory vehicles, Map<Integer, Customer> customers){
        String[] traits = contract.split(",");
        if (traits.length != 7){
            return null;
        }
        else{
            int contractID = Integer.parseInt(traits[0].trim());
            Customer customer = customers.get(Integer.parseInt(traits[1].trim()));
            int currentCarId = Integer.parseInt(traits[2].trim());
            int duration = Integer.parseInt(traits[3].trim());
            LocalDate contractDate = LocalDate.parse(traits[4].trim());
            String status = traits[5].trim();
            int clean = Integer.parseInt(traits[6].trim());
            RentalContract rental = new RentalContract(contractID, vehicles.getVehicle(currentCarId), duration, contractDate, ContractStatus.valueOf(status), clean, customer);
            return rental;
        }

    }

    public Map<Integer, List<RentalContract>> scanActives(List<RentalContract> allContracts){
        Map<Integer, List<RentalContract>> actives = new HashMap<>();

        for (RentalContract contract : allContracts){
            if(contract.getStatus()==ContractStatus.ACTIVE || contract.getStatus() == ContractStatus.UPCOMING){
                actives.computeIfAbsent(contract.getVehicleID(), vehicleList -> new ArrayList<>()).add(contract);
            }
        }

        actives.values().forEach(list -> {
            list.sort((contractOne, contractTwo) ->
                    contractOne.getStartDate().compareTo(contractTwo.getStartDate()));
        });

        return actives;
    }

    public ArrayList<RentalContract> loadRentals(Inventory vehicles, Map<Integer, Customer> customers){
        ArrayList<RentalContract> actives= new ArrayList<>();
        try(Stream <String> lines = Files.lines(Paths.get(contractPath))){
            actives = lines.filter(line -> !line.trim().isEmpty())
                    .map(line -> this.contractFromString(line, vehicles, customers))
                    .filter(contract -> contract != null)
                    .peek(RentalContract::updateStatus)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return actives;

    }

    public ArrayList<RentalContract> scanLates(ArrayList<RentalContract> allContracts){
        ArrayList<RentalContract> lates = new ArrayList<>();
        for (RentalContract contract : allContracts){
            if (contract.getStatus() == ContractStatus.LATE){
                lates.add(contract);
            }
        }

        return lates;

    }
    public boolean saveRentals(ArrayList<RentalContract> contracts) throws IOException{
        try(BufferedWriter contractWriter = new BufferedWriter(new FileWriter(contractPath, false))){
            contractWriter.write("");
            for(RentalContract contract: contracts){
                contract.updateStatus();
                contractWriter.append(contract.exportStr());
            }
        } catch (IOException e) {
            System.out.println("Failed to save: " + e.getMessage());
            return false;
        }
        return true;
    }

    public void saveCustomers(Map<Integer, Customer> customers) throws IOException{
        try(BufferedWriter customerWriter = new BufferedWriter(new FileWriter(customerPath, false))){
            customerWriter.write("");
            for(Map.Entry<Integer, Customer> customer: customers.entrySet()){
                customerWriter.append(customer.getValue().exportString());
            }
        };

    }

    public Customer customerFromString(String customerStr){
        String[] traits = customerStr.split(",");
        if(traits.length != 4){
            return null;
        }
        else{
            int id = Integer.parseInt(traits[0].trim());
            String lastName = traits[1].trim();
            String firstName = traits[2].trim();
            int age = Integer.parseInt(traits[3].trim());
            return new Customer(firstName, lastName, id, age);
        }

    }
    public Map<Integer, Customer> loadCustomers(){
        HashMap<Integer, Customer> customers = new HashMap<>();
        try(Stream<String> lines = Files.lines(Paths.get(customerPath))){
            return lines.filter(line -> !line.trim().isEmpty())
                    .map(this::customerFromString)
                    .filter(customer -> customer != null)
                    .collect(Collectors.toMap(Customer::getID, customer -> customer, (existing, replacement) -> existing));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
