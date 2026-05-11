import { useCallback, useEffect, useState } from 'react';
import { clearMockEmails, getErrorMessage, getMockEmails } from '../lib/api';
import type { MockEmail } from '../types';

const POLL_INTERVAL_MS = 3000;

interface UseMockEmailsResult {
  emails: MockEmail[];
  loading: boolean;
  error: string | null;
  refresh: () => Promise<void>;
  clear: () => Promise<void>;
}

export function useMockEmails(userId: number): UseMockEmailsResult {
  const [emails, setEmails] = useState<MockEmail[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    setLoading(true);
    try {
      const next = await getMockEmails(userId);
      setEmails(next);
      setError(null);
    } catch (cause) {
      setError(getErrorMessage(cause));
    } finally {
      setLoading(false);
    }
  }, [userId]);

  const clear = useCallback(async () => {
    await clearMockEmails();
    await refresh();
  }, [refresh]);

  useEffect(() => {
    void refresh();
    const handle = window.setInterval(() => {
      void refresh();
    }, POLL_INTERVAL_MS);
    return () => window.clearInterval(handle);
  }, [refresh]);

  return { emails, loading, error, refresh, clear };
}
