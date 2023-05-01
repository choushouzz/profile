package warehouse;

// TODO: Auto-generated Javadoc
/**
 * The Class PickFace.
 */

public class PickFace {

  /** The zone. */
  private String zone = null;

  /** The aisle. */
  private int aisle = 0;

  /** The rack. */
  private int rack = 0;

  /** The level. */
  private int level = 0;

  /** The SKU. */
  private String sku = null;

  /** The id. */
  private String location = null;

  /** The number . */
  private int num = 30;
  /*
   * number of fascia
   * 
   * 
   * /** Gets the id.
   *
   * @param zone the zone
   * 
   * @param ai the aisle
   * 
   * @param ra the rack
   * 
   * @param lvl the level
   * 
   * @return the string
   */

  /**
   * Instantiates a new pick face.
   *
   * @param zone
   *          the zone
   * @param aisle
   *          the aisle
   * @param rack
   *          the rack
   * @param level
   *          the level
   * @param sku
   *          the SKU
   */
  public PickFace(String zone, int aisle, int rack, int level, String sku) {
    this.zone = zone;
    this.aisle = aisle;
    this.rack = rack;
    this.level = level;
    this.sku = sku;

    this.location = this.zone + '_' + this.aisle + '_' + this.rack + '_' + this.level;
  }

  /**
   * Sets the number.
   *
   * @param num
   *          the new number
   */
  public void setNum(int num) {
    this.num = num;

  }

  /**
   * Adds the number.
   *
   * @param num
   *          the number to add
   */
  public void addNum(int num) {
    this.num += num;
  }

  /**
   * Gets the location.
   */
  public String getLocation() {
    return this.location;
  }

  /**
   * Pick.
   *
   * @param sku
   *          the SKU number
   */
  public void pick(String sku) {
    if (this.sku == sku) {
      this.num -= 1;
    }
    if (num <= 5) {
      System.out.println("Replenishing for " + this.zone + "_" + this.aisle 
          + "_" + this.rack + "_" + this.level);
    }
  }

  /**
   * Gets the SKU.
   *
   * @return the front SKU
   */
  public String getsku() {
    return this.sku;
  }

  /**
   * Gets the zone.
   *
   * @return the zone
   */
  public String getZone() {
    return this.zone;
  }

  /**
   * Gets the aisle.
   *
   * @return the aisle
   */
  public int getAisle() {
    return this.aisle;
  }

  /**
   * Gets the rack.
   *
   * @return the rack
   */
  public int getRack() {
    return this.rack;
  }

  /**
   * Gets the level.
   *
   * @return the level
   */
  public int getLevel() {
    return this.level;
  }

  /**
   * Gets the number.
   *
   * @return the number.
   */
  public int getNum() {
    return this.num;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return this.location;
  }

  /**
   * Get the information of pickface.
   * 
   * @return the String pickface information
   */
  public String toString() {
    return this.zone + "," + this.aisle + "," + this.rack + "," + this.level + ", " + this.num;

  }

}
