package io.szelejewski.tpo.jms;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class App {
  private final static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
  private final static ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
  private final static String queueName = "QUEUE";

  public static void main(String[] args) throws Exception {
    ThreadPoolExecutor requestExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    ThreadPoolExecutor serviceExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);
    connectionFactory.setTrustedPackages(new ArrayList<>(Collections.singletonList("io.szelejewski.tpo.jms")));
    // for simplicity I have used just a simple loops to populate thread pools
    // spinning up some threads for Services - Service listens for incoming requests asynchronously
    for (int i = 0; i < 5; i++) {
      serviceExecutor.execute(new Service(connectionFactory.createConnection(), queueName));
    }
    // queuing 100 requesters, max 10 threads active, (each requester makes 3 requests)
    // after sending request, Requester listens for incoming responses asynchronously
    for (int i = 0; i < 20; i++) {
      requestExecutor.execute(new Requester(connectionFactory.createConnection(), queueName));
    }
    requestExecutor.shutdown();
    serviceExecutor.shutdown();
  }
}
