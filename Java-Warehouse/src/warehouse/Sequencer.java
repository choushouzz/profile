package warehouse;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.logging.Logger;

/**
 * The Class Sequencer.
 */
public class Sequencer extends Worker {
  /** logger. */
  private static Logger logger = Logger.getLogger(Sequencer.class.getName());

  /** The pick request. */
  private PickRequest sequencerequest = null;

  /** The pallet. */
  private Pallet pallet = new Pallet();

  private HashMap<Integer, Pallet> donelist = new HashMap<>();

  private static int rescanindex = 0;

  /**
   * Instantiates a new sequencer.
   *
   * @param name
   *          the name
   * @param title
   *          the title
   */
  public Sequencer(String name, String title) {
    super(name, title);
  }

  /**
   * Gets the request.
   *
   */

  public void getRequest() {
    this.sequencerequest = ControlSystem.getWarehouse().getSequenceList().get(0);
    if (null != sequencerequest) {
      if (ControlSystem.getWarehouse().getpicked().get(this.sequencerequest.getId()).size() == 8) {

        ControlSystem.getWarehouse().getSequenceList().remove(0);
        System.out.println("Sequencer " + this.getName() 
            + "'s sequence request is accpeted, now on mission "
            + sequencerequest.getId());
      } else {
        Picker pk = new Picker(this.sequencerequest);
        System.out.println("Not enough fasicas. Repick");
        for (String sku : this.sequencerequest.getSkuList()) {
          pk.pick(sku);
        }
        pk.marshaling();
        if (ControlSystem.getWarehouse().getpicked()
            .get(this.sequencerequest.getId()).size() == 8) {
          ControlSystem.getWarehouse().getSequenceList().remove(0);
        }
      }

    }
  }

  /**
   * Sequence.
   */
  public void sequence(String sku) {
    pallet.setPickRequest(this.sequencerequest);
    if (pallet.getPickRequest().getfrontSkuList().contains(sku)) {
      pallet.addFrontSkus(sku);
      System.out.println("Sequenced " + this.pallet.getFrontSkus().size() + " front fasica");
    } else if (pallet.getPickRequest().getrearSkuList().contains(sku)) {
      pallet.addRearSkus(sku);
      System.out.println("Sequenced " + this.pallet.getRearSkus().size() + " " + "rear fasica");
    } else {
      System.out.println("SKU number not found");
    }
    if (check(this.pallet)) {
      System.out.println("Pallet full, ready to load.");
      this.donelist.put(pallet.getPickRequest().getId(), this.pallet);
      this.pallet = new Pallet();
    }
  }

  /**
   * Rescan the items in the pallets.
   */
  public void rescan() {
    logger.info("squencer rescaning");
    ArrayList<ArrayList<Order>> rescanlist = ControlSystem.getWarehouse().getOrderPackList();
    ArrayList<Order> orderpacktoscan = rescanlist.get(Sequencer.rescanindex);
    int pickrequestindex = rescanindex + 1;
    Sequencer.rescanindex++;
    Pallet pallettoscan = this.donelist.get(pickrequestindex);
    Integer index = 0;
    while (index < orderpacktoscan.size()) {
      Order order = orderpacktoscan.get(index);
      if (!pallettoscan.getFrontSkus().get(index).equals(order.getFrontSku())) {
        System.out.println("Rescan fail, wrong order.");
        this.donelist.remove(pickrequestindex);
        ControlSystem.getWarehouse().addWrongOrderPallets(pickrequestindex, pallettoscan);
        break;
      }
      if (!pallettoscan.getRearSkus().get(index).equals(order.getRearSku())) {
        System.out.println("Rescan fail, wrong order.");
        ControlSystem.getWarehouse().addWrongOrderPallets(pickrequestindex, pallettoscan);
        this.donelist.remove(pickrequestindex);
        break;
      }
      index++;
    }
    System.out.println("Rescan done, correct order.");
    ControlSystem.getWarehouse().loadListadd(pickrequestindex, pallettoscan);

  }

  /**
   * Check.
   *
   * @param li
   *          the loaded item
   * @return true, if successful
   */
  public static boolean check(Pallet li) {
    if ((li.getFrontSkus().size() != 4) || (li.getRearSkus().size() != 4)) {
      return false;
    } else {
      return true;
    }
  }

  public PickRequest getPickRequest() {
    return this.sequencerequest;
  }

  public HashMap<Integer, Pallet> getDoneList() {
    return this.donelist;
  }

  public void removeFromDoneList(Integer key) {
    this.donelist.remove(key);
  }

}
