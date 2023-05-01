package warehouse;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Class Truck.
 */
public class Truck {

  /** The status. */
  private boolean status = true;

  /** The storage. */
  private HashMap<Integer, ArrayList<Pallet>> storage = new HashMap<Integer, ArrayList<Pallet>>();

  /** The current level. */
  private int level = 0;

  /**
   * Instantiates a new truck.
   */
  public Truck() {
    for (int i = 0; i <= 9; i++) {
      ArrayList<Pallet> pallets = new ArrayList<Pallet>();
      storage.put(i, pallets);
    }

  }

  /**
   * Load a pallet.
   * 
   * @param pallet
   *          the pallets to load.
   */
  public void load(Pallet pallet) {
    if (status) {
      if (this.storage.get(level).size() < 2) {
        this.storage.get(level).add(pallet);

      } else {
        level += 1;
        this.storage.get(level).add(pallet);

      }
      if (this.level == 9 & this.storage.get(level).size() == 2) {
        this.status = false;
      }

    }

  }

  /**
   * Gets the status of the Truck.
   *
   * @return the status
   */
  public boolean getStatus() {
    return this.status;
  }

  /**
   * Gets the storage.
   *
   * @return the storage
   */
  public HashMap<Integer, ArrayList<Pallet>> getStorage() {
    return this.storage;
  }

}
