import styles from './OccupancyBar.module.css';

interface OccupancyBarProps {
  available: number;
  total: number;
}

const LOW_AVAILABILITY_RATIO = 0.15;

export function OccupancyBar({ available, total }: OccupancyBarProps) {
  const takenPercent = total === 0 ? 100 : Math.round(((total - available) / total) * 100);
  const lowAvailability = total > 0 && available / total <= LOW_AVAILABILITY_RATIO;

  return (
    <div className={styles.wrapper}>
      <div
        className={styles.track}
        role="meter"
        aria-valuemin={0}
        aria-valuemax={100}
        aria-valuenow={takenPercent}
        aria-label="Zajętość miejsc"
      >
        <div
          className={`${styles.fill} ${lowAvailability ? styles.low : ''}`}
          style={{ width: `${takenPercent}%` }}
        />
      </div>
      <span className={styles.caption}>
        Wolne miejsca: <strong>{available}</strong> z {total}
      </span>
    </div>
  );
}
