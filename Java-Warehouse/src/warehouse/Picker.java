package warehouse;

import java.util.ArrayList;

/**
 * The Class Picker.
 */
public class Picker extends Worker {

  /** The pick request. */
  private PickRequest pickrequest = null;

  /** The pick sequences. */
  private ArrayList<String> pickSeqs = null;

  /** The current fasicas. */
  private ArrayList<String> fasicas = new ArrayList<String>();

  /** The skus to be picked. */
  private ArrayList<String> allsku = null;

  public PickRequest getPickrequest() {
    return pickrequest;
  }

  public ArrayList<String> getPickSeqs() {
    return pickSeqs;
  }

  public ArrayList<String> getFasicas() {
    return fasicas;
  }

  /**
   * Instantiates a new picker.
   *
   * @param name
   *          the name
   * @param title
   *          the title
   */
  public Picker(String name, String title) {
    super(name, title);
  }

  /**
   * Instantiates a new picke in emergency situation when the sequencer detects.
   * mistakes
   *
   * @param pick
   *          the pickrequest
   */
  public Picker(PickRequest pick) {
    this.pickrequest = pick;
    ArrayList<String> skus = this.getPickrequest().getSkuList();
    pickSeqs = (ArrayList<String>) ControlSystem.getWarehouse().optimize(skus);
    this.allsku = skus;
  }

  /**
   * Ready.
   */
  public void ready() {
    this.getRequest();
  }

  /**
   * Gets the request.
   *
   */
  public void getRequest() {
    pickrequest = ControlSystem.getWarehouse().getPickRequestList().get(0);
    if (null != getPickrequest()) {
      ControlSystem.getWarehouse().getPickRequestList().remove(0);
      ArrayList<String> skus = this.getPickrequest().getSkuList();
      pickSeqs = (ArrayList<String>) ControlSystem.getWarehouse().optimize(skus);
      this.allsku = skus;

      System.out.println(
          "Picker " + this.getName() + "'s picking request is accpeted, now on mission " 
           + getPickrequest().getId());
    }
  }

  /**
   * Pick.
   *
   * @param num
   *          the number
   */
  public void pick(String num) {
    for (int i = 0; i < this.getPickSeqs().size(); i++) {
      if (this.getPickSeqs().get(i).contains(num)) {
        this.fasicas.add(num);
        String loc = this.getPickSeqs().get(i);
        String[] items = loc.split("_");
        String location = items[0] + '_' + items[1] + '_' + items[2] + '_' + items[3];

        this.getPickSeqs().remove(i);

        PickFace pickface = ControlSystem.getWarehouse().getPickFace(location);
        System.out.println("Picker " + this.getName() + " is moving to location " 
            + items[0] + "_" + items[1] + "_"
            + items[2] + "_" + items[3] + ".");
        ControlSystem.getWarehouse().pickfrom(location);
        System.out.println("Picked from " + pickface.getZone() + "_" + pickface.getAisle() 
            + "_" + pickface.getRack()
            + "_" + pickface.getLevel() + " , " + pickface.getNum() + " left.");
        if (pickface.getNum() <= Config.getReplenishLimit()) {
          ControlSystem.getWarehouse().doReplenisher(items);
        }
      } else if (i >= this.getPickSeqs().size() - 1 && (!allsku.contains(num))) {
        System.out.println("Scanned wrong sku" + " " + num);
        for (String sku : this.getPickrequest().getSkuList()) {
          if (!this.fasicas.contains(sku)) {
            System.out.println("Picker " + this.getName() + " now picks" + " " + sku);
            this.pick(sku);
            break;
          }
        }

      }
    }

  }

  /**
   * Marshaling.
   */
  public void marshaling() {
    ControlSystem.getWarehouse().addpicked(this.pickrequest.getId(), this.fasicas);
    pickrequest = null;
    System.out.println("Picker " + this.getName() + " is moving to marshalling area.");
    fasicas = new ArrayList<String>();
  }

}
