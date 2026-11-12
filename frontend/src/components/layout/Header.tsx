import { Link, NavLink } from 'react-router-dom';

import { GridLogo } from './GridLogo';
import styles from './Header.module.css';
import { UserSwitcher } from './UserSwitcher';

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  isActive ? `${styles.navLink} ${styles.active}` : styles.navLink;

export function Header() {
  return (
    <header className={styles.header}>
      <div className={`container ${styles.inner}`}>
        <Link to="/" className={styles.brand}>
          <GridLogo />
          <span className={styles.brandName}>Rezerwacja miejsc</span>
        </Link>
        <nav className={styles.nav} aria-label="Główna nawigacja">
          <NavLink to="/" end className={navLinkClass}>
            Wydarzenia
          </NavLink>
          <NavLink to="/my-reservations" className={navLinkClass}>
            Moje rezerwacje
          </NavLink>
        </nav>
        <UserSwitcher />
      </div>
    </header>
  );
}
