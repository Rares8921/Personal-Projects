package com.example.ihas;

import com.example.ihas.devices.SmartRefrigerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class SmartRefrigeratorTest {

    @Test
    public void testSetTemperatureValid() {
        SmartRefrigerator ref = new SmartRefrigerator("R001", "Kitchen Fridge");
        ref.setTemperature(5.0);
        Assertions.assertEquals(5.0, ref.getTemperature(), 0.01);
    }

    @Test
    public void testSetTemperatureInvalid() {
        SmartRefrigerator ref = new SmartRefrigerator("R001", "Kitchen Fridge");
        Assertions.assertThrows(IllegalArgumentException.class, () -> ref.setTemperature(-10.0));
    }

    @Test
    public void testToggleDoor() {
        SmartRefrigerator ref = new SmartRefrigerator("R001", "Kitchen Fridge");
        boolean initialDoorStatus = ref.isDoorOpen();
        ref.togglePower();
        Assertions.assertNotEquals(initialDoorStatus, ref.isDoorOpen());
    }

    @Test
    public void testAddAndRemoveItem() {
        SmartRefrigerator ref = new SmartRefrigerator("R001", "Kitchen Fridge");
        LocalDate expiry = LocalDate.now().plusDays(7);

        ref.addItem("Milk", expiry);

        boolean containsMilk = ref.getInventory().values().stream().anyMatch(list -> list.contains("Milk"));
        Assertions.assertTrue(containsMilk);

        ref.removeItem("Milk");
        boolean containsMilkAfterRemoval = ref.getInventory().values().stream().anyMatch(list -> list.contains("Milk"));
        Assertions.assertFalse(containsMilkAfterRemoval);
    }


    @Test
    public void testCheckExpiredItems() {
        SmartRefrigerator ref = new SmartRefrigerator("R001", "Kitchen Fridge");
        LocalDate pastDate = LocalDate.now().minusDays(1);
        ref.addItem("ExpiredItem", pastDate);
        String result = ref.checkExpiredItems();
        Assertions.assertTrue(result.contains("ExpiredItem"));
    }
}
