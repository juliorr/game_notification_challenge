package com.globalli.notifications.messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopology {

  private static final int DELIVERY_LIMIT = 5;

  private record DispatchRoute(String queueName, String binding) {}

  private static final List<DispatchRoute> DISPATCH_ROUTES =
      List.of(
          new DispatchRoute(EventRoutingKeys.GAME_QUEUE, EventRoutingKeys.GAME_BINDING),
          new DispatchRoute(EventRoutingKeys.SOCIAL_QUEUE, EventRoutingKeys.SOCIAL_BINDING),
          new DispatchRoute(EventRoutingKeys.USER_QUEUE, EventRoutingKeys.USER_BINDING));

  @Bean
  public Declarables notificationsTopology() {
    TopicExchange eventsExchange = new TopicExchange(EventRoutingKeys.EVENTS_EXCHANGE, true, false);
    TopicExchange dlxExchange = new TopicExchange(EventRoutingKeys.DLX_EXCHANGE, true, false);

    Map<String, Object> dispatchArgs =
        Map.of(
            "x-dead-letter-exchange",
            EventRoutingKeys.DLX_EXCHANGE,
            "x-delivery-limit",
            DELIVERY_LIMIT);

    List<Declarable> declarables = new ArrayList<>();
    declarables.add(eventsExchange);
    declarables.add(dlxExchange);

    for (DispatchRoute route : DISPATCH_ROUTES) {
      Queue queue = createDispatchQueue(route.queueName(), dispatchArgs);
      declarables.add(queue);
      declarables.add(BindingBuilder.bind(queue).to(eventsExchange).with(route.binding()));
    }

    Queue deadLetterQueue =
        QueueBuilder.durable(EventRoutingKeys.DEAD_LETTER_QUEUE).quorum().build();
    declarables.add(deadLetterQueue);
    declarables.add(bind(deadLetterQueue, dlxExchange, EventRoutingKeys.DEAD_LETTER_BINDING));

    return new Declarables(declarables);
  }

  private static Queue createDispatchQueue(String name, Map<String, Object> args) {
    return QueueBuilder.durable(name).quorum().withArguments(args).build();
  }

  private static Binding bind(Queue queue, TopicExchange exchange, String routingKey) {
    return BindingBuilder.bind(queue).to(exchange).with(routingKey);
  }
}
