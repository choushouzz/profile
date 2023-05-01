package warehouse;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * The Class Loader.
 */
public class Loader extends Worker {
  /** logger. */
  static Logger logger = Logger.getLogger(Loader.class.getName());

  /** The number to count. */
  private static int count = 1;

  /**
   * Instantiates a new loader.
   *
   * @param name
   *          the name
   * @param title
   *          the title
   */
  public Loader(String name, String title) {
    super(name, title);
  }

  /** The next request id. */
  private int nextReqId = 0;

  /**
   * Gets the next request id.
   *
   * @return the next request id
   */
  public int getNextReqId() {
    return nextReqId;
  }

  /**
   * Ready.
   */
  public void ready() {
    nextReqId = count;
    System.out.println("Loader " + this.getName()  
        + "'s picking request is accpeted, now on mission " + getNextReqId());
    count++;
  }

  /**
   * Load.
   */
  public void load() {
    logger.info("loader rescan");
    if (ControlSystem.getWarehouse().getLoadList().size() > 0) {
      int id = ControlSystem.getWarehouse().getLoadList().firstKey();
      // System.out.println(id + " " + nextReqId);
      if (id == this.nextReqId) {
        // load
        Pallet li = ControlSystem.getWarehouse().getLoadList().get(id);
        System.out.println("Loading pickRequst " + li.getPickRequest().getId());
        for (Order o : li.getPickRequest().getOrderPack()) {
          System.out.println(
              "Loading Order " + o.getColour() + ","  
              + o.getModel() + "," + o.getFrontSku() + "," + o.getRearSku());
        }
        this.nextReqId++;
        ControlSystem.getWarehouse().getDoneList().add(li);
        ControlSystem.getWarehouse().getLoadList().remove(id);
        ControlSystem.getWarehouse().getTruck().load(li);
        if (!ControlSystem.getWarehouse().getTruck().getStatus()) {
          ControlSystem.getWarehouse().getTruckList().add(ControlSystem.getWarehouse().getTruck());
          ControlSystem.getWarehouse().getNewTruck();
        }
      }
    }
  }

  /**
   * Rescan the items.
   */
  public void rescan() {
    ArrayList<Pallet> tmpList = new ArrayList<Pallet>(ControlSystem.getWarehouse().getDoneList());
    while (!tmpList.isEmpty()) {
      Pallet li = tmpList.get(0);
      if (Sequencer.check(li)) {
        tmpList.remove(0);
        // ok, do nothing
      } else {
        // something wrong, retrieve the request
        System.out.println("Loader rescan wrong");
        break;
      }

    }
  }

}
