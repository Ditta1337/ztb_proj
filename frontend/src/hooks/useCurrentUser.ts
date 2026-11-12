import { useContext } from 'react';

import { CurrentUserContext, type CurrentUserContextValue } from '../context/CurrentUserContext';

export function useCurrentUser(): CurrentUserContextValue {
  const context = useContext(CurrentUserContext);
  if (!context) {
    throw new Error('useCurrentUser must be used inside <CurrentUserProvider>');
  }
  return context;
}
