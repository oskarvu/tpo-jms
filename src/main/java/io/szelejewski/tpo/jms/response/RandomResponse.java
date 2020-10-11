package io.szelejewski.tpo.jms.response;

import io.szelejewski.tpo.jms.response.Response;

public class RandomResponse extends Response {
  private static final long serialVersionUID = -8474300333957992569L;

  public RandomResponse(double result, String error) {
    super("RANDOM_RESPONSE", result, error);
  }
}
