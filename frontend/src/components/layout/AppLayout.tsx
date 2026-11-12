import { Outlet } from 'react-router-dom';

import styles from './AppLayout.module.css';
import { Header } from './Header';

export function AppLayout() {
  return (
    <div className={styles.shell}>
      <div className={styles.demoBanner} role="note">
        Tryb demo – dane pochodzą z mocków, rezerwacje nie są zapisywane w bazie.
      </div>
      <Header />
      <main className={styles.main}>
        <Outlet />
      </main>
      <footer className={styles.footer}>
        <div className="container">
          System rezerwacji miejsc · projekt z baz danych · PostgreSQL & współbieżność
        </div>
      </footer>
    </div>
  );
}
