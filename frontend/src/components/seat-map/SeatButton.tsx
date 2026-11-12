import { SEAT_CATEGORY_LABELS } from '../../constants/labels';
import type { EventSeat } from '../../types/api';
import { formatPrice, formatSeat } from '../../utils/formatters';
import styles from './SeatMap.module.css';

interface SeatButtonProps {
  seat: EventSeat;
  selected: boolean;
  disabled: boolean;
  onToggle: (seat: EventSeat) => void;
}

export function SeatButton({ seat, selected, disabled, onToggle }: SeatButtonProps) {
  const reserved = seat.status === 'RESERVED';
  const classes = [
    styles.seat,
    reserved && styles.reserved,
    selected && styles.selected,
    !reserved && seat.category === 'VIP' && styles.vip,
    !reserved && seat.category === 'ACCESSIBLE' && styles.accessible,
  ]
    .filter(Boolean)
    .join(' ');

  const stateLabel = reserved ? 'zajęte' : selected ? 'wybrane' : 'wolne';
  const description = `${formatSeat(seat.rowLabel, seat.seatNumber)}, ${SEAT_CATEGORY_LABELS[seat.category]}, ${formatPrice(seat.price)}, ${stateLabel}`;

  return (
    <button
      type="button"
      className={classes}
      disabled={reserved || disabled}
      aria-pressed={selected}
      aria-label={description}
      title={description}
      onClick={() => onToggle(seat)}
    >
      {seat.seatNumber}
    </button>
  );
}
