package io.szelejewski.tpo.jms.request;

public class RandomRequest extends Request {
  private static final long serialVersionUID = 4352078684991694026L;

  public RandomRequest() {
    super("RANDOM_REQUEST");
  }
}
