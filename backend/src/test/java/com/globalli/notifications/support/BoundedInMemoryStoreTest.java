package com.globalli.notifications.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class BoundedInMemoryStoreTest {

  private record Entry(long userId, Instant sentAt, String label) {}

  private BoundedInMemoryStore<Entry> newStore(int max) {
    return new BoundedInMemoryStore<>(Entry::userId, max);
  }

  @Test
  void recordsAndFindsAllInDescendingInsertionOrder() {
    BoundedInMemoryStore<Entry> store = newStore(10);
    Instant base = Instant.parse("2026-01-01T00:00:00Z");
    store.record(new Entry(1L, base, "first"));
    store.record(new Entry(1L, base.plusSeconds(5), "second"));
    store.record(new Entry(1L, base.plusSeconds(10), "third"));

    assertThat(store.findAll())
        .extracting(Entry::label)
        .containsExactly("third", "second", "first");
  }

  @Test
  void findByUserIdFiltersAndPreservesNewestFirst() {
    BoundedInMemoryStore<Entry> store = newStore(10);
    Instant base = Instant.parse("2026-01-01T00:00:00Z");
    store.record(new Entry(1L, base, "u1-a"));
    store.record(new Entry(2L, base.plusSeconds(1), "u2-a"));
    store.record(new Entry(1L, base.plusSeconds(2), "u1-b"));

    assertThat(store.findByUserId(1L)).extracting(Entry::label).containsExactly("u1-b", "u1-a");
    assertThat(store.findByUserId(2L)).extracting(Entry::label).containsExactly("u2-a");
    assertThat(store.findByUserId(99L)).isEmpty();
  }

  @Test
  void evictsOldestEntriesWhenExceedingMax() {
    BoundedInMemoryStore<Entry> store = newStore(3);
    Instant base = Instant.parse("2026-01-01T00:00:00Z");
    for (int i = 0; i < 5; i++) {
      store.record(new Entry(1L, base.plusSeconds(i), "e" + i));
    }

    assertThat(store.findAll()).extracting(Entry::label).containsExactly("e4", "e3", "e2");
  }

  @Test
  void clearRemovesAllEntries() {
    BoundedInMemoryStore<Entry> store = newStore(10);
    store.record(new Entry(1L, Instant.now(), "x"));
    store.clear();
    assertThat(store.findAll()).isEmpty();
  }
}
