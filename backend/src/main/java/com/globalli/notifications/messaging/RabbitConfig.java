package com.globalli.notifications.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.globalli.notifications.events.ChallengeCompleted;
import com.globalli.notifications.events.FriendRequestAccepted;
import com.globalli.notifications.events.FriendRequestSent;
import com.globalli.notifications.events.ItemAcquired;
import com.globalli.notifications.events.NewFollower;
import com.globalli.notifications.events.PlayerAttackedInPvp;
import com.globalli.notifications.events.PlayerDefeatedInPvp;
import com.globalli.notifications.events.PlayerLeveledUp;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  private static final Logger log = LoggerFactory.getLogger(RabbitConfig.class);

  private static final List<Class<?>> EVENT_TYPES =
      List.of(
          PlayerLeveledUp.class,
          PlayerDefeatedInPvp.class,
          PlayerAttackedInPvp.class,
          ItemAcquired.class,
          ChallengeCompleted.class,
          FriendRequestSent.class,
          FriendRequestAccepted.class,
          NewFollower.class);

  @Bean
  public DefaultJackson2JavaTypeMapper eventTypeMapper() {
    DefaultJackson2JavaTypeMapper mapper = new DefaultJackson2JavaTypeMapper();
    mapper.setTrustedPackages("com.globalli.notifications.events");
    Map<String, Class<?>> idClassMapping =
        EVENT_TYPES.stream().collect(Collectors.toMap(Class::getSimpleName, Function.identity()));
    mapper.setIdClassMapping(idClassMapping);
    return mapper;
  }

  @Bean
  public MessageConverter messageConverter(DefaultJackson2JavaTypeMapper eventTypeMapper) {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
    converter.setJavaTypeMapper(eventTypeMapper);
    return converter;
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, MessageConverter messageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter);
    template.setMandatory(true);
    template.setReturnsCallback(
        returned ->
            log.error(
                "Unroutable message: exchange={} routingKey={} replyText={}",
                returned.getExchange(),
                returned.getRoutingKey(),
                returned.getReplyText()));
    template.setDefaultReceiveQueue(null);
    template.setBeforePublishPostProcessors(
        message -> {
          message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
          return message;
        });
    return template;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      SimpleRabbitListenerContainerFactoryConfigurer configurer,
      ConnectionFactory connectionFactory,
      MessageConverter messageConverter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    factory.setMessageConverter(messageConverter);
    factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
    return factory;
  }
}
