package UnitTests;

import warehouse.Worker;
import static org.junit.Assert.*;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


public class WorkerTest {

  @Test
  public void testWorkerTwoParameterGetName() {
    Worker Alice = new Worker("Alice", "Picker");
    assertEquals(Alice.getName(), "Alice");
  }

  @Test
  public void testWorkerTwoParameterGetTitle() {
    Worker Alice = new Worker("Alice", "Picker");
    assertEquals(Alice.getTitle(), "Picker");
  }

  @Test
  public void testLoaderTwoParameterGetTitle() {
    Worker Bill = new Worker("Bill", "Loader");
    assertEquals(Bill.getTitle(), "Loader");
  }

  @Test
  public void testWokerNoParameterGetName() {
    Worker Alice = new Worker();
    assertEquals(Alice.getName(), null);
  }

  @Test
  public void testWokerNoParameterGetTitle() {
    Worker Alice = new Worker();
    assertEquals(Alice.getTitle(), null);
  }

}
