import Inventory.Inventory;
import manager.RentalManager;
import models.Customer;
import models.RentalContract;
import models.Vehicle;
import traits.ContractStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Core system tests.
 * Using the stock inventory setup
 * two customers
 * 4 rentals
 * Cancels
 * Scheduling system
 */
public class RentalTests {

    private RentalManager rentalManager;

    @BeforeEach
    void setUp() {
        rentalManager = new RentalManager();
    }

    /**
    *  Testing the base rental conditions
    * */
    @Test
    void validSingleRental(){
        Customer customer = rentalManager.getCustomers().get(1);
        Vehicle vehicle = rentalManager.getCarInventory().getVehicle(1);

        /**
         * Current Date
         */
        RentalContract current = rentalManager.rentVehicle(vehicle, customer, 3, LocalDate.now());
        assertNotNull(current);
        assertEquals(ContractStatus.ACTIVE, current.getStatus());

        /**
         * Future date
         */
        RentalContract future = rentalManager.rentVehicle(rentalManager.getCarInventory().getVehicle(2), customer, 3, LocalDate.now().plusDays(5));
        assertNotNull(future);
        assertEquals(ContractStatus.UPCOMING, future.getStatus());
    }

    /**
     * Test cases for invalid rentals
     * Car Stock, Past Date (cant time travel), unspecified duration
     * */
    @Test
    void invalidSingleRental(){
        Customer customer = rentalManager.getCustomers().get(1);
        Vehicle vehicle = rentalManager.getCarInventory().getVehicle(3);

        // Past Date
        RentalContract past = rentalManager.rentVehicle(vehicle, customer, 3, LocalDate.now().minusDays(1));
        assertNull(past);

        // unspecified duration (0 or less)
        RentalContract noDuration = rentalManager.rentVehicle(vehicle, customer, 0, LocalDate.now());
        assertNull(noDuration);

        RentalContract negativeDuration = rentalManager.rentVehicle(vehicle, customer, -1, LocalDate.now());
        assertNull(negativeDuration);

        // Car Stock - if vehicle is already rented for today
        rentalManager.rentVehicle(vehicle, customer, 3, LocalDate.now()); // Rent it first
        RentalContract doubleRent = rentalManager.rentVehicle(vehicle, rentalManager.getCustomers().get(2), 3, LocalDate.now());
        assertNull(doubleRent);
    }

    /**
     *  Multiple non-overlapping reservations in the future
     *  Tests the scheduling system.
     */
    @Test
    void oneVehcileMultipleReservations(){
        Customer customer1 = rentalManager.getCustomers().get(6);
        Customer customer2 = rentalManager.getCustomers().get(7);
        Vehicle vehicle = rentalManager.getCarInventory().getVehicle(9);

        RentalContract res1 = rentalManager.rentVehicle(vehicle, customer1, 2, LocalDate.now().plusDays(1)); // Day 1 to 3
        assertNotNull(res1);

        RentalContract res2 = rentalManager.rentVehicle(vehicle, customer2, 2, LocalDate.now().plusDays(4)); // Day 4 to 6
        assertNotNull(res2);

        RentalContract overlap = rentalManager.rentVehicle(vehicle, customer1, 2, LocalDate.now().plusDays(2)); // Day 2 to 4 - overlaps with res1
        assertNull(overlap);
    }

    /**
     *  Testing cancellation system
     *
     */
    @Test
    void rentThenCancel(){
        Customer customer1 = rentalManager.getCustomers().get(6);
        Vehicle vehicle = rentalManager.getCarInventory().getVehicle(9);

        RentalContract res1 = rentalManager.rentVehicle(vehicle, customer1, 2, LocalDate.now().plusDays(1)); // Day 1 to 3
        assertNotNull(res1);

        assertTrue(rentalManager.cancel(customer1));
    }

    @Test
    void invalidCancel(){
        Customer customer1 = rentalManager.getCustomers().get(6);
        Vehicle vehicle = rentalManager.getCarInventory().getVehicle(9);

        RentalContract res1 = rentalManager.rentVehicle(vehicle, customer1, 5, LocalDate.now()); // Day 1 to 3
        assertNotNull(res1);

        assertFalse(rentalManager.cancel(customer1));

    }

}
