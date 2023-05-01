package warehouse;

import java.util.ArrayList;

/**
 * The Class Pallet. which is always a pair of pallets for the same order.
 */
public class Pallet {

  /** The picking request. */
  private PickRequest pickrequest = null;

  /** The front SKUs. */
  private ArrayList<String> frontSkus = new ArrayList<String>();

  /** The rear SKUs. */
  private ArrayList<String> rearSkus = new ArrayList<String>();

  public void addFrontSkus(String sku) {
    this.frontSkus.add(sku);
  }

  public void addRearSkus(String sku) {
    this.rearSkus.add(sku);
  }

  public PickRequest getPickRequest() {
    return this.pickrequest;
  }

  public void setPickRequest(PickRequest pr) {
    this.pickrequest = pr;
  }

  public ArrayList<String> getFrontSkus() {
    return this.frontSkus;
  }

  public ArrayList<String> getRearSkus() {
    return this.rearSkus;
  }

}
