package com.globalli.notifications.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.globalli.notifications.events.AllNotificationsRead;
import com.globalli.notifications.events.NotificationRead;
import com.globalli.notifications.events.NotificationsCleared;
import com.globalli.notifications.messaging.DomainEventPublisher;
import com.globalli.notifications.service.NotificationQueryService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificationsController.class)
class NotificationsControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private NotificationQueryService queryService;
  @MockitoBean private DomainEventPublisher eventPublisher;

  @Test
  void getDelegatesToQueryService() throws Exception {
    when(queryService.findPage(eq(1L), any(), any())).thenReturn(List.of());

    mockMvc.perform(get("/api/users/1/notifications?limit=200")).andExpect(status().isOk());

    verify(queryService).findPage(eq(1L), eq(Optional.empty()), eq(200));
  }

  @Test
  void unreadCountReturnsValueFromQueryService() throws Exception {
    when(queryService.unreadCount(7L)).thenReturn(4L);

    mockMvc
        .perform(get("/api/users/7/notifications/unread-count"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count").value(4));
  }

  @Test
  void markAllReadPublishesEvent() throws Exception {
    mockMvc.perform(post("/api/users/3/notifications/read")).andExpect(status().isAccepted());

    verify(eventPublisher).publish(new AllNotificationsRead(3L));
  }

  @Test
  void markReadPublishesEvent() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc
        .perform(post("/api/users/3/notifications/" + id + "/read"))
        .andExpect(status().isAccepted());

    ArgumentCaptor<NotificationRead> captor = ArgumentCaptor.forClass(NotificationRead.class);
    verify(eventPublisher).publish(captor.capture());
    org.assertj.core.api.Assertions.assertThat(captor.getValue())
        .isEqualTo(new NotificationRead(3L, id));
  }

  @Test
  void clearAllPublishesEvent() throws Exception {
    mockMvc.perform(delete("/api/users/3/notifications")).andExpect(status().isAccepted());

    verify(eventPublisher).publish(new NotificationsCleared(3L));
  }
}
