package io.szelejewski.tpo.jms.response;

import io.szelejewski.tpo.jms.response.Response;

public class ArithmeticResponse extends Response {
  private static final long serialVersionUID = 475168280016182057L;

  public ArithmeticResponse(double result, String error) {
    super("ARITHMETIC_RESPONSE", result, error);
  }
}
