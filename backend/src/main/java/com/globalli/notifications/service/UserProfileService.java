package com.globalli.notifications.service;

import com.globalli.notifications.domain.persistence.UserProfileEntity;
import com.globalli.notifications.domain.persistence.UserProfileJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

  private static final int INITIAL_LEVEL = 1;

  private final UserProfileJpaRepository repository;

  public UserProfileService(UserProfileJpaRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public int incrementLevel(long userId) {
    UserProfileEntity profile =
        repository.findById(userId).orElseGet(() -> new UserProfileEntity(userId, INITIAL_LEVEL));
    profile.setLevel(profile.getLevel() + 1);
    repository.save(profile);
    return profile.getLevel();
  }

  @Transactional(readOnly = true)
  public int currentLevel(long userId) {
    return repository.findById(userId).map(UserProfileEntity::getLevel).orElse(INITIAL_LEVEL);
  }
}
