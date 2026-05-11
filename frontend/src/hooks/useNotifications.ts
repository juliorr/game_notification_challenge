import { useCallback, useEffect, useRef, useState } from 'react';
import { Client, type IMessage } from '@stomp/stompjs';
import {
  clearNotifications,
  getNotifications,
  getUnreadCount,
  markAllRead,
  markRead,
} from '../lib/api';
import type { Notification } from '../types';

interface ReadStatusPayload {
  notificationId: string;
  readAt: string;
}

interface ReadAllStatusPayload {
  readAt: string;
}

interface UseNotificationsResult {
  notifications: Notification[];
  unreadCount: number;
  connected: boolean;
  error: unknown;
  clear: () => Promise<void>;
  markAllRead: () => Promise<void>;
  markRead: (notificationId: string) => Promise<void>;
}

const USER_ID_DEBOUNCE_MS = 400;
const RECONNECT_DELAY_MS = 2000;

interface SubscriptionHandlers {
  onIncoming: (notification: Notification) => void;
  onCleared: () => void;
  onReadOne: (payload: ReadStatusPayload) => void;
  onReadAll: (payload: ReadAllStatusPayload) => void;
}

function buildBrokerUrl(): string {
  const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws';
  return `${protocol}://${window.location.host}/ws`;
}

function subscribeAll(client: Client, userId: number, handlers: SubscriptionHandlers): void {
  client.subscribe(`/topic/notifications/${userId}`, (frame: IMessage) => {
    handlers.onIncoming(JSON.parse(frame.body) as Notification);
  });
  client.subscribe(`/topic/notifications/${userId}/cleared`, () => {
    handlers.onCleared();
  });
  client.subscribe(`/topic/notifications/${userId}/read`, (frame: IMessage) => {
    handlers.onReadOne(JSON.parse(frame.body) as ReadStatusPayload);
  });
  client.subscribe(`/topic/notifications/${userId}/read-all`, (frame: IMessage) => {
    handlers.onReadAll(JSON.parse(frame.body) as ReadAllStatusPayload);
  });
}

export function useNotifications(userId: number): UseNotificationsResult {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [connected, setConnected] = useState(false);
  const [error, setError] = useState<unknown>(null);
  const [activeUserId, setActiveUserId] = useState(userId);
  const clientRef = useRef<Client | null>(null);

  useEffect(() => {
    const handle = window.setTimeout(() => setActiveUserId(userId), USER_ID_DEBOUNCE_MS);
    return () => window.clearTimeout(handle);
  }, [userId]);

  const loadInitialUnread = useCallback(async (uid: number) => {
    try {
      const count = await getUnreadCount(uid);
      setUnreadCount(count);
    } catch (cause) {
      setError(cause);
    }
  }, []);

  const handleIncoming = useCallback((incoming: Notification) => {
    setNotifications((current) =>
      current.some((n) => n.id === incoming.id) ? current : [incoming, ...current],
    );
    if (!incoming.readAt) {
      setUnreadCount((count) => count + 1);
    }
  }, []);

  const handleCleared = useCallback(() => {
    setNotifications([]);
    setUnreadCount(0);
  }, []);

  const handleReadOne = useCallback((payload: ReadStatusPayload) => {
    setNotifications((current) => {
      let changed = false;
      const next = current.map((n) => {
        if (n.id === payload.notificationId && !n.readAt) {
          changed = true;
          return { ...n, readAt: payload.readAt };
        }
        return n;
      });
      if (changed) {
        setUnreadCount((count) => Math.max(0, count - 1));
      }
      return next;
    });
  }, []);

  const handleReadAll = useCallback((payload: ReadAllStatusPayload) => {
    setNotifications((current) =>
      current.map((n) => (n.readAt ? n : { ...n, readAt: payload.readAt })),
    );
    setUnreadCount(0);
  }, []);

  useEffect(() => {
    let cancelled = false;
    setNotifications([]);
    setUnreadCount(0);
    setError(null);

    getNotifications(activeUserId)
      .then((history) => {
        if (!cancelled) setNotifications(history);
      })
      .catch((cause) => {
        if (!cancelled) setError(cause);
      });
    void loadInitialUnread(activeUserId);

    const client = new Client({
      brokerURL: buildBrokerUrl(),
      reconnectDelay: RECONNECT_DELAY_MS,
      onConnect: () => {
        setConnected(true);
        subscribeAll(client, activeUserId, {
          onIncoming: handleIncoming,
          onCleared: handleCleared,
          onReadOne: handleReadOne,
          onReadAll: handleReadAll,
        });
      },
      onDisconnect: () => setConnected(false),
      onStompError: () => setConnected(false),
      onWebSocketClose: () => setConnected(false),
    });

    client.activate();
    clientRef.current = client;

    return () => {
      cancelled = true;
      void client.deactivate();
      clientRef.current = null;
    };
  }, [activeUserId, loadInitialUnread, handleIncoming, handleCleared, handleReadOne, handleReadAll]);

  const handleMarkAllRead = useCallback(async () => {
    await markAllRead(activeUserId);
    const now = new Date().toISOString();
    setNotifications((current) =>
      current.map((n) => (n.readAt ? n : { ...n, readAt: now })),
    );
    setUnreadCount(0);
  }, [activeUserId]);

  const handleMarkRead = useCallback(
    async (notificationId: string) => {
      const target = notifications.find((n) => n.id === notificationId);
      if (!target || target.readAt) return;
      await markRead(activeUserId, notificationId);
      const now = new Date().toISOString();
      setNotifications((current) =>
        current.map((n) => (n.id === notificationId && !n.readAt ? { ...n, readAt: now } : n)),
      );
      setUnreadCount((count) => Math.max(0, count - 1));
    },
    [activeUserId, notifications],
  );

  const handleClear = useCallback(async () => {
    await clearNotifications(activeUserId);
    setNotifications([]);
    setUnreadCount(0);
  }, [activeUserId]);

  return {
    notifications,
    unreadCount,
    connected,
    error,
    clear: handleClear,
    markAllRead: handleMarkAllRead,
    markRead: handleMarkRead,
  };
}
