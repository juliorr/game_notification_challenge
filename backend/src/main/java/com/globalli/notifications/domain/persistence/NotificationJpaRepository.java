package com.globalli.notifications.domain.persistence;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {

  @Query(
      """
      select n from NotificationEntity n
      where n.userId = :userId and n.deletedAt is null
      order by n.createdAt desc
      """)
  List<NotificationEntity> findActiveByUserId(@Param("userId") long userId, Pageable pageable);

  @Query(
      """
      select n from NotificationEntity n
      where n.userId = :userId and n.deletedAt is null and n.createdAt < :cursor
      order by n.createdAt desc
      """)
  List<NotificationEntity> findActiveByUserIdBefore(
      @Param("userId") long userId, @Param("cursor") Instant cursor, Pageable pageable);

  @Query(
      """
      select count(n) from NotificationEntity n
      where n.userId = :userId and n.deletedAt is null and n.readAt is null
      """)
  long countUnread(@Param("userId") long userId);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      update NotificationEntity n set n.readAt = :now
      where n.userId = :userId and n.readAt is null and n.deletedAt is null
      """)
  int markAllRead(@Param("userId") long userId, @Param("now") Instant now);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      update NotificationEntity n set n.readAt = :now
      where n.id = :id and n.userId = :userId
      and n.readAt is null and n.deletedAt is null
      """)
  int markRead(@Param("userId") long userId, @Param("id") UUID id, @Param("now") Instant now);

  @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query(
      """
      update NotificationEntity n set n.deletedAt = :now
      where n.userId = :userId and n.deletedAt is null
      """)
  int softDeleteAllByUserId(@Param("userId") long userId, @Param("now") Instant now);
}
