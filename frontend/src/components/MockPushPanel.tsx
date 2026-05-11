import { useMockPush } from '../hooks/useMockPush';
import { MessageCard } from './MessageCard';

interface MockPushPanelProps {
  userId: number;
}

export function MockPushPanel({ userId }: MockPushPanelProps) {
  const { pushes, loading, error, refresh, clear } = useMockPush(userId);

  return (
    <section className="panel">
      <h2>Mock push outbox (user {userId})</h2>
      <p style={{ color: '#64748b', marginTop: 0 }}>
        Friend request, friend accepted and new follower events also generate a simulated push
        notification. No real push service is used; pushes are kept in memory and exposed via{' '}
        <code>GET /api/mock-push</code>.
      </p>
      <div className="row" style={{ marginBottom: 12 }}>
        <button className="secondary" onClick={() => void refresh()} disabled={loading}>
          {loading ? 'Refreshing…' : 'Refresh'}
        </button>
        <button className="secondary" onClick={() => void clear()} disabled={pushes.length === 0}>
          Clear outbox
        </button>
      </div>
      {error && <p style={{ color: '#b91c1c' }}>{error}</p>}
      {pushes.length === 0 && !loading && (
        <p style={{ color: '#64748b' }}>
          No mock push notifications yet — trigger a friend request, friend accept or follow from
          the Generate tab.
        </p>
      )}
      {pushes.map((push) => (
        <MessageCard
          key={push.id}
          title={push.title}
          body={push.body}
          recipientLabel="Device"
          recipient={push.deviceToken}
          sentAt={push.sentAt}
          sourceNotificationId={push.sourceNotificationId}
        />
      ))}
    </section>
  );
}
