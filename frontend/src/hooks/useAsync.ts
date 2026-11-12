import { useCallback, useEffect, useState } from 'react';

interface UseAsyncResult<T> {
  data: T | null;
  error: Error | null;
  loading: boolean;
  reload: () => void;
}

interface SettledRequest<T> {
  loader: () => Promise<T>;
  reloadToken: number;
  data: T | null;
  error: Error | null;
}

const toError = (error: unknown) => (error instanceof Error ? error : new Error(String(error)));

export function useAsync<T>(loader: () => Promise<T>): UseAsyncResult<T> {
  const [reloadToken, setReloadToken] = useState(0);
  const [settled, setSettled] = useState<SettledRequest<T> | null>(null);

  useEffect(() => {
    let cancelled = false;
    loader()
      .then((data) => {
        if (!cancelled) setSettled({ loader, reloadToken, data, error: null });
      })
      .catch((error: unknown) => {
        if (!cancelled) setSettled({ loader, reloadToken, data: null, error: toError(error) });
      });
    return () => {
      cancelled = true;
    };
  }, [loader, reloadToken]);

  const reload = useCallback(() => setReloadToken((token) => token + 1), []);

  const isCurrent = settled !== null && settled.loader === loader && settled.reloadToken === reloadToken;
  return {
    data: isCurrent ? settled.data : null,
    error: isCurrent ? settled.error : null,
    loading: !isCurrent,
    reload,
  };
}
