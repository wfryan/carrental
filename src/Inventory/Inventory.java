package Inventory;

import models.*;
import traits.CarColor;
import traits.CarMake;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Inventory {
    private Map<Integer, Vehicle> rentalInventory;
    private String saveLocation;

    public Inventory() throws RuntimeException{
        this.rentalInventory = new HashMap<>();
        try {
            this.loadInventory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Inventory(String filePath) throws RuntimeException{

        try{
            this.saveLocation = filePath;
            this.rentalInventory = new HashMap<>();
            this.loadInventory(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getSize(){
        return rentalInventory.size();
    }
    private void parseTraits(String[] traits){
        int id = Integer.parseInt(traits[0].trim());
        CarMake make = CarMake.valueOf(traits[1].trim());
        String model = traits[2].trim();
        CarColor color = CarColor.valueOf(traits[3].trim());
        double rate = Double.parseDouble(traits[4].trim());
        switch (model){
            case "SUV":
                if(traits.length == 7){
                    double towCapacity = Double.parseDouble(traits[5].trim());
                    boolean hasTrailerHitch = Boolean.getBoolean(traits[6].trim());
                    SUV current = new SUV(id, make, color, rate, towCapacity, hasTrailerHitch);
                    this.rentalInventory.put(id, current);
                }
                else if (traits.length == 5){
                    SUV current = new SUV(id, make, color, rate);
                    this.rentalInventory.put(id, current);
                }
                else{
                    System.out.println("Skipping line, corrupt entry: " + traits.toString());
                }
                break;
            case "VAN":
                if (traits.length == 6){
                    int cargoLimit = Integer.parseInt(traits[5].trim());
                    Van current = new Van(id, make, color, rate, cargoLimit);
                    this.rentalInventory.put(id, current);
                }
                else if (traits.length == 5){
                    Van current = new Van(id, make, color, rate);
                    this.rentalInventory.put(id, current);
                }
                else{
                    System.out.println("Skipping line, corrupt entry: " + traits.toString());
                }
                break;
            case "SEDAN":
                if (traits.length == 6){
                    int doors = Integer.parseInt(traits[5].trim());
                    Sedan current = new Sedan(id, make, color, rate, doors);
                    this.rentalInventory.put(id, current);
                }
                else if(traits.length == 5){
                    Sedan current = new Sedan(id, make, color, rate);
                    this.rentalInventory.put(id, current);
                }
                else{
                    System.out.println("Skipping line, corrupt entry: " + traits.toString());
                }
                break;

        }
    }

    public boolean loadInventory(String filePath) throws IOException {

        Path path = Paths.get(filePath);

        try (Stream<String> lines = Files.lines(path)){
            lines.filter(line -> !line.trim().isEmpty())
                    .forEach(line -> {
                        String[] tokens = line.split(",");
                        if (tokens.length < 5) {
                            System.out.println("Skipping line, corrupt entry: " + Arrays.toString(tokens));
                        } else {
                            this.parseTraits(tokens);
                        }
                    });
        } catch (IOException e){
            System.out.println("Failed to read custom inventory" + e.getMessage());
        }


        return true;
    }

    public boolean loadInventory() throws IOException {
        this.saveLocation = System.getProperty("user.dir") + "/data/stock_inventory.csv";
        try (Stream<String> lines = Files.lines(Paths.get(this.saveLocation))){
            lines.filter(line -> !line.trim().isEmpty())
                    .forEach(line -> {
                        String[] tokens = line.split(",");
                        if(tokens.length < 5){
                            System.out.println("Skipping line, corrupt entry: " + Arrays.toString(tokens));
                        }
                        else{
                            this.parseTraits(tokens);
                        }
                    });
        } catch (IOException e){
            System.out.println("Failed to load stock inventory" + e.getMessage());
        }

        return true;
    }


    /**
     * Saves the car inventory to disk
     * @return true if works, false if fails
     */
    public boolean saveInventory(){
        try(BufferedWriter inventoryWriter = new BufferedWriter(new FileWriter(this.saveLocation, false))){
            inventoryWriter.write("");

            for(Map.Entry<Integer, Vehicle> entry : this.rentalInventory.entrySet()){
                Vehicle vehicle = entry.getValue();
                if(vehicle != null){
                    inventoryWriter.write(vehicle.exportString());
                }
            }
        } catch (IOException e){
            System.out.println("Failed to save inventory: " + e.getMessage());
            return false;
        }

        return true;
    }

    public Vehicle getVehicle(int id){
        return this.rentalInventory.get(id);
    }

    public int getNextID(){
        return rentalInventory.size() + 1;
    }

    public void addVehicle(int ID, Vehicle newVehicle){
        this.rentalInventory.put(ID, newVehicle);
    }

    public void testHarness(){
        this.rentalInventory = new HashMap<>();
    }

    public void removeVehicle(int id){
        this.rentalInventory.remove(id);
    }

    public Map<Integer, Vehicle> getInventory() {
        return rentalInventory;
    }
}
