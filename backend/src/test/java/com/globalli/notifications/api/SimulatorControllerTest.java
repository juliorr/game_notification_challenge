package com.globalli.notifications.api;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globalli.notifications.api.dto.ChallengeRequest;
import com.globalli.notifications.api.dto.FollowRequest;
import com.globalli.notifications.api.dto.FriendRequestBody;
import com.globalli.notifications.api.dto.ItemRequest;
import com.globalli.notifications.api.dto.LevelUpRequest;
import com.globalli.notifications.api.dto.PvpRequest;
import com.globalli.notifications.simulators.GameEngine;
import com.globalli.notifications.simulators.SocialSystem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SimulatorController.class)
class SimulatorControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private GameEngine gameEngine;
  @MockitoBean private SocialSystem socialSystem;

  @Test
  void levelUpReturnsAcceptedAndDelegates() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/level-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LevelUpRequest(5L))))
        .andExpect(status().isAccepted());

    verify(gameEngine).playerLeveledUp(5L);
  }

  @Test
  void levelUpRejectsNonPositiveUserId() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/level-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LevelUpRequest(0L))))
        .andExpect(status().isBadRequest());

    verify(gameEngine, never()).playerLeveledUp(0L);
  }

  @Test
  void itemDelegatesToGameEngine() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ItemRequest(5L, "Sword"))))
        .andExpect(status().isAccepted());

    verify(gameEngine).itemAcquired(5L, "Sword");
  }

  @Test
  void itemRejectsBlankName() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/item")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ItemRequest(5L, "  "))))
        .andExpect(status().isBadRequest());
  }

  @Test
  void challengeDelegatesToGameEngine() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/challenge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChallengeRequest(5L, "Daily"))))
        .andExpect(status().isAccepted());

    verify(gameEngine).challengeCompleted(5L, "Daily");
  }

  @Test
  void pvpDefeatedDelegatesToGameEngine() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/pvp/defeated")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PvpRequest(5L, 9L))))
        .andExpect(status().isAccepted());

    verify(gameEngine).playerDefeatedInPvp(5L, 9L);
  }

  @Test
  void friendRequestDelegatesToSocialSystem() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/friend-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FriendRequestBody(1L, 2L))))
        .andExpect(status().isAccepted());

    verify(socialSystem).friendRequestSent(1L, 2L);
  }

  @Test
  void friendRequestRejectsSameUser() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/friend-request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FriendRequestBody(1L, 1L))))
        .andExpect(status().isBadRequest());

    verify(socialSystem, never()).friendRequestSent(1L, 1L);
  }

  @Test
  void friendAcceptDelegatesToSocialSystem() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/friend-accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FriendRequestBody(2L, 1L))))
        .andExpect(status().isAccepted());

    verify(socialSystem).friendRequestAccepted(2L, 1L);
  }

  @Test
  void followDelegatesToSocialSystem() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FollowRequest(3L, 5L))))
        .andExpect(status().isAccepted());

    verify(socialSystem).newFollower(3L, 5L);
  }

  @Test
  void followRejectsSameUser() throws Exception {
    mockMvc
        .perform(
            post("/api/sim/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new FollowRequest(7L, 7L))))
        .andExpect(status().isBadRequest());
  }
}
