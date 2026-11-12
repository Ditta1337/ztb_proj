import styles from './Spinner.module.css';

export function Spinner({ label = 'Ładowanie…' }: { label?: string }) {
  return (
    <div className={styles.wrapper} role="status">
      <div className={styles.grid} aria-hidden="true">
        {Array.from({ length: 9 }, (_, index) => (
          <span key={index} className={styles.cell} style={{ animationDelay: `${index * 80}ms` }} />
        ))}
      </div>
      <span className={styles.label}>{label}</span>
    </div>
  );
}
