package warehouse;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

/**
 * The Class Order.
 */
public class Order {

  /** The colour. */
  private String colour = null;

  /** The model. */
  private String model = null;

  /** The SKU 1. */
  private String sku1 = null;

  /** The SKU 2. */
  private String sku2 = null;

  /** The SKU 1 lookup map. */
  // Look up table for SKU from color and model
  private static HashMap<String, String> SKU1LookupMap = new HashMap<String, String>();

  /** The SKU 2 lookup map. */
  private static HashMap<String, String> SKU2LookupMap = new HashMap<String, String>();

  /**
   * Instantiates a new order.
   *
   * @param mod
   *          the model
   * @param color
   *          the color
   */
  public Order(String mod, String color) {
    this.colour = color;
    this.model = mod;
    this.sku1 = getSku1(mod, color);
    this.sku2 = getSku2(mod, color);

  }
  
  
  /**
   * Instantiates a new order.
   *
   * @param mod
   *          the model
   * @param color
   *          the color
   * @param sku1
   *          the front sku
   * @param sku2
   *          the rear sku
   */
  public Order(String mod, String color, String sku1, String sku2) {
    this.colour = color;
    this.model = mod;
    this.sku1 = sku1;
    this.sku2 = sku2;

  }

  /**
   * Initializes the lookup maps.
   * 
   * @param file
   *          the name of file to be read
   */
  public static void initLookupMaps(String file) {
    BufferedReader transFile = null;
    try {
      transFile = new BufferedReader(new FileReader(file));

      String line = null;
      transFile.readLine();// skip first title line
      while ((line = transFile.readLine()) != null) {
        String[] items = line.split(",");
        String clr = items[0];
        String mod = items[1];
        String s1 = items[2];
        String s2 = items[3];
        String key = clr + "_" + mod;
        getsku1LookupMap().put(key, s1);
        getsku2LookupMap().put(key, s2);
      }
      transFile.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  /**
   * Gets the key.
   *
   * @param mod
   *          the modLE
   * @param clr
   *          the colour
   * @return the string
   */
  /*
   * 
   * get hashmap key
   */
  public static String getKey(String mod, String clr) {
    String key = clr + "_" + mod;
    return key;
  }

  /**
   * Gets the SKU 1.
   *
   * @param model
   *          the model
   * @param color
   *          the color
   * @return the string
   */
  public static String getSku1(String model, String color) {

    String key = getKey(model, color);
    if (getsku1LookupMap().containsKey(key)) {
      return getsku1LookupMap().get(key);
    }
    return null;
  }

  /**
   * Gets the SKU 2.
   *
   * @param model
   *          the model
   * @param color
   *          the color
   * @return the string
   * 
   */
  public static String getSku2(String model, String color) {

    String key = getKey(model, color);
    if (getsku1LookupMap().containsKey(key)) {
      return getsku2LookupMap().get(key);
    }
    return null;
  }

  public String getColour() {
    return this.colour;
  }

  public String getModel() {
    return this.model;
  }

  public String getFrontSku() {
    return this.sku1;
  }

  public String getRearSku() {
    return this.sku2;
  }

  public static HashMap<String, String> getsku1LookupMap() {
    return SKU1LookupMap;
  }

  public static HashMap<String, String> getsku2LookupMap() {
    return SKU2LookupMap;
  }

}
