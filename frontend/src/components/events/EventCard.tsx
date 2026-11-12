import { Link } from 'react-router-dom';

import { EVENT_CATEGORY_LABELS } from '../../constants/labels';
import type { EventSummary } from '../../types/api';
import { formatDay, formatMonthShort, formatPrice, formatTime } from '../../utils/formatters';
import { Badge } from '../ui/Badge';
import styles from './EventCard.module.css';
import { OccupancyBar } from './OccupancyBar';

export function EventCard({ event }: { event: EventSummary }) {
  const soldOut = event.availableSeats === 0;

  return (
    <Link to={`/events/${event.id}`} className={styles.card}>
      <div className={styles.date}>
        <span className={styles.day}>{formatDay(event.startsAt)}</span>
        <span className={styles.month}>{formatMonthShort(event.startsAt)}</span>
      </div>

      <div className={styles.body}>
        <div className={styles.badges}>
          <Badge>{EVENT_CATEGORY_LABELS[event.category]}</Badge>
          {soldOut && <Badge tone="dark">Wyprzedane</Badge>}
        </div>
        <h3 className={styles.title}>{event.title}</h3>
        <p className={styles.meta}>
          {formatTime(event.startsAt)} · {event.venueName}, {event.city}
        </p>
        <OccupancyBar available={event.availableSeats} total={event.totalSeats} />
      </div>

      <div className={styles.footer}>
        <span className={styles.price}>
          {event.minPrice > 0 && <small>od </small>}
          {formatPrice(event.minPrice)}
        </span>
        <span className={styles.cta}>Wybierz miejsca →</span>
      </div>
    </Link>
  );
}
