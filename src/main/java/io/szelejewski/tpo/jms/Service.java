package io.szelejewski.tpo.jms;

import io.szelejewski.tpo.jms.request.ArithmeticRequest;
import io.szelejewski.tpo.jms.request.Request;
import io.szelejewski.tpo.jms.response.ArithmeticResponse;
import io.szelejewski.tpo.jms.response.RandomResponse;
import io.szelejewski.tpo.jms.response.Response;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Service implements Runnable {
  private static final Logger logger = Logger.getLogger(App.class);
  private final Connection connection;
  private final String queueName;

  public Service(Connection connection, String queueName) {
    this.connection = connection;
    this.queueName = queueName;
  }

  @Override
  public void run() {
    try{
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination destination = session.createQueue(queueName);
      MessageConsumer messageConsumer = session.createConsumer(destination, "name LIKE '%REQUEST'");
      messageConsumer.setMessageListener(new Listener());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private class Listener implements MessageListener {

    @Override
    public void onMessage(Message message) {
      try {
        logger.warn(" : message from requester " + message.getLongProperty("id") + " retrieved from destination");
        logger.warn(" : processing request from requester " + message.getLongProperty("id"));
        int duration = ThreadLocalRandom.current().nextInt(3, 6);
        TimeUnit.SECONDS.sleep(duration);
        // getting request object
        ObjectMessage objectMessage = (ObjectMessage) message;
        Request request = (Request) objectMessage.getObject();
        // creating response object
        Response response = null;
        if (request.getName().equals("ARITHMETIC_REQUEST")) {
          ArithmeticRequest arithmeticRequest = (ArithmeticRequest) request;
          double result;
          switch (arithmeticRequest.getOperation()) {
            case ADD:
              result = arithmeticRequest.getA() + arithmeticRequest.getB();
              response = new ArithmeticResponse(result, null);
              break;
            case SUBTRACT:
              result = arithmeticRequest.getA() - arithmeticRequest.getB();
              response = new ArithmeticResponse(result, null);
              break;
            case MULTIPLY:
              result = arithmeticRequest.getA() * arithmeticRequest.getB();
              response = new ArithmeticResponse(result, null);
              break;
            case DIVIDE:
              if (arithmeticRequest.getB() == 0) {
                response = new ArithmeticResponse(0, "Dividing by zero not allowed.");
              } else {
                result = arithmeticRequest.getA() / arithmeticRequest.getB();
                response = new ArithmeticResponse(result, null);
              }
              break;
          }
        } else {
          response = new RandomResponse(ThreadLocalRandom.current().nextDouble(), null);
        }

        // sending response object
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createQueue(queueName);
        MessageProducer messageProducer = session.createProducer(destination);
        ObjectMessage responseMessage = session.createObjectMessage(response);
        responseMessage.setLongProperty("id", message.getLongProperty("id"));
        responseMessage.setStringProperty("name", response.getName());
        logger.warn(" : message to requester " + message.getLongProperty("id") + " put into destination");
        messageProducer.send(responseMessage);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
