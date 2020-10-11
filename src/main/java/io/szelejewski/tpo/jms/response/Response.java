package io.szelejewski.tpo.jms.response;

import java.io.Serializable;

public class Response implements Serializable {
  private static final long serialVersionUID = 5542944823755574661L;
  protected final String name;
  protected final double result;
  protected final String error;

  public Response(String name, double result, String error) {
    this.name = name;
    this.result = result;
    this.error = error;
  }

  public String getName() {
    return name;
  }

  public double getResult() {
    return result;
  }

  public String getError() {
    return error;
  }
}
