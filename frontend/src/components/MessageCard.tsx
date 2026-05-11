interface MessageCardProps {
  title: string;
  body: string;
  recipientLabel: string;
  recipient: string;
  sentAt: string;
  sourceNotificationId: string;
}

export function MessageCard({
  title,
  body,
  recipientLabel,
  recipient,
  sentAt,
  sourceNotificationId,
}: MessageCardProps) {
  return (
    <div className="notif SOCIAL read">
      <div className="notif-message">
        <strong>{title}</strong>
      </div>
      <div className="meta">
        {recipientLabel}: {recipient} · {new Date(sentAt).toLocaleString()}
      </div>
      <div style={{ marginTop: 6 }}>{body}</div>
      <div className="meta" style={{ marginTop: 6 }}>
        notification: {sourceNotificationId}
      </div>
    </div>
  );
}
