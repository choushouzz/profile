package warehouse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * The Class Warehouse.
 */
public class Warehouse {

  /** logger. */
  private Logger logger = Logger.getLogger(Warehouse.class.getName());

  /** The pick faces. */
  private HashMap<String, PickFace> pickfaces = new HashMap<String, PickFace>();

  /** The pick request list. */
  private ArrayList<PickRequest> pickrequests = new ArrayList<PickRequest>();

  private HashMap<String, PickFace> skuPickFace = new HashMap<String, PickFace>();

  /** The sequence list. */
  private ArrayList<PickRequest> sequenceList = new ArrayList<PickRequest>();

  /** The load list. */
  private TreeMap<Integer, Pallet> loadList = new TreeMap<Integer, Pallet>();

  /** The Done list. */
  private ArrayList<Pallet> doneList = new ArrayList<Pallet>();

  /** The worker map. */
  private HashMap<String, Worker> workerMap = new HashMap<String, Worker>();

  /** The picked skus. */
  private HashMap<Integer, ArrayList<String>> picked = new HashMap<Integer, ArrayList<String>>();

  /** The Truck list. */
  private ArrayList<Truck> truckList = new ArrayList<Truck>();

  /** The temporary truck. */
  private Truck truck = new Truck();

  private ArrayList<Order> orderPack = new ArrayList<>();

  private HashMap<Integer, Pallet> sequencerrescannedwrong = new HashMap<>();

  private ArrayList<ArrayList<Order>> orderPackList = new ArrayList<>();

