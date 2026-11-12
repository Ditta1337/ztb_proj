import { MAX_SEATS_PER_RESERVATION } from '../../constants/seatMap';
import type { EventSeat, User } from '../../types/api';
import { formatAmount, formatPrice, pluralizeSeats } from '../../utils/formatters';
import { Button } from '../ui/Button';
import styles from './ReservationSummary.module.css';

interface ReservationSummaryProps {
  selectedSeats: EventSeat[];
  currentUser: User | null;
  submitting: boolean;
  onRemoveSeat: (seat: EventSeat) => void;
  onClear: () => void;
  onSubmit: () => void;
}

export function ReservationSummary({
  selectedSeats,
  currentUser,
  submitting,
  onRemoveSeat,
  onClear,
  onSubmit,
}: ReservationSummaryProps) {
  const total = selectedSeats.reduce((sum, seat) => sum + seat.price, 0);
  const hasSelection = selectedSeats.length > 0;
  const canSubmit = hasSelection && currentUser !== null && !submitting;

  return (
    <aside className={styles.summary} aria-label="Podsumowanie rezerwacji">
      <div className={styles.header}>
        <h2 className={styles.heading}>Twoja rezerwacja</h2>
        {hasSelection && (
          <button type="button" className={styles.clear} onClick={onClear}>
            Wyczyść
          </button>
        )}
      </div>

      <p className={styles.user}>
        {currentUser ? (
          <>
            Rezerwujesz jako <strong>{currentUser.firstName} {currentUser.lastName}</strong>
          </>
        ) : (
          'Wybierz użytkownika w nagłówku.'
        )}
      </p>

      {hasSelection ? (
        <ul className={styles.seatList}>
          {selectedSeats.map((seat) => (
            <li key={seat.id} className={styles.seatItem}>
              <span>
                Rząd <strong>{seat.rowLabel}</strong>, miejsce <strong>{seat.seatNumber}</strong>
              </span>
              <span className={styles.seatPrice}>{formatPrice(seat.price)}</span>
              <button
                type="button"
                className={styles.remove}
                onClick={() => onRemoveSeat(seat)}
                aria-label={`Usuń rząd ${seat.rowLabel}, miejsce ${seat.seatNumber}`}
              >
                ×
              </button>
            </li>
          ))}
        </ul>
      ) : (
        <p className={styles.placeholder}>
          Kliknij wolne miejsca na mapie (maks. {MAX_SEATS_PER_RESERVATION}).
        </p>
      )}

      <div className={styles.total}>
        <span>Razem</span>
        <strong>{formatAmount(total)}</strong>
      </div>

      <Button size="lg" fullWidth disabled={!canSubmit} onClick={onSubmit}>
        {submitting
          ? 'Rezerwuję…'
          : hasSelection
            ? `Zarezerwuj ${selectedSeats.length} ${pluralizeSeats(selectedSeats.length)}`
            : 'Zarezerwuj'}
      </Button>
    </aside>
  );
}
