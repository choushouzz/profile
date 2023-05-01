package warehouse;

/**
 * The Class Worker.
 */
public class Worker {

  /** The name. */
  private String name;

  /** The title. */
  private String title;

  /**
   * Constructor of worker.
   */
  public Worker() {
  }

  /**
   * Constructor of worker.
   *
   * @param name
   *          the name
   * @param title
   *          the title
   */
  public Worker(String name, String title) {
    this.name = name;
    this.title = title;
  }

  /**
   * Gets the name of the worker.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Gets the title of the worker.
   *
   * @return the title
   */
  public String getTitle() {
    return this.title;
  }

}
