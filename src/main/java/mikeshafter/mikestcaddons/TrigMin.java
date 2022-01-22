package mikeshafter.mikestcaddons;

public class TrigMin {
  private final String variable;
  private int min;
  
  public TrigMin(int min, String variable) {
    this.min = min;
    this.variable = variable;
  }
  
  public void nextMin() {
    this.min--;
  }
  
  public int getMin() {
    return min;
  }
  
  public String getVariable() {
    return variable;
  }
}
