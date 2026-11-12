import styles from './SeatMap.module.css';

const LEGEND_ITEMS = [
  { className: '', label: 'Wolne' },
  { className: styles.selected, label: 'Wybrane' },
  { className: styles.reserved, label: 'Zajęte' },
  { className: styles.vip, label: 'VIP' },
  { className: styles.accessible, label: 'Dostępne dla niepełnosprawnych' },
];

export function SeatLegend() {
  return (
    <ul className={styles.legend}>
      {LEGEND_ITEMS.map((item) => (
        <li key={item.label} className={styles.legendItem}>
          <span className={`${styles.seat} ${styles.legendSwatch} ${item.className}`} aria-hidden="true" />
          {item.label}
        </li>
      ))}
    </ul>
  );
}
