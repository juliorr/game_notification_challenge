import { simulator } from '../lib/api';

interface EventTriggerPanelProps {
  userId: number;
}

const SECTION_LABEL_WIDTH_PX = 80;
const ROW_GAP_PX = 8;
const COUNTERPART_USER_ID = 99;
const SAMPLE_ITEM = 'Sword of Azeroth';
const SAMPLE_CHALLENGE = 'Dragon Slayer';

export function EventTriggerPanel({ userId }: EventTriggerPanelProps) {
  return (
    <section className="panel">
      <h2>Trigger events for user {userId}</h2>
      <div className="row" style={{ marginBottom: ROW_GAP_PX }}>
        <strong style={{ width: SECTION_LABEL_WIDTH_PX }}>Game</strong>
        <button onClick={() => void simulator.levelUp(userId)}>Level Up</button>
        <button onClick={() => void simulator.item(userId, SAMPLE_ITEM)}>Item Acquired</button>
        <button onClick={() => void simulator.challenge(userId, SAMPLE_CHALLENGE)}>
          Challenge Completed
        </button>
        <button onClick={() => void simulator.pvpAttack(userId, COUNTERPART_USER_ID)}>
          PvP Attacked
        </button>
        <button onClick={() => void simulator.pvpDefeated(userId, COUNTERPART_USER_ID)}>
          PvP Defeated
        </button>
      </div>
      <div className="row">
        <strong style={{ width: SECTION_LABEL_WIDTH_PX }}>Social</strong>
        <button
          className="social"
          onClick={() => void simulator.friendRequest(COUNTERPART_USER_ID, userId)}
        >
          Friend Request
        </button>
        <button
          className="social"
          onClick={() => void simulator.friendAccept(COUNTERPART_USER_ID, userId)}
        >
          Friend Accepted
        </button>
        <button
          className="social"
          onClick={() => void simulator.follow(COUNTERPART_USER_ID, userId)}
        >
          New Follower
        </button>
      </div>
    </section>
  );
}
