import manager.RentalManager;
import models.Customer;
import models.RentalContract;
import models.Sedan;
import models.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import traits.CarColor;
import traits.CarMake;
import traits.ContractStatus;


import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ReturnTests {

    private RentalManager rentalManager;
    private Customer testCustomer;
    private Customer testCustomerTwo;
    private Vehicle testVehicle;
    private Vehicle testVehicleTwo;

    @BeforeEach
    void setUp(){
        rentalManager = new RentalManager();
        testCustomer = new Customer("John", "Doe", 7777, 25);
        testVehicle = new Sedan(777, CarMake.VOLKSWAGEN, CarColor.BLUE, 50.0);
        testCustomerTwo = new Customer("John", "Doe", 7778, 25);
        testVehicleTwo = new Sedan(778, CarMake.VOLKSWAGEN, CarColor.RED, 50.0);
        ArrayList<Vehicle> testCars = new ArrayList<>();
        testCars.add(testVehicle);
        testCars.add(testVehicleTwo);
        rentalManager.testHarnessSetup(testCars);
        rentalManager.addCustomer(testCustomer);
        rentalManager.addCustomer(testCustomerTwo);

    }
    /**
     *  Early
     */
    @Test
    void validEarlyReturns(){
        RentalContract rentalContract = new RentalContract(77777, testVehicle, 3,
                LocalDate.now().minusDays(1), ContractStatus.ACTIVE, testCustomer);
        rentalManager.testHarnessRental(rentalContract);

        boolean isEarly = rentalManager.returnVehicle(testCustomer);
        assertTrue(isEarly, "Early return successful");
        boolean returnWorked = rentalManager.returnHarness(testVehicle.getID());
        assertTrue(returnWorked, "Active contract removed properly");

    }

    /**
     *  On time
     */
    @Test
    void validOnTimeReturns(){
        RentalContract rentalContract = new RentalContract(77777, testVehicle, 3,
                LocalDate.now().minusDays(3), ContractStatus.ACTIVE, testCustomer);
        rentalManager.testHarnessRental(rentalContract);

        boolean isOnTime = rentalManager.returnVehicle(testCustomer);
        assertTrue(isOnTime, "Deadline return successful");
        boolean returnWorked = rentalManager.returnHarness(testVehicle.getID());
        assertTrue(returnWorked, "Active contract removed properly");

    }

    /**
     * Invalid Customer
     */
    @Test
    void invalidReturnsNoCustomer(){
        RentalContract rentalContract = new RentalContract(77777, testVehicle, 3,
                LocalDate.now().minusDays(3), ContractStatus.ACTIVE, testCustomer);
        rentalManager.testHarnessRental(rentalContract);

        boolean noCustomer = rentalManager.returnVehicle(new Customer("Test", "Test", 235, 25));
        assertFalse(noCustomer, "Customer does not exist in system");

    }

    /**
     * Wrong customer / customer has no rentals
     */
    @Test
    void invalidWrongCustomer(){
        RentalContract rentalContract = new RentalContract(77777, testVehicle, 3,
                LocalDate.now().minusDays(3), ContractStatus.ACTIVE, testCustomer);
        rentalManager.testHarnessRental(rentalContract);

        boolean noRentals = rentalManager.returnVehicle(testCustomerTwo);
        assertFalse(noRentals, "Customer has no rentals");
    }



    /**
     * Late fee calculation
     *
     * */
    @Test
    void lateReturns(){
        RentalContract rentalContract = new RentalContract(77777, testVehicle, 2,
                LocalDate.now().minusDays(3), ContractStatus.ACTIVE, testCustomer);
        rentalManager.testHarnessRental(rentalContract);

        boolean isLate = rentalManager.returnVehicle(testCustomer);
        assertTrue(isLate, "Late return successful");
        long calculatedLateFees = rentalContract.calculateLateFee();
        assertEquals(calculatedLateFees, (testVehicle.getLateRate() * 1));
    }
}
