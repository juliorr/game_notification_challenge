import type { Notification } from '../types';

interface NotificationListProps {
  notifications: Notification[];
  connected: boolean;
  unreadCount: number;
  onClear: () => void;
  onMarkAllRead: () => void;
  onMarkRead: (notificationId: string) => void;
}

export function NotificationList({
  notifications,
  connected,
  unreadCount,
  onClear,
  onMarkAllRead,
  onMarkRead,
}: NotificationListProps) {
  return (
    <section className="panel">
      <h2>
        Notifications
        {unreadCount > 0 && <span className="badge unread">{unreadCount}</span>}
        <span className="connection" style={{ marginLeft: 12 }}>
          <span className={`dot ${connected ? 'on' : 'off'}`} />
          {connected ? 'connected' : 'disconnected'}
        </span>
      </h2>
      <div className="row" style={{ marginBottom: 12 }}>
        <button className="secondary" onClick={onMarkAllRead} disabled={unreadCount === 0}>
          Mark all read
        </button>
        <button className="secondary" onClick={onClear}>
          Clear
        </button>
      </div>
      {notifications.length === 0 && <p style={{ color: '#64748b' }}>No notifications yet.</p>}
      {notifications.map((notification) => {
        const isUnread = !notification.readAt;
        return (
          <div
            key={notification.id}
            className={`notif ${notification.category} ${isUnread ? 'unread' : 'read'}`}
            role={isUnread ? 'button' : undefined}
            tabIndex={isUnread ? 0 : undefined}
            onClick={isUnread ? () => onMarkRead(notification.id) : undefined}
            onKeyDown={
              isUnread
                ? (event) => {
                    if (event.key === 'Enter' || event.key === ' ') {
                      event.preventDefault();
                      onMarkRead(notification.id);
                    }
                  }
                : undefined
            }
          >
            <div className="notif-message">
              {isUnread && <span className="unread-dot" aria-hidden="true" />}
              {notification.message}
            </div>
            <div className="meta">
              {notification.type} · {new Date(notification.timestamp).toLocaleTimeString()}
              {notification.readAt && ` · read ${new Date(notification.readAt).toLocaleTimeString()}`}
            </div>
          </div>
        );
      })}
    </section>
  );
}
