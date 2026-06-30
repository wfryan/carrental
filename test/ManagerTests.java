import Inventory.Inventory;
import manager.RentalManager;
import manager.StateManager;
import manager.VehicleReturnManager;
import models.Customer;
import models.RentalContract;
import models.Vehicle;
import traits.ContractStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This suite of tests is for the Manager specific functions. So the return manager, the state manager, and the rental manager
 * Basically all the helpers made.
 *
 * Actual testing of the core system is in RentalTests
 *
 */
public class ManagerTests {


    String contractTests;
    String customerTests;

    RentalManager rental;
    StateManager states;


    @BeforeEach
    void setUp(){
        rental = new RentalManager();
        contractTests = System.getProperty("user.dir") + "/data/tests/manager_contracts.csv";
        customerTests = System.getProperty("user.dir") + "/data/tests/manager_customers.csv";
        states = new StateManager(contractTests, customerTests);
    }

    /**
     * StateManager testing
     */
    @Test
    void testContractParsing(){
        Inventory carInventory = rental.getCarInventory();
        Map<Integer, Customer> customers = rental.getCustomers();
        assertNotNull(states.loadRentals(carInventory, customers));

    }


    @Test
    void testCustomerParsing(){
        Map<Integer, Customer> customers = states.loadCustomers();
        assertNotNull(customers);
        assertEquals(10, customers.size());
        assertTrue(customers.containsKey(1));
        assertEquals("Smith", customers.get(1).getLastName());
    }

    /**
     * Test with a contract becoming late
     * Test with a new contract
     */
    @Test
    void testContractSaving() throws IOException {
        ArrayList<RentalContract> contracts = states.loadRentals(rental.getCarInventory(), rental.getCustomers());
        assertTrue(states.saveRentals(contracts));
        contracts.add(new RentalContract(contracts.size() + 1, rental.getCarInventory().getVehicle(7), 4, LocalDate.now(), ContractStatus.ACTIVE, rental.getCustomers().get(8)));
        assertTrue(states.saveRentals(contracts));
    }

    /**
     * Test with a new Customer being added
     */
    @Test
    void testCustomerSaving() throws IOException {
        Map<Integer, Customer> customers = states.loadCustomers();
        int initialSize = customers.size();

        Customer newCustomer = new Customer("Test", "User", 999, 30);
        customers.put(newCustomer.getID(), newCustomer);
        states.saveCustomers(customers);

        Map<Integer, Customer> reloaded = states.loadCustomers();
        assertEquals(initialSize + 1, reloaded.size());
        assertTrue(reloaded.containsKey(999));

        // Cleanup
        customers.remove(999);
        states.saveCustomers(customers);
    }

    @Test
    void testUpcoming(){
        rental.start();
        Customer tmp = rental.getCustomers().get(1);
        assertTrue(rental.viewUpcoming(tmp));
        tmp = rental.getCustomers().get(2);
        assertFalse(rental.viewUpcoming(tmp));
    }

    /**
     * Test the return manager parsing
     */
    @Test
    void testVehicleReturnManager(){
        rental.start();
        Customer tmp = rental.getCustomers().get(2);
        VehicleReturnManager returnManager = new VehicleReturnManager(tmp);
        returnManager.parseAll(rental.getAllContracts(), rental.getActiveContracts(), rental.getLateRentals());
        assertNotNull(returnManager.getReturner());
        System.out.println(returnManager.getTarget());
        assertNotNull(returnManager.getTarget());
    }

    @Test
    void testVehicleCancelParse(){
        rental.start();
        Customer tmp = rental.getCustomers().get(3);
        VehicleReturnManager returnManager = new VehicleReturnManager(tmp);
        returnManager.parseCancel(rental.getAllContracts(), rental.getActiveContracts());
        assertNotNull(returnManager.getReturner());
        System.out.println(returnManager.getTarget());
        assertNotNull(returnManager.getTarget());
    }

    @Test
    void makeNewVehicles(){
        rental.start();
        assertTrue(rental.addVehicle("SEDAN"));
        assertTrue(rental.addVehicle("VAN"));
        assertTrue(rental.addVehicle("SUV"));
        assertFalse(rental.addVehicle("TRUCk"));
    }



}
