package com.globalli.notifications.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.globalli.notifications.domain.Notification;
import com.globalli.notifications.messaging.RabbitContainerSupport;
import com.globalli.notifications.simulators.GameEngine;
import java.lang.reflect.Type;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class EndToEndNotificationTest extends RabbitContainerSupport {

  @LocalServerPort private int port;
  @Autowired private GameEngine gameEngine;

  @Test
  void clientReceivesNotificationOverWebSocket() throws Exception {
    WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setObjectMapper(new ObjectMapper().registerModule(new JavaTimeModule()));
    stompClient.setMessageConverter(converter);

    LinkedBlockingQueue<Notification> received = new LinkedBlockingQueue<>();

    StompSession session =
        stompClient
            .connectAsync("ws://localhost:" + port + "/ws", new StompSessionHandlerAdapter() {})
            .get(5, TimeUnit.SECONDS);

    session.subscribe(
        "/topic/notifications/1",
        new StompFrameHandler() {
          @Override
          public Type getPayloadType(StompHeaders headers) {
            return Notification.class;
          }

          @Override
          public void handleFrame(StompHeaders headers, Object payload) {
            received.add((Notification) payload);
          }
        });

    Thread.sleep(500);
    gameEngine.playerLeveledUp(1L);

    Notification notification = received.poll(5, TimeUnit.SECONDS);
    assertThat(notification).isNotNull();
    assertThat(notification.message()).isEqualTo("Congratulations! You've reached level 2!");

    session.disconnect();
    stompClient.stop();
  }
}
