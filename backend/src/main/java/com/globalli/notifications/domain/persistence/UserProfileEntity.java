package com.globalli.notifications.domain.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_profiles")
public class UserProfileEntity {

  @Id
  @Column(name = "user_id", nullable = false)
  private long userId;

  @Column(nullable = false)
  private int level;

  protected UserProfileEntity() {}

  public UserProfileEntity(long userId, int level) {
    this.userId = userId;
    this.level = level;
  }

  public long getUserId() {
    return userId;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }
}
