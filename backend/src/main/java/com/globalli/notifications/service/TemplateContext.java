package com.globalli.notifications.service;

import java.util.Map;

public sealed interface TemplateContext {

  Map<String, Object> parameters();

  record LevelUp(int level) implements TemplateContext {
    @Override
    public Map<String, Object> parameters() {
      return Map.of("level", level);
    }
  }

  record ItemAcquired(String item) implements TemplateContext {
    @Override
    public Map<String, Object> parameters() {
      return Map.of("item", item);
    }
  }

  record ChallengeCompleted(String challenge) implements TemplateContext {
    @Override
    public Map<String, Object> parameters() {
      return Map.of("challenge", challenge);
    }
  }

  record PvpDefeated(long attackerUserId) implements TemplateContext {
    @Override
    public Map<String, Object> parameters() {
      return Map.of("attacker", attackerUserId);
    }
  }

  record PvpAttacked(long attackerUserId) implements TemplateContext {
    @Override
    public Map<String, Object> parameters() {
      return Map.of("attacker", attackerUserId);
    }
  }

  record SocialActor(long actorUserId) implements TemplateContext {
    @Override
    public Map<String, Object> parameters() {
      return Map.of("actor", actorUserId);
    }
  }
}
