import { useCallback, useMemo, useState, type ReactNode } from 'react';

import { usersApi } from '../api/usersApi';
import { useAsync } from '../hooks/useAsync';
import { CurrentUserContext, type CurrentUserContextValue } from './CurrentUserContext';

const STORAGE_KEY = 'seat-reservation.currentUserId';

function readStoredUserId(): number | null {
  try {
    const stored = window.localStorage.getItem(STORAGE_KEY);
    return stored ? Number(stored) : null;
  } catch {
    return null;
  }
}

function tryStoreUserId(userId: number): boolean {
  try {
    window.localStorage.setItem(STORAGE_KEY, String(userId));
    return true;
  } catch {
    return false;
  }
}

export function CurrentUserProvider({ children }: { children: ReactNode }) {
  const { data: users, loading, error } = useAsync(usersApi.getUsers);
  const [selectedUserId, setSelectedUserId] = useState<number | null>(readStoredUserId);

  const selectUser = useCallback((userId: number) => {
    setSelectedUserId(userId);
    tryStoreUserId(userId);
  }, []);

  const value = useMemo<CurrentUserContextValue>(() => {
    const allUsers = users ?? [];
    const currentUser = allUsers.find((user) => user.id === selectedUserId) ?? allUsers[0] ?? null;
    return { users: allUsers, currentUser, selectUser, loading, error };
  }, [users, selectedUserId, selectUser, loading, error]);

  return <CurrentUserContext.Provider value={value}>{children}</CurrentUserContext.Provider>;
}
