package io.szelejewski.tpo.jms.request;

import io.szelejewski.tpo.jms.ArithmeticOperation;

public class ArithmeticRequest extends Request {
  private static final long serialVersionUID = -1233726650717693362L;
  private final double a;
  private final double b;
  private final ArithmeticOperation operation;

  public ArithmeticRequest(double a, double b, ArithmeticOperation operation) {
    super("ARITHMETIC_REQUEST");
    this.a = a;
    this.b = b;
    this.operation = operation;
  }

  public double getA() {
    return a;
  }

  public double getB() {
    return b;
  }

  public ArithmeticOperation getOperation() {
    return operation;
  }
}
