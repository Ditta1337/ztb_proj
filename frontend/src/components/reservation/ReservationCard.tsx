import { Link } from 'react-router-dom';

import { RESERVATION_STATUS_LABELS } from '../../constants/labels';
import type { Reservation } from '../../types/api';
import { formatDateTime, formatPrice } from '../../utils/formatters';
import { Badge } from '../ui/Badge';
import { Button } from '../ui/Button';
import styles from './ReservationCard.module.css';

interface ReservationCardProps {
  reservation: Reservation;
  cancelling: boolean;
  onCancel: (reservation: Reservation) => void;
}

export function ReservationCard({ reservation, cancelling, onCancel }: ReservationCardProps) {
  const active = reservation.status === 'CONFIRMED';

  return (
    <article className={`${styles.card} ${active ? '' : styles.inactive}`}>
      <div className={styles.seat} aria-label={`Rząd ${reservation.rowLabel}, miejsce ${reservation.seatNumber}`}>
        <span className={styles.seatRow}>Rząd {reservation.rowLabel}</span>
        <span className={styles.seatNumber}>{reservation.seatNumber}</span>
      </div>

      <div className={styles.body}>
        <div className={styles.badges}>
          <Badge tone={active ? 'success' : 'muted'}>{RESERVATION_STATUS_LABELS[reservation.status]}</Badge>
        </div>
        <Link to={`/events/${reservation.eventId}`} className={styles.title}>
          {reservation.eventTitle}
        </Link>
        <p className={styles.meta}>
          {formatDateTime(reservation.eventStartsAt)} · {reservation.venueName}
        </p>
        <p className={styles.meta}>
          Zarezerwowano: {formatDateTime(reservation.createdAt)}
          {reservation.cancelledAt && ` · anulowano: ${formatDateTime(reservation.cancelledAt)}`}
        </p>
      </div>

      <div className={styles.actions}>
        <span className={styles.price}>{formatPrice(reservation.price)}</span>
        {active && (
          <Button variant="secondary" disabled={cancelling} onClick={() => onCancel(reservation)}>
            {cancelling ? 'Anuluję…' : 'Anuluj'}
          </Button>
        )}
      </div>
    </article>
  );
}
