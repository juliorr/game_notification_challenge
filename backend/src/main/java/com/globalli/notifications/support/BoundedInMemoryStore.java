package com.globalli.notifications.support;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

public class BoundedInMemoryStore<T> {

  private static final int DEFAULT_MAX_ENTRIES = 500;

  private final Function<T, Long> userIdExtractor;
  private final int maxEntries;
  private final Deque<T> entries = new ArrayDeque<>();
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public BoundedInMemoryStore(Function<T, Long> userIdExtractor) {
    this(userIdExtractor, DEFAULT_MAX_ENTRIES);
  }

  public BoundedInMemoryStore(Function<T, Long> userIdExtractor, int maxEntries) {
    this.userIdExtractor = userIdExtractor;
    this.maxEntries = maxEntries;
  }

  public void record(T entry) {
    withWriteLock(
        () -> {
          entries.addFirst(entry);
          while (entries.size() > maxEntries) {
            entries.pollLast();
          }
        });
  }

  public List<T> findAll() {
    return withReadLock(() -> new ArrayList<>(entries));
  }

  public List<T> findByUserId(long userId) {
    return withReadLock(
        () -> {
          List<T> result = new ArrayList<>();
          for (T entry : entries) {
            if (userIdExtractor.apply(entry) == userId) {
              result.add(entry);
            }
          }
          return result;
        });
  }

  public void clear() {
    withWriteLock(entries::clear);
  }

  private <R> R withReadLock(Supplier<R> action) {
    lock.readLock().lock();
    try {
      return action.get();
    } finally {
      lock.readLock().unlock();
    }
  }

  private void withWriteLock(Runnable action) {
    lock.writeLock().lock();
    try {
      action.run();
    } finally {
      lock.writeLock().unlock();
    }
  }
}
