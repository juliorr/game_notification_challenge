package com.globalli.notifications.events;

public sealed interface GameEvent
    permits PlayerLeveledUp,
        ItemAcquired,
        ChallengeCompleted,
        PlayerDefeatedInPvp,
        PlayerAttackedInPvp {

  long userId();
}
