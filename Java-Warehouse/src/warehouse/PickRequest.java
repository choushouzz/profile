package warehouse;

import java.util.ArrayList;

/**
 * The Class PickRequest.
 */
public class PickRequest {

  /** The id. */
  private int id = 0;

  /** The count. */
  private static int count = 1;

  /** The order pack. */
  private ArrayList<Order> orderPack = null;

  /**
   * Instantiates a new pick request.
   */

  public PickRequest() {
    id = count;
    count++;
  }

  /**
   * Gets the front SKU list.
   *
   * @return the front SKU list
   */
  public ArrayList<String> getfrontSkuList() {
    ArrayList<String> lst = new ArrayList<String>();
    if (null != this.getOrderPack()) {
      for (Order o : this.getOrderPack()) {
        lst.add(o.getFrontSku());
      }
    }
    return lst;
  }

  /**
   * Gets the rearSKU list.
   *
   * @return the rear SKU list
   */

  public ArrayList<String> getrearSkuList() {
    ArrayList<String> lst = new ArrayList<String>();
    if (null != this.getOrderPack()) {
      for (Order o : this.getOrderPack()) {

        lst.add(o.getRearSku());
      }
    }
    return lst;
  }

  /**
   * Gets the SKU list.
   *
   * @return the SKU list
   */

  public ArrayList<String> getSkuList() {
    ArrayList<String> lst = new ArrayList<String>();
    if (null != this.getOrderPack()) {
      for (Order o : this.getOrderPack()) {
        lst.add(o.getFrontSku());
        lst.add(o.getRearSku());
      }
    }
    return lst;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the order pack.
   *
   * @return the order pack
   */
  public ArrayList<Order> getOrderPack() {
    return orderPack;
  }

  /**
   * Sets the order pack.
   *
   * @param orderpack
   *          the new order pack
   */
  public void setOrderPack(ArrayList<Order> orderpack) {
    this.orderPack = orderpack;
  }

  /**
   * Convert pick request to string.
   */
  public String toString() {
    return "Picking mission " + this.getId() + " is available.";
  }
}
