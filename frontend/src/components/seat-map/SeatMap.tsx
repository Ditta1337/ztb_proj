import { Fragment, useMemo } from 'react';

import { COMPACT_SEAT_MAP_THRESHOLD, SEAT_MAP_LAYOUTS } from '../../constants/seatMap';
import type { EventSeat, VenueType } from '../../types/api';
import { groupSeatsByRow } from '../../utils/seatRows';
import { SeatButton } from './SeatButton';
import { SeatLegend } from './SeatLegend';
import styles from './SeatMap.module.css';

interface SeatMapProps {
  seats: EventSeat[];
  venueType: VenueType;
  selectedSeatIds: ReadonlySet<number>;
  selectionLimitReached: boolean;
  onToggleSeat: (seat: EventSeat) => void;
}

export function SeatMap({ seats, venueType, selectedSeatIds, selectionLimitReached, onToggleSeat }: SeatMapProps) {
  const rows = useMemo(() => groupSeatsByRow(seats), [seats]);
  const { frontLabel, aisleAfterSeat } = SEAT_MAP_LAYOUTS[venueType];
  const widestRow = Math.max(0, ...rows.map((row) => row.seats.length));
  const compact = widestRow > COMPACT_SEAT_MAP_THRESHOLD;

  return (
    <section className={styles.wrapper} aria-label="Mapa miejsc">
      <div className={`${styles.scroll} ${compact ? styles.compact : ''}`}>
        <div className={styles.map}>
          <div className={styles.front}>{frontLabel}</div>
          {rows.map((row) => (
            <div key={row.label} className={styles.row}>
              <span className={styles.rowLabel}>{row.label}</span>
              <div className={styles.rowSeats}>
                {row.seats.map((seat) => {
                  const selected = selectedSeatIds.has(seat.id);
                  return (
                    <Fragment key={seat.id}>
                      <SeatButton
                        seat={seat}
                        selected={selected}
                        disabled={selectionLimitReached && !selected}
                        onToggle={onToggleSeat}
                      />
                      {seat.seatNumber === aisleAfterSeat && <span className={styles.aisle} aria-hidden="true" />}
                    </Fragment>
                  );
                })}
              </div>
              <span className={styles.rowLabel}>{row.label}</span>
            </div>
          ))}
        </div>
      </div>
      <SeatLegend />
    </section>
  );
}
