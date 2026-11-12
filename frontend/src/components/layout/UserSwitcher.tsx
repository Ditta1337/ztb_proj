import { useId } from 'react';

import { useCurrentUser } from '../../hooks/useCurrentUser';
import styles from './UserSwitcher.module.css';

export function UserSwitcher() {
  const { users, currentUser, selectUser, loading, error } = useCurrentUser();
  const selectId = useId();

  if (error) {
    return <span className={styles.offline}>Brak połączenia z API</span>;
  }

  return (
    <div className={styles.switcher}>
      <label htmlFor={selectId} className={styles.label}>
        Użytkownik
      </label>
      <select
        id={selectId}
        className={styles.select}
        value={currentUser?.id ?? ''}
        disabled={loading}
        onChange={(event) => selectUser(Number(event.target.value))}
      >
        {users.map((user) => (
          <option key={user.id} value={user.id}>
            {user.firstName} {user.lastName}
          </option>
        ))}
      </select>
    </div>
  );
}
