import manager.RentalManager;
import models.Customer;
import models.Vehicle;
import models.RentalContract;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Main {

private static RentalManager rentalManager;
private static Customer activeCustomer;
protected static void createAccount(int id){
    Scanner scan = new Scanner(System.in);
    System.out.println("First Name: ");
    String first = scan.nextLine();
    System.out.println("Last Name: ");
    String last = scan.nextLine();
    System.out.println("Age: ");
    int age = scan.nextInt();
    Customer customer = new Customer(first, last, id, age);
    activeCustomer = customer;
    rentalManager.addCustomer(customer);
}

protected static void login(){
    Scanner scan = new Scanner(System.in);
    System.out.println("First Name: ");
    String first = scan.nextLine();
    System.out.println("Last Name: ");
    String last = scan.nextLine();
    activeCustomer = rentalManager.login(first, last);

}

protected static void adminLogin(){
    Scanner scan = new Scanner(System.in);
    System.out.println("First Name: ");
    String first = scan.nextLine();
    System.out.println("Last Name: ");
    String last = scan.nextLine();

    if(first.equals("Admin") && last.equals("Admin")){
        String action = "";
        while (true){
            System.out.println("Add Vehcile: A\n Logout and Quit: Q");
            action = scan.nextLine();
            if(action.length() > 1){
                System.out.println("Single character commands please");
            } else{
                switch(action.toUpperCase()){
                    case "A":
                        String model = "";
                        System.out.println("** VEHICLE INVENTORY ADDITION ** RATE, MAKE, AND COLOR ARE RANDOM");
                        System.out.println("**Available Types**\n\tSEDAN\n\tSUV\n\tVAN");
                        System.out.println("Enter Vehicle Type: ");
                        model = scan.nextLine().toUpperCase();
                        rentalManager.addVehicle(model);
                        break;
                    case "Q":
                        rentalManager.quit();
                        return;
                }
            }
        }
    }

}

protected static void reserveVehicle(){
    Scanner scan = new Scanner(System.in);
    System.out.println("Enter Vehicle ID to reserve (int): ");

    int id = 0;
    id = scan.nextInt();

    Vehicle vehicle = rentalManager.getVehicle(id);
    if (vehicle == null) {
        System.out.println("Vehicle not found.");
        return;
    }

    System.out.println("Enter duration (days - int): ");
    int duration = 0;
    duration = scan.nextInt();

    System.out.println("Enter start date (YYYY-MM-DD) or 'today': ");
    String dateStr = scan.nextLine();
    LocalDate date;
    if (dateStr.equalsIgnoreCase("today")) {
        date = LocalDate.now();
    } else {
        try {
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }
    }

    RentalContract contract = rentalManager.rentVehicle(vehicle, activeCustomer, duration, date);
    if (contract != null) {
        System.out.println("Reservation successful!");
        System.out.println(contract);
    } else {
        System.out.println("Vehicle is not available for those dates.");
    }
}

protected static void viewInventory(){
    int numVehicles = rentalManager.getVehicleCount();
    Scanner scan = new Scanner(System.in);
    int pageNum = 1;
    while (true){
        System.out.println("PAGE: " + pageNum);
        rentalManager.viewInventory(pageNum);
        System.out.println("There are " + numVehicles + " vehicles to see, which page would you like to go to (-1 to exit): ");
        pageNum = scan.nextInt();
        if(pageNum < 0){
            return;
        }
    }

}

protected static void viewUpcoming(){
    rentalManager.viewUpcoming(activeCustomer);
}

protected static void returnVehicle(){
    rentalManager.returnVehicle(activeCustomer);
}

protected static void cancelReservation(){
    System.out.println("Attemping to cancel the upcoming reservation for: " + activeCustomer);
    rentalManager.cancel(activeCustomer);
}

public static void main(String[] args) {

    int argLength = args.length;
    System.out.println(argLength);
    if(argLength > 1){
        if (args[0].equals("--custom")){
            rentalManager = new RentalManager(args[1]);
        }
        else{
            System.out.println("Invalid commandline argument, example arg: --custom \"path/to/inventor\" ");
            return;
        }
    }
    else{
        rentalManager = new RentalManager();
    }

    boolean running = true;
    boolean loginMenu = true;
    Scanner scan = new Scanner(System.in);
    String action = "";
    System.out.println("Login: L \nCreate User: C\nAdmin Login: A\nQuit: Q");
    action = scan.nextLine();

    while (loginMenu){
        if (action.length() > 1){
            System.out.println("Invalid action, please use single characters");
        }
        else {
            switch (action.toUpperCase()){
                case "L":
                    login();
                    if(activeCustomer != null){
                        loginMenu = false;
                    }
                    break;
                case "C":
                    createAccount(rentalManager.getNextCustomerID());
                    loginMenu = false;
                    break;
                case "A":
                    adminLogin();
                    System.out.println("See you later!");
                    return;
                case "Q":
                    rentalManager.quit();
                    System.out.println("See you later!");
                    return;

            }
        }
    }
    while(running){
        System.out.println("\n\n\nActions:\n\tReserve a vehicle: R\n\tView Inventory: V\n\tView Upcoming: U\n\t" +
                "Return vehicle: F\n\tCancel Reservation: C\n\tLogout and Quit: Q");
        action = scan.nextLine();
        if (action.length() > 1){
            System.out.println("Invalid action, please use single characters");
        }
        else{
            switch (action.toUpperCase()){
                case "R":
                    reserveVehicle();
                    break;
                case "V":
                    viewInventory();
                    break;
                case "U":
                    viewUpcoming();
                    break;
                case "F":
                    returnVehicle();
                    break;
                case "C":
                    cancelReservation();
                    break;
                case "Q":
                    activeCustomer = null;
                    rentalManager.quit();
                    running = false;
                    break;

            }
        }
    }

    System.out.println("Thanks for using our Car Rental service! Have a nice day!");


}
}
