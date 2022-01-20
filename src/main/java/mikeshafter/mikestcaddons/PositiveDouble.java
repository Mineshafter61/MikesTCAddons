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
  
  public void set(double v) {
    this.v = v;
    if (this.v < 0) this.v = 0;
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PositiveDouble that)) return false;
    return Double.compare(that.v, v) == 0;
  }
  
  public void set(PositiveDouble v) {
    this.v = v.get();
    if (this.v < 0) this.v = 0;
  }
  
  public void add(double v) {
    this.v += v;
    if (this.v < 0) this.v = 0;
  }
  
  public double get() {
    return v;
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
    this.v += v.get();
    if (this.v < 0) this.v = 0;
  }
  
  public void subtract(PositiveDouble v) {
    this.v -= v.get();
    if (this.v < 0) this.v = 0;
  }
  
  public void multiply(PositiveDouble v) {
    this.v *= v.get();
    if (this.v < 0) this.v = 0;
  }
  
  public void divide(PositiveDouble v) {
    this.v /= v.get();
    if (this.v < 0) this.v = 0;
  }
  
  public boolean lessThan(double v) {
    return this.v < v;
  }
  
  public boolean moreThan(double v) {
    return this.v > v;
  }
  
  public boolean lessThanOrEquals(double v) {
    return this.v <= v;
  }
  
  public boolean moreThanOrEquals(double v) {
    return this.v >= v;
  }
  
  public boolean lessThan(PositiveDouble v) {
    return this.v < v.get();
  }
  
  public boolean moreThan(PositiveDouble v) {
    return this.v > v.get();
  }
  
  public boolean lessThanOrEquals(PositiveDouble v) {
    return this.v <= v.get();
  }
  
  public boolean moreThanOrEquals(PositiveDouble v) {
    return this.v >= v.get();
  }
}
