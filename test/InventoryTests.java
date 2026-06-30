import Inventory.Inventory;
import models.Sedan;
import org.junit.jupiter.api.Test;
import traits.CarColor;
import traits.CarMake;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testing things related to the inventory
 */
public class InventoryTests {

    /**
     * Tests loading the stock inventory
     */
    @Test
    void loadInventory(){
        int StockInventorySize = 15;
        try{
            Inventory testStock = new Inventory();
            assertEquals(StockInventorySize, testStock.getSize());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            fail();
        }

    }

    /**
     * Tests loading the inventory from a custom path
     */
    @Test
    void loadCustomInventory(){
        String customPath = System.getProperty("user.dir") +"/data/tests/custom/custom_inventory.csv";
        int customInventorySize = 30;
        try{
            Inventory testCustom = new Inventory(customPath);
            assertEquals(customInventorySize, testCustom.getSize());
        } catch (RuntimeException e){
            System.out.println("Reading failed: exception message\n\n" + e.getMessage());
            fail();
        }
    }

    /**
     * Test saving the stock inventory
     * Test saving a custom inventory
     * Tests change, save, reload
     */
    @Test
    void testInventorySave(){
        String customPath = System.getProperty("user.dir") +"/data/tests/custom/custom_inventory.csv";
        Inventory testStock = new Inventory();
        Inventory testCustom = new Inventory(customPath);
        assertTrue(testStock.saveInventory());
        assertTrue(testCustom.saveInventory());
        Sedan test = new Sedan(99, CarMake.VOLKSWAGEN, CarColor.BLUE, 750);
        testCustom = new Inventory(customPath);
        testCustom.addVehicle(99, test);
        testCustom.saveInventory();
        Inventory tmp = new Inventory(customPath);
        assertEquals(tmp.getInventory(), testCustom.getInventory());

        testCustom.removeVehicle(test.getID());
        testCustom.saveInventory();

    }
}
