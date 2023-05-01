package warehouse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * The Class ControlSystem.
 */
public class ControlSystem {

  /** The warehouse. */
  private static Warehouse warehouse = new Warehouse();

  /**
   * Instantiates a new control system.
   */
  public ControlSystem() {

  }

  /**
   * Initial.
   *
   * @param traversaltable
   *          the traversal_table.csv file
   * @param initial
   *          the initial.csv file
   * @param translation
   *          the translation.csv file
   */
  public void initial(String traversaltable, String initial, String translation) {
    warehouse.initPickFacesByTraversalTable(traversaltable);
    warehouse.initPickFacesByInitialFile(initial);
    Order.initLookupMaps(translation);
  }

  /**
   * Run.
   *
   * @param traversaltable
   *          the traversal_table.csv
   * @param initial
   *          the initial.csv
   * @param translation
   *          the translation.csv
   * @param events
   *          the event text file(16orders.txt as an example)
   */
  public void run(String traversaltable, String initial, String translation, String events) {
    warehouse.readEventFile(events);
    this.outputFinal();
    this.outputDoneOrders();
  }

  /**
   * Output final.
   */
  public void outputFinal() {
    try {
      File file = new File("final.csv");

      // if file doesn't exists, then create it
      if (!file.exists()) {
        file.createNewFile();
      }

      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      HashMap<String, PickFace> pickfaces = warehouse.getPickFaces();
      for (String key : pickfaces.keySet()) {
        if (pickfaces.get(key).getNum() < 30) {
          bw.write(pickfaces.get(key).toString());
          bw.newLine();
          // System.out.println(key + "!=" +
          // pickfaces.get(key).getNum() );
        }

        // if (pickfaces.get(key).getNum() < 30){
        // System.out.println(key + "< " + pickfaces.get(key).getNum()
        // );
        // }
      }

      System.out.println("final.csv Done");
      bw.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Output done orders.
   */
  public void outputDoneOrders() {
    try {
      File file = new File("orders.csv");
      // if file doesn't exists, then create it
      if (!file.exists()) {
        file.createNewFile();
      }

      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);

      for (Pallet li : warehouse.getDoneList()) {
        for (Order o : li.getPickRequest().getOrderPack()) {

          bw.write(o.getModel() + "," + o.getColour());
          bw.newLine();
        }
      }

      System.out.println("orders.csv Done");
      bw.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static Warehouse getWarehouse() {
    return ControlSystem.warehouse;
  }

  /**
   * Output final.
   */

  static Logger logger = Logger.getLogger(ControlSystem.class.getName());

  /**
   * The main method.
   *
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    logger.config("log.properties");
    ControlSystem control = new ControlSystem();
    logger.info("constructor");
    control.initial("traversal_table.csv", "initial.csv", "translation.csv");
    control.run("traversal_table.csv", "initial.csv", "translation.csv", "16orders.txt");

  }

}
