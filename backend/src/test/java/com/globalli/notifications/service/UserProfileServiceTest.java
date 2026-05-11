package com.globalli.notifications.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.globalli.notifications.domain.persistence.UserProfileEntity;
import com.globalli.notifications.domain.persistence.UserProfileJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class UserProfileServiceTest {

  @Mock private UserProfileJpaRepository repository;

  private UserProfileService service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new UserProfileService(repository);
  }

  @Test
  void incrementsFromInitialLevelWhenProfileMissing() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    int level = service.incrementLevel(1L);

    assertThat(level).isEqualTo(2);
    verify(repository).save(any(UserProfileEntity.class));
  }

  @Test
  void incrementsExistingProfile() {
    UserProfileEntity existing = new UserProfileEntity(1L, 5);
    when(repository.findById(1L)).thenReturn(Optional.of(existing));

    int level = service.incrementLevel(1L);

    assertThat(level).isEqualTo(6);
    verify(repository).save(existing);
  }

  @Test
  void currentLevelReturnsInitialWhenAbsent() {
    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThat(service.currentLevel(1L)).isEqualTo(1);
  }

  @Test
  void currentLevelReturnsRepositoryValue() {
    when(repository.findById(1L)).thenReturn(Optional.of(new UserProfileEntity(1L, 9)));

    assertThat(service.currentLevel(1L)).isEqualTo(9);
  }
}
