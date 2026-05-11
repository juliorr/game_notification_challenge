import { useMockEmails } from '../hooks/useMockEmails';
import { MessageCard } from './MessageCard';

interface MockMailboxPanelProps {
  userId: number;
}

export function MockMailboxPanel({ userId }: MockMailboxPanelProps) {
  const { emails, loading, error, refresh, clear } = useMockEmails(userId);

  return (
    <section className="panel">
      <h2>Mock mailbox (user {userId})</h2>
      <p style={{ color: '#64748b', marginTop: 0 }}>
        Friend request and friend accepted events also generate a simulated email. Real SMTP is not
        used; emails are kept in memory and exposed via <code>GET /api/mock-emails</code>.
      </p>
      <div className="row" style={{ marginBottom: 12 }}>
        <button className="secondary" onClick={() => void refresh()} disabled={loading}>
          {loading ? 'Refreshing…' : 'Refresh'}
        </button>
        <button className="secondary" onClick={() => void clear()} disabled={emails.length === 0}>
          Clear mailbox
        </button>
      </div>
      {error && <p style={{ color: '#b91c1c' }}>{error}</p>}
      {emails.length === 0 && !loading && (
        <p style={{ color: '#64748b' }}>
          No mock emails yet — trigger a friend request from the Generate tab.
        </p>
      )}
      {emails.map((email) => (
        <MessageCard
          key={email.id}
          title={email.subject}
          body={email.body}
          recipientLabel="To"
          recipient={email.to}
          sentAt={email.sentAt}
          sourceNotificationId={email.sourceNotificationId}
        />
      ))}
    </section>
  );
}
