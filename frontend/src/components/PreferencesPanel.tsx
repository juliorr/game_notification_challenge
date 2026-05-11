import { useEffect, useState } from 'react';
import { getPreferences, updatePreferences } from '../lib/api';
import { toggleInList } from '../lib/collections';
import type { DeliveryChannel, NotificationCategory, UserPreferences } from '../types';

interface PreferencesPanelProps {
  userId: number;
}

const ALL_CATEGORIES: NotificationCategory[] = ['GAME', 'SOCIAL'];
const ALL_CHANNELS: DeliveryChannel[] = ['IN_APP', 'EMAIL', 'PUSH'];

const CHANNEL_LABEL: Record<DeliveryChannel, string> = {
  IN_APP: 'In-app',
  EMAIL: 'Email',
  PUSH: 'Push',
};

const DEFAULT_PREFERENCES: UserPreferences = {
  notificationsEnabled: true,
  enabledCategories: ALL_CATEGORIES,
  enabledChannels: ALL_CHANNELS,
};

export function PreferencesPanel({ userId }: PreferencesPanelProps) {
  const [preferences, setPreferences] = useState<UserPreferences>(DEFAULT_PREFERENCES);

  useEffect(() => {
    void getPreferences(userId).then(setPreferences);
  }, [userId]);

  async function applyPatch(patch: Partial<UserPreferences>): Promise<void> {
    const updated = await updatePreferences(userId, patch);
    setPreferences(updated);
  }

  const notificationsDisabled = !preferences.notificationsEnabled;

  return (
    <section className="panel">
      <h2>Preferences for user {userId}</h2>

      <div className="row" style={{ marginBottom: 12 }}>
        <label>
          <input
            type="checkbox"
            checked={preferences.notificationsEnabled}
            onChange={(event) =>
              void applyPatch({ notificationsEnabled: event.target.checked })
            }
          />
          <strong>Receive notifications</strong>
        </label>
      </div>

      <fieldset
        disabled={notificationsDisabled}
        style={{ border: 0, padding: 0, opacity: notificationsDisabled ? 0.5 : 1 }}
      >
        <h3 style={{ margin: '8px 0 4px' }}>Categories</h3>
        <div className="row">
          {ALL_CATEGORIES.map((category) => (
            <label key={category}>
              <input
                type="checkbox"
                checked={preferences.enabledCategories.includes(category)}
                onChange={() =>
                  void applyPatch({
                    enabledCategories: toggleInList(preferences.enabledCategories, category),
                  })
                }
              />
              {category}
            </label>
          ))}
        </div>

        <h3 style={{ margin: '12px 0 4px' }}>Channels</h3>
        <div className="row">
          {ALL_CHANNELS.map((channel) => (
            <label key={channel}>
              <input
                type="checkbox"
                checked={preferences.enabledChannels.includes(channel)}
                onChange={() =>
                  void applyPatch({
                    enabledChannels: toggleInList(preferences.enabledChannels, channel),
                  })
                }
              />
              {CHANNEL_LABEL[channel]}
            </label>
          ))}
        </div>
      </fieldset>
    </section>
  );
}
