package UnitTests;

import warehouse.Truck;
import warehouse.Pallet;
import static org.junit.Assert.*;
import java.util.HashMap;
import java.util.ArrayList;

import org.junit.Test;

public class TruckTest {

  @Test
  public void testTruckGetStatusTrue() {
    Truck truck = new Truck();
    assertTrue(truck.getStatus());
  }

  @Test
  public void testTruckLoadOneItem() {
    Pallet pallet = new Pallet();
    Truck truck = new Truck();
    truck.load(pallet);
    assertEquals(truck.getStorage().get(0).size(), 1);
    assertEquals(truck.getStorage().get(0).get(0), pallet);
  }


  @Test
  public void testTruckLoadMaxItems() {

    Truck truck = new Truck();
    int temp = 0;
    while (temp != 20) {
      Pallet pallet = new Pallet();
      truck.load(pallet);
      temp++;
    }
    assertFalse(truck.getStatus());
    assertEquals(truck.getStorage().size(), 10);
    int level = 0;
    while (level <= 9) {
      assertEquals(truck.getStorage().get(level).size(), 2);
      level ++;
    }
  }

  @Test
  public void testTruckNotFullyLoadedGetStatus() {
    Truck truck = new Truck();
    assertTrue(truck.getStatus());
  }

  @Test
  public void testTrcukFullyLoadedGetStatus() {
    Truck truck = new Truck();
    int temp = 0;
    while (temp != 20) {
      Pallet pallet = new Pallet();
      truck.load(pallet);
      temp++;
    }
    assertFalse(truck.getStatus());
  }
  
  @Test
  public void testNoItemGetStorage(){
    Truck truck = new Truck();
    int level = 0;
    while (level < truck.getStorage().size()) {
      assertEquals(truck.getStorage().get(level).size(), 0);
      level ++;
    }
  }
  @Test
  public void testNonEmptyItemGetStorage(){
    Truck truck = new Truck();
    Pallet pallet1 = new Pallet();
    Pallet pallet2 = new Pallet();
    Pallet pallet3 = new Pallet();
    truck.load(pallet1);
    truck.load(pallet2);
    truck.load(pallet3);
    HashMap<Integer, ArrayList<Pallet>> expectedstorage = new HashMap<>();
    ArrayList<Pallet> level1 = new ArrayList<>();
    ArrayList<Pallet> level2 = new ArrayList<>();
    level1.add(pallet1);
    level1.add(pallet2);
    level2.add(pallet3);
    expectedstorage.put(0, level1);
    expectedstorage.put(1, level2);
    int level = 2;
    while (level != 10){
      ArrayList<Pallet> al = new ArrayList<>();
      expectedstorage.put(level, al);
      level ++;
    }
    System.out.println(truck.getStorage());
    System.out.println(expectedstorage);
    assertEquals(truck.getStorage(), expectedstorage);
  }
}
