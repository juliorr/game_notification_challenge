import { useCallback, useEffect, useState } from 'react';
import { clearMockPush, getErrorMessage, getMockPush } from '../lib/api';
import type { MockPushNotification } from '../types';

const POLL_INTERVAL_MS = 3000;

interface UseMockPushResult {
  pushes: MockPushNotification[];
  loading: boolean;
  error: string | null;
  refresh: () => Promise<void>;
  clear: () => Promise<void>;
}

export function useMockPush(userId: number): UseMockPushResult {
  const [pushes, setPushes] = useState<MockPushNotification[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    setLoading(true);
    try {
      const next = await getMockPush(userId);
      setPushes(next);
      setError(null);
    } catch (cause) {
      setError(getErrorMessage(cause));
    } finally {
      setLoading(false);
    }
  }, [userId]);

  const clear = useCallback(async () => {
    await clearMockPush();
    await refresh();
  }, [refresh]);

  useEffect(() => {
    void refresh();
    const handle = window.setInterval(() => {
      void refresh();
    }, POLL_INTERVAL_MS);
    return () => window.clearInterval(handle);
  }, [refresh]);

  return { pushes, loading, error, refresh, clear };
}
