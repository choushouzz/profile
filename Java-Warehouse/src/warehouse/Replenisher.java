package warehouse;

/**
 * The Class Replenisher.
 */
public class Replenisher extends Worker {

  /** The warehouse. */
  private Warehouse warehouse = null;

  /**
   * Gets the wh.
   *
   * @return the wh
   */
  public Warehouse getWh() {
    return this.warehouse;
  }

  /**
   * Sets the warehouse.
   *
   * @param wh
   *          the new warehouse
   */
  public void setWh(Warehouse wh) {
    this.warehouse = wh;
  }

  /**
   * Instantiates a new replenisher.
   *
   * @param name
   *          the name
   * @param title
   *          the title
   */
  public Replenisher(String name, String title) {
    super(name, title);
  }

  /**
   * Replenish the pick face.
   *
   * @param zone
   *          the zone
   * @param ai
   *          the aisle
   * @param ra
   *          the rack
   * @param lvl
   *          the level
   */
  public void replenish(String zone, int ai, int ra, int lvl) {
    String location = zone + '_' + ai + '_' + ra + '_' + lvl;
    PickFace pf = this.warehouse.getPickFace(location);
    pf.addNum(Config.getReplenishNum());
    System.out.println("<<<Replenished zone " + zone + ", aisle " + ai 
        + ", rack " + ra + ", level" + lvl + ".");
  }
}
