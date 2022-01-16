package mikeshafter.mikestcaddons;

import java.util.Objects;


public class PositiveDouble {
  double v;
  
  public PositiveDouble(double v) {
    this.v = v;
    if (this.v < 0) this.v = 0;
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(v);
  }
  
  public double getV() {
    return v;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PositiveDouble that)) return false;
    return Double.compare(that.v, v) == 0;
  }
  
  public void setV(double v) {
    this.v = v;
    if (this.v < 0) this.v = 0;
  }
  
  public void add(double v) {
    this.v += v;
    if (this.v < 0) this.v = 0;
  }
  
  public void setV(PositiveDouble v) {
    this.v = v.getV();
    if (this.v < 0) this.v = 0;
  }
  
  public void subtract(double v) {
    this.v -= v;
    if (this.v < 0) this.v = 0;
  }
  
  public void multiply(double v) {
    this.v *= v;
    if (this.v < 0) this.v = 0;
  }
  
  public void divide(double v) {
    this.v /= v;
    if (this.v < 0) this.v = 0;
  }
  
  public void add(PositiveDouble v) {
    this.v += v.getV();
    if (this.v < 0) this.v = 0;
  }
  
  public void subtract(PositiveDouble v) {
    this.v -= v.getV();
    if (this.v < 0) this.v = 0;
  }
  
  public void multiply(PositiveDouble v) {
    this.v *= v.getV();
    if (this.v < 0) this.v = 0;
  }
  
  public void divide(PositiveDouble v) {
    this.v /= v.getV();
    if (this.v < 0) this.v = 0;
  }
  
  
}
