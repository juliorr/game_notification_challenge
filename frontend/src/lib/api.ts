import type { MockEmail, MockPushNotification, Notification, UserPreferences } from '../types';

interface RequestOptions {
  method?: string;
  body?: unknown;
  failureMessage: string;
}

async function request<T>(path: string, options: RequestOptions): Promise<T> {
  const { method = 'GET', body, failureMessage } = options;
  const init: RequestInit = { method };
  if (body !== undefined) {
    init.headers = { 'Content-Type': 'application/json' };
    init.body = JSON.stringify(body);
  }
  const response = await fetch(path, init);
  if (!response.ok) {
    throw new Error(`${failureMessage} (${response.status})`);
  }
  if (response.status === 204) {
    return undefined as T;
  }
  const contentType = response.headers.get('content-type') ?? '';
  if (!contentType.includes('application/json')) {
    return undefined as T;
  }
  return (await response.json()) as T;
}

function postEvent(path: string, body: unknown): Promise<void> {
  return request<void>(path, { method: 'POST', body, failureMessage: `POST ${path} failed` });
}

export const simulator = {
  levelUp: (userId: number) => postEvent('/api/sim/level-up', { userId }),
  item: (userId: number, item: string) => postEvent('/api/sim/item', { userId, item }),
  challenge: (userId: number, challenge: string) =>
    postEvent('/api/sim/challenge', { userId, challenge }),
  pvpDefeated: (userId: number, attackerUserId: number) =>
    postEvent('/api/sim/pvp/defeated', { userId, attackerUserId }),
  pvpAttack: (userId: number, attackerUserId: number) =>
    postEvent('/api/sim/pvp/attack', { userId, attackerUserId }),
  friendRequest: (fromUserId: number, toUserId: number) =>
    postEvent('/api/sim/friend-request', { fromUserId, toUserId }),
  friendAccept: (fromUserId: number, toUserId: number) =>
    postEvent('/api/sim/friend-accept', { fromUserId, toUserId }),
  follow: (followerUserId: number, followedUserId: number) =>
    postEvent('/api/sim/follow', { followerUserId, followedUserId }),
};

export function getNotifications(userId: number): Promise<Notification[]> {
  return request<Notification[]>(`/api/users/${userId}/notifications`, {
    failureMessage: 'Failed to fetch notifications',
  });
}

export async function getUnreadCount(userId: number): Promise<number> {
  const body = await request<{ count: number }>(
    `/api/users/${userId}/notifications/unread-count`,
    { failureMessage: 'Failed to fetch unread count' },
  );
  return body.count;
}

export function markAllRead(userId: number): Promise<void> {
  return request<void>(`/api/users/${userId}/notifications/read`, {
    method: 'POST',
    failureMessage: 'Failed to mark notifications as read',
  });
}

export function markRead(userId: number, notificationId: string): Promise<void> {
  return request<void>(`/api/users/${userId}/notifications/${notificationId}/read`, {
    method: 'POST',
    failureMessage: 'Failed to mark notification as read',
  });
}

export function clearNotifications(userId: number): Promise<void> {
  return request<void>(`/api/users/${userId}/notifications`, {
    method: 'DELETE',
    failureMessage: 'Failed to clear notifications',
  });
}

export function getPreferences(userId: number): Promise<UserPreferences> {
  return request<UserPreferences>(`/api/users/${userId}/preferences`, {
    failureMessage: 'Failed to fetch preferences',
  });
}

export function updatePreferences(
  userId: number,
  patch: Partial<UserPreferences>,
): Promise<UserPreferences> {
  return request<UserPreferences>(`/api/users/${userId}/preferences`, {
    method: 'PUT',
    body: patch,
    failureMessage: 'Failed to update preferences',
  });
}

export function getMockEmails(userId?: number): Promise<MockEmail[]> {
  const path = userId == null ? '/api/mock-emails' : `/api/mock-emails?userId=${userId}`;
  return request<MockEmail[]>(path, { failureMessage: 'Failed to fetch mock emails' });
}

export function clearMockEmails(): Promise<void> {
  return request<void>('/api/mock-emails', {
    method: 'DELETE',
    failureMessage: 'Failed to clear mock emails',
  });
}

export function getMockPush(userId?: number): Promise<MockPushNotification[]> {
  const path = userId == null ? '/api/mock-push' : `/api/mock-push?userId=${userId}`;
  return request<MockPushNotification[]>(path, {
    failureMessage: 'Failed to fetch mock push notifications',
  });
}

export function clearMockPush(): Promise<void> {
  return request<void>('/api/mock-push', {
    method: 'DELETE',
    failureMessage: 'Failed to clear mock push notifications',
  });
}

export function getErrorMessage(cause: unknown): string {
  if (cause instanceof Error) return cause.message;
  if (typeof cause === 'string') return cause;
  return 'Unknown error';
}
