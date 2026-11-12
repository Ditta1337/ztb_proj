import { createContext } from 'react';

import type { User } from '../types/api';

export interface CurrentUserContextValue {
  users: User[];
  currentUser: User | null;
  selectUser: (userId: number) => void;
  loading: boolean;
  error: Error | null;
}

export const CurrentUserContext = createContext<CurrentUserContextValue | null>(null);
