package com.globalli.notifications.api;

import com.globalli.notifications.api.dto.ChallengeRequest;
import com.globalli.notifications.api.dto.FollowRequest;
import com.globalli.notifications.api.dto.FriendRequestBody;
import com.globalli.notifications.api.dto.ItemRequest;
import com.globalli.notifications.api.dto.LevelUpRequest;
import com.globalli.notifications.api.dto.PvpRequest;
import com.globalli.notifications.simulators.GameEngine;
import com.globalli.notifications.simulators.SocialSystem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sim")
@Tag(
    name = "Simulator",
    description = "Endpoints that simulate game and social domain events for local testing")
public class SimulatorController {

  private final GameEngine gameEngine;
  private final SocialSystem socialSystem;

  public SimulatorController(GameEngine gameEngine, SocialSystem socialSystem) {
    this.gameEngine = gameEngine;
    this.socialSystem = socialSystem;
  }

  @PostMapping("/level-up")
  @Operation(
      summary = "Simulate a player leveling up.",
      description = "Publishes a PlayerLeveledUp domain event for the given user.")
  public ResponseEntity<Void> levelUp(@Valid @RequestBody LevelUpRequest body) {
    gameEngine.playerLeveledUp(body.userId());
    return accepted();
  }

  @PostMapping("/item")
  @Operation(
      summary = "Simulate a player acquiring an item.",
      description = "Publishes an ItemAcquired domain event for the given user and item.")
  public ResponseEntity<Void> item(@Valid @RequestBody ItemRequest body) {
    gameEngine.itemAcquired(body.userId(), body.item());
    return accepted();
  }

  @PostMapping("/challenge")
  @Operation(
      summary = "Simulate a player completing a challenge.",
      description =
          "Publishes a ChallengeCompleted domain event for the given user and challenge.")
  public ResponseEntity<Void> challenge(@Valid @RequestBody ChallengeRequest body) {
    gameEngine.challengeCompleted(body.userId(), body.challenge());
    return accepted();
  }

  @PostMapping("/pvp/defeated")
  @Operation(
      summary = "Simulate a player being defeated in PvP by another player.",
      description =
          "Publishes a PlayerDefeatedInPvp domain event where userId is the defeated player and"
              + " attackerUserId is the winner.")
  public ResponseEntity<Void> pvpDefeated(@Valid @RequestBody PvpRequest body) {
    gameEngine.playerDefeatedInPvp(body.userId(), body.attackerUserId());
    return accepted();
  }

  @PostMapping("/pvp/attack")
  @Operation(
      summary = "Simulate a player being attacked in PvP by another player.",
      description =
          "Publishes a PlayerAttackedInPvp domain event where userId is the attacked player and"
              + " attackerUserId is the aggressor.")
  public ResponseEntity<Void> pvpAttack(@Valid @RequestBody PvpRequest body) {
    gameEngine.playerAttackedInPvp(body.userId(), body.attackerUserId());
    return accepted();
  }

  @PostMapping("/friend-request")
  @Operation(
      summary = "Simulate a friend request being sent.",
      description = "Publishes a FriendRequestSent domain event from fromUserId to toUserId.")
  public ResponseEntity<Void> friendRequest(@Valid @RequestBody FriendRequestBody body) {
    socialSystem.friendRequestSent(body.fromUserId(), body.toUserId());
    return accepted();
  }

  @PostMapping("/friend-accept")
  @Operation(
      summary = "Simulate a friend request being accepted.",
      description =
          "Publishes a FriendRequestAccepted domain event where fromUserId originally sent the"
              + " request and toUserId accepted it.")
  public ResponseEntity<Void> friendAccept(@Valid @RequestBody FriendRequestBody body) {
    socialSystem.friendRequestAccepted(body.fromUserId(), body.toUserId());
    return accepted();
  }

  @PostMapping("/follow")
  @Operation(
      summary = "Simulate a new follower.",
      description =
          "Publishes a NewFollower domain event where followerUserId starts following"
              + " followedUserId.")
  public ResponseEntity<Void> follow(@Valid @RequestBody FollowRequest body) {
    socialSystem.newFollower(body.followerUserId(), body.followedUserId());
    return accepted();
  }

  private static ResponseEntity<Void> accepted() {
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }
}
