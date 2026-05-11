package com.globalli.notifications.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.globalli.notifications.service.UserPreferenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PreferencesController.class)
@Import(UserPreferenceService.class)
class PreferencesControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void getReturnsAllEnabledByDefault() throws Exception {
    mockMvc
        .perform(get("/api/users/1/preferences"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.notificationsEnabled").value(true))
        .andExpect(jsonPath("$.enabledCategories").isArray())
        .andExpect(jsonPath("$.enabledChannels").isArray());
  }

  @Test
  void putUpdatesNotificationsEnabledOnly() throws Exception {
    mockMvc
        .perform(
            put("/api/users/1/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"notificationsEnabled\": false}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.notificationsEnabled").value(false));
  }

  @Test
  void putWithCategoriesReplacesEnabledSet() throws Exception {
    mockMvc
        .perform(
            put("/api/users/1/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"enabledCategories\": [\"GAME\"]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.enabledCategories[0]").value("GAME"))
        .andExpect(jsonPath("$.enabledCategories.length()").value(1));
  }

  @Test
  void putWithChannelsReplacesEnabledSet() throws Exception {
    mockMvc
        .perform(
            put("/api/users/1/preferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"enabledChannels\": [\"IN_APP\"]}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.enabledChannels[0]").value("IN_APP"))
        .andExpect(jsonPath("$.enabledChannels.length()").value(1));
  }
}