  /**
   * Initial the pick faces.
   *
   * @param file
   *          the file
   */
  public void initPickFacesByTraversalTable(String file) {
    BufferedReader traversaltablefile = null;

    try {
      traversaltablefile = new BufferedReader(new FileReader(file));

      String line = traversaltablefile.readLine();
      while (line != null) {
        String[] items = line.split(",");
        PickFace pickface = new PickFace(items[0], Integer.parseInt(items[1]), 
            Integer.parseInt(items[2]),
            Integer.parseInt(items[3]), items[4]);
        String location = pickface.getLocation();

        // set initial number
        pickface.setNum(Config.getInitPickFaceNum());

        this.getPickFaces().put(location, pickface);
        this.skuPickFace.put(pickface.getsku(), pickface);
        line = traversaltablefile.readLine();

      }
      traversaltablefile.close();
      logger.info("Done initializing " + file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Based on the Integer SKUs in List 'skus', return a List of locations, where
   * each location is a String containing 5 pieces of information: the zone
   * character (in the range ['A'..'B']), the aisle number (an integer in the
   * range [0..1]), the rack number (an integer in the range ([0..2]), and the
   * level on the rack (an integer in the range [0..3]), and the SKU number.
   * 
   * @param skus
   *          the list of SKUs to retrieve.
   * @return the List of locations.
   */
  public List<String> optimize(List<String> skus) {

    class SkuOrder {
      public String sku;
      public String order;

      public SkuOrder(String sku, String order) {
        this.sku = sku;
        this.order = order;
      }
    }

    ArrayList<SkuOrder> lst = new ArrayList<SkuOrder>();
    for (Object sku : skus) {
      if (this.skuPickFace.containsKey(sku)) {
        lst.add(new SkuOrder((String) sku, skuPickFace.get(sku).getsku()));
      } else {
        System.out.println("Cannot find " + sku);
      }
    }

    Collections.sort(lst, new Comparator<Object>() {
      public int compare(Object obj1, Object obj2) {
        SkuOrder aa = (SkuOrder) obj1;
        SkuOrder bb = (SkuOrder) obj2;
        return aa.order.compareTo(bb.order);
      }
    });

    List<String> retList = new ArrayList<String>();
    for (SkuOrder so : lst) {
      if (this.skuPickFace.containsKey(so.sku)) {
        PickFace pf = this.skuPickFace.get(so.sku);
        String locInfo = pf.getId() + '_' + so.sku;
        retList.add(locInfo);
      }
    }
    return retList;

  }

  /**
   * Initial the pick faces.
   *
   * @param file
   *          the file
   */
  public void initPickFacesByInitialFile(String file) {
    BufferedReader initFile = null;
    try {
      initFile = new BufferedReader(new FileReader(file));
      String line = initFile.readLine();

      while (line != null) {
        String[] items = line.split(",");
        PickFace pickface = new PickFace(items[0], Integer.parseInt(items[1]), 
            Integer.parseInt(items[2]),Integer.parseInt(items[3]), line);
        String location = items[0] + '_' + items[1] + '_' + items[2] + '_' + items[3];
        pickface.setNum(Integer.parseInt(items[4]));
        this.pickfaces.put(location, pickface);
        line = initFile.readLine();
        initFile.close();
        logger.info("init " + file + " done");
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Gets the pick face.
   *
   * @param id
   *          the id
   * @return the pick face
   */
  public PickFace getPickFace(String id) {
    return this.getPickFaces().get(id);
  }

  /**
   * Read event file.
   *
   * @param fn
   *          the file
   */
  public void readEventFile(String fn) {
    BufferedReader evtFile = null;

    try {
      evtFile = new BufferedReader(new FileReader(fn));

      String line = null;
      while ((line = evtFile.readLine()) != null) {
        System.out.println("--->EVENT:" + line);
        String[] items = line.split(" ");
        if (items[0].equals("Order")) {
          doOrder(items);
        } else if (items[0].equals("Picker")) {
          this.doPicker(items);
        } else if (items[0].equals("Sequencer")) {
          this.doSequencer(items);
        } else if (items[0].equals("Loader")) {
          this.doLoader(items);
        } else if (items[0].equals("Replenisher")) {
          this.doReplenisher(items);
        } else {
          System.out.println("Unknown cmd");
        }

      }
      evtFile.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Do order.
   *
   * @param items
   *          the items
   */
  public void doOrder(String[] items) {
    Order order = new Order(items[1], items[2]);
    if (this.orderPack.size() < 3) {
      this.orderPack.add(order);
    } else if (this.orderPack.size() == 3) {
      this.orderPack.add(order);
      this.orderPackList.add(this.orderPack);
      assert (this.orderPack.size() == 4);
      PickRequest pickrequest = new PickRequest();
      pickrequest.setOrderPack(this.orderPack);
      this.orderPack = new ArrayList<Order>();
      System.out.println(pickrequest.toString());
      pickrequests.add(pickrequest);
      sequenceList.add(pickrequest);
    }
  }

  /**
   * Give picker instructions.
   *
   * @param items
   *          the items
   */
  public void doPicker(String[] items) {
    String name = items[1];
    Picker picker = null;
    if (this.getWorkerMap().containsKey(name)) {
      picker = (Picker) this.getWorkerMap().get(name);
    } else {
      picker = new Picker(name, items[0]);

      this.getWorkerMap().put(name, picker);
    }
    if (items[2].equals("ready")) {
      picker.ready();
    } else if (items[2].equals("pick")) {
      picker.pick(items[3]);
    } else if (items[2].equals("to")) {
      picker.marshaling();
    }
  }

  /**
   * Give sequencer instructions.
   *
   * @param items
   *          the items
   */
  public void doSequencer(String[] items) {
    String name = items[1];
    Sequencer sqcer = null;
    if (this.getWorkerMap().containsKey(name)) {
      sqcer = (Sequencer) this.getWorkerMap().get(name);
    } else {
      sqcer = new Sequencer(name, items[0]);
      this.getWorkerMap().put(name, sqcer);
    }
    if ("rescan".equals(items[2])) {
      sqcer.rescan();
    } else if (("ready".equals(items[2]))) {
      sqcer.getRequest();
    } else {
      sqcer.sequence(items[3]);
    }
  }

  /**
   * Give loader instructions.
   *
   * @param items
   *          the items
   */
  public void doLoader(String[] items) {

    String name = items[1];
    Loader loader = null;
    if (this.getWorkerMap().containsKey(name)) {
      loader = (Loader) this.getWorkerMap().get(name);
    } else {
      loader = new Loader(name, items[0]);
      this.getWorkerMap().put(name, loader);
    }
    if ("rescan".equals(items[2])) {
      loader.rescan();
    } else if (("loads".equals(items[2]))) {
      loader.load();
    } else if (("ready".equals(items[2]))) {
      loader.ready();
    }
  }

  /**
   * Give replenisher instructions.
   *
   * @param items
   *          the items
   */
  public void doReplenisher(String[] items) {
    String name = items[1];
    Replenisher rper = null;
    if (this.getWorkerMap().containsKey(name)) {
      rper = (Replenisher) this.getWorkerMap().get(name);
    } else {
      rper = new Replenisher(name, items[0]);
      this.getWorkerMap().put(name, rper);
    }
    if (items.length > 3) {
      rper.replenish(items[3], Integer.parseInt(items[4]), Integer.parseInt(items[5]), 
          Integer.parseInt(items[6]));

    }
  }

  /**
   * Pick from a specified location.
   *
   * @param location
   *          the location
   */
  public void pickfrom(String location) {
    PickFace pickface = this.getPickFaces().get(location);
    pickface.addNum(-1);
    this.getPickFaces().put(location, pickface);

  }
  
  /**
   * return the pickrequest.
   */
  public PickRequest requestForPicking() {
    if (this.getPickRequestList().isEmpty()) {
      return null;
    } else {
      PickRequest pickrequest = this.getPickRequestList().get(0);
      this.getPickRequestList().remove(0);
      return pickrequest;
    }
  }

  /**
   * Add pick requests to the pick request list.
   *
   * @param pick
   *          the pick
   */
  public void pickRequestListadd(PickRequest pick) {
    pickrequests.add(pick);
  }

  /**
   * Add pick requests to the sequence list.
   *
   * @param pick
   *          the pick
   */
  public void sequenceListadd(PickRequest pick) {
    sequenceList.add(pick);
  }

  /**
   * add items to be loaded to the warehouse load list.
   *
   * @param num
   *          the number
   * @param pallet
   *          the pallet
   */
  public void loadListadd(Integer num, Pallet pallet) {
    this.getLoadList().put(num, pallet);
  }

  /**
   * Gets the pick request list.
   *
   * @return the pick request list
   */
  public ArrayList<PickRequest> getPickRequestList() {
    return pickrequests;
  }

  /**
   * Gets the sequence list.
   *
   * @return the sequence list
   */
  public ArrayList<PickRequest> getSequenceList() {
    return sequenceList;
  }

  /**
   * Gets the load list.
   *
   * @return the load list
   */
  public TreeMap<Integer, Pallet> getLoadList() {
    return loadList;
  }

  /**
   * Gets the done list.
   *
   * @return the done list
   */
  public ArrayList<Pallet> getDoneList() {
    return doneList;
  }

  /**
   * Gets the truck list.
   *
   * @return the truck list
   */
  public ArrayList<Truck> getTruckList() {
    return truckList;
  }

  /**
   * Gets the truck.
   *
   * @return the truck
   */
  public Truck getTruck() {
    return this.truck;
  }

  /**
   * Gets the new truck.
   *
   */
  public void getNewTruck() {
    this.truck = new Truck();
  }

  /**
   * Gets the pick faces.
   *
   * @return the pickfaces
   */
  public HashMap<String, PickFace> getPickFaces() {
    return pickfaces;
  }

  /**
   * Gets the worker.
   *
   * @return the worker
   */
  public HashMap<String, Worker> getWorkerMap() {
    return workerMap;
  }

  public ArrayList<ArrayList<Order>> getOrderPackList() {
    return this.orderPackList;
  }

  public HashMap<Integer, Pallet> getWrongOrderPallets() {
    return this.sequencerrescannedwrong;
  }

  public void addWrongOrderPallets(Integer key, Pallet value) {
    this.sequencerrescannedwrong.put(key, value);
  }

  public void addpicked(Integer id, ArrayList<String> skus) {
    picked.put(id, skus);
  }

  public HashMap<Integer, ArrayList<String>> getpicked() {
    return picked;
  }
}
