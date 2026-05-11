export type NotificationCategory = 'GAME' | 'SOCIAL';

export type NotificationType =
  | 'LEVEL_UP'
  | 'ITEM_ACQUIRED'
  | 'CHALLENGE_COMPLETED'
  | 'PVP_DEFEATED'
  | 'PVP_ATTACKED'
  | 'FRIEND_REQUEST'
  | 'FRIEND_ACCEPTED'
  | 'NEW_FOLLOWER';

export interface Notification {
  id: string;
  userId: number;
  type: NotificationType;
  category: NotificationCategory;
  message: string;
  timestamp: string;
  readAt: string | null;
}

export type DeliveryChannel = 'IN_APP' | 'EMAIL' | 'PUSH';

export interface UserPreferences {
  notificationsEnabled: boolean;
  enabledCategories: NotificationCategory[];
  enabledChannels: DeliveryChannel[];
}

export interface MockEmail {
  id: string;
  userId: number;
  to: string;
  subject: string;
  body: string;
  sentAt: string;
  sourceNotificationId: string;
}

export interface MockPushNotification {
  id: string;
  userId: number;
  deviceToken: string;
  title: string;
  body: string;
  sentAt: string;
  sourceNotificationId: string;
}
