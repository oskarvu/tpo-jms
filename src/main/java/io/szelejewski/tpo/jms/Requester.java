package io.szelejewski.tpo.jms;

import io.szelejewski.tpo.jms.request.ArithmeticRequest;
import io.szelejewski.tpo.jms.request.RandomRequest;
import io.szelejewski.tpo.jms.request.Request;
import io.szelejewski.tpo.jms.response.ArithmeticResponse;
import io.szelejewski.tpo.jms.response.RandomResponse;
import io.szelejewski.tpo.jms.response.Response;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.concurrent.atomic.AtomicLong;

public class Requester implements Runnable {
  private static final Logger logger = Logger.getLogger(App.class);
  private final Connection connection;
  private final String queueName;
  private static final AtomicLong idCounter = new AtomicLong(0);
  protected final long id = idCounter.incrementAndGet();

  public Requester(Connection connection, String queueName) {
    this.connection = connection;
    this.queueName = queueName;
  }

  @Override
  public void run() {
    // creating 3 requests
    ArithmeticRequest requestOne = new ArithmeticRequest(10, 20, ArithmeticOperation.ADD);
    Request requestTwo = new ArithmeticRequest(-2.55, 11.3, ArithmeticOperation.DIVIDE);
    Request requestThree = new RandomRequest();
    try {
      connection.start();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      Destination destination = session.createQueue(queueName);
      // 3 messages
      prepareAndSendMessage(requestOne, session, destination);
      prepareAndSendMessage(requestTwo, session, destination);
      prepareAndSendMessage(requestThree, session, destination);
      // waiting for response from all messages
      MessageConsumer messageConsumer =
          session.createConsumer(destination, "name LIKE '%RESPONSE' and id = " + id);
      messageConsumer.setMessageListener(new Listener());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void prepareAndSendMessage(Request request, Session session, Destination destination)
      throws Exception {
    MessageProducer messageProducer = session.createProducer(destination);
    logger.warn("-" + id + " : object put into message");
    ObjectMessage message = session.createObjectMessage(request);
    message.setLongProperty("id", id);
    message.setStringProperty("name", request.getName());
    messageProducer.send(message);
    logger.warn("-" + id + " : message put into destination");
  }

  private static class Listener implements MessageListener {

    @Override
    public void onMessage(Message message) {
      try {
        logger.warn("-" + message.getStringProperty("id") + " : message retrieved after processing");
        ObjectMessage objectMessage = (ObjectMessage) message;
        Response response = (Response) objectMessage.getObject();
        String result;
        if (response.getName().equals("ARITHMETIC_RESPONSE")) {
          ArithmeticResponse arithmeticResponse = (ArithmeticResponse) response;
          if (arithmeticResponse.getError() == null) {
            result = "calculation: " + arithmeticResponse.getResult();
          } else {
            result = "arithmetic error";
          }
        } else {
          RandomResponse randomResponse = (RandomResponse) response;
          if (randomResponse.getError() == null) {
            result = "random number: " + randomResponse.getResult();
          } else {
            result = "random error";
          }
        }
        logger.warn("-" + message.getStringProperty("id") + " : message consumed, result: " + result);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
