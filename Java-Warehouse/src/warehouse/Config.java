package warehouse;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * The Class Configure.
 */
public class Config {

  /** The initial pick face number. */
  private static int initPickFaceNum = 30;

  /** The replenish limit. */
  private static int replenishLimit = 5;

  /** The number of items to replenish. */
  private static int replenishNum = 25;

  /**
   * Gets the initial pick face number.
   *
   * @return the initial number of items on pick face.
   */
  public static int getInitPickFaceNum() {
    return initPickFaceNum;
  }

  /**
   * Sets the initial number of items on pick face.
   *
   * @param initPickFaceNum
   *          the new stock number of the pick face
   */
  public static void setInitPickFaceNum(int initPickFaceNum) {
    Config.initPickFaceNum = initPickFaceNum;
  }

  /**
   * Gets the replenish num.
   *
   * @return the replenish num
   */
  public static int getReplenishNum() {
    return replenishNum;
  }

  /**
   * Sets the replenish num.
   *
   * @param replensihNum
   *          the new replenish num
   */
  public static void setReplenishNum(int replensihNum) {
    Config.replenishNum = replensihNum;
  }

  /**
   * Gets the replenish limit.
   *
   * @return the replenish limit
   */
  public static int getReplenishLimit() {
    return replenishLimit;
  }

  /**
   * Sets the replenish limit.
   *
   * @param replenishLimit
   *          the new replenish limit
   */
  public static void setReplenishLimit(int replenishLimit) {
    Config.replenishLimit = replenishLimit;
  }

  /**
   * Read configure from file.
   *
   * @param config
   *          the file to be read that contains all the configures.
   */
  public static void readConfig(String config) {
    BufferedReader cfg = null;

    try {
      cfg = new BufferedReader(new FileReader(config));

      String line = cfg.readLine();
      while (line != null) {
        if (line.startsWith("#")) {
          // comment
          continue;
        }
        String[] items = line.split("=");

        if ("initPickFaceNum".equals(items[0])) {
          setInitPickFaceNum(Integer.parseInt(items[1]));
        }

        if ("replenishLimit".equals(items[0])) {
          setReplenishLimit(Integer.parseInt(items[1]));
        }
        if ("replenishNum".equals(items[0])) {
          setReplenishNum(Integer.parseInt(items[1]));
        }

        line = cfg.readLine();

      }
      cfg.close();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
