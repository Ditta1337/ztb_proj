import styles from './GridLogo.module.css';

const GRID_SIZE = 4;
const HIGHLIGHTED_CELL = 6;

export function GridLogo() {
  return (
    <span className={styles.logo} aria-hidden="true">
      {Array.from({ length: GRID_SIZE * GRID_SIZE }, (_, index) => (
        <span key={index} className={index === HIGHLIGHTED_CELL ? styles.highlighted : styles.cell} />
      ))}
    </span>
  );
}
