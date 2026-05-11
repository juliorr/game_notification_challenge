import { useState } from 'react';
import { NavLink, Navigate, Route, Routes } from 'react-router-dom';
import { UserSelector } from './components/UserSelector';
import { PreferencesPanel } from './components/PreferencesPanel';
import { EventTriggerPanel } from './components/EventTriggerPanel';
import { NotificationList } from './components/NotificationList';
import { MockMailboxPanel } from './components/MockMailboxPanel';
import { MockPushPanel } from './components/MockPushPanel';
import { useNotifications } from './hooks/useNotifications';

export function App() {
  const [userId, setUserId] = useState(1);
  const { notifications, unreadCount, connected, clear, markAllRead, markRead } =
    useNotifications(userId);

  return (
    <div className="app">
      <h1>Gaming Notifications</h1>
      <nav className="panel nav-tabs">
        <NavLink to="/generate">Generate</NavLink>
        <NavLink to="/notifications">Notifications</NavLink>
        <NavLink to="/mailbox">Mailbox</NavLink>
        <NavLink to="/push">Push</NavLink>
      </nav>
      <section className="panel">
        <h2>Session</h2>
        <UserSelector userId={userId} onChange={setUserId} />
      </section>
      <Routes>
        <Route path="/" element={<Navigate to="/generate" replace />} />
        <Route path="/generate" element={<EventTriggerPanel userId={userId} />} />
        <Route
          path="/notifications"
          element={
            <>
              <PreferencesPanel userId={userId} />
              <NotificationList
                notifications={notifications}
                connected={connected}
                unreadCount={unreadCount}
                onClear={clear}
                onMarkAllRead={markAllRead}
                onMarkRead={markRead}
              />
            </>
          }
        />
        <Route path="/mailbox" element={<MockMailboxPanel userId={userId} />} />
        <Route path="/push" element={<MockPushPanel userId={userId} />} />
      </Routes>
    </div>
  );
}
