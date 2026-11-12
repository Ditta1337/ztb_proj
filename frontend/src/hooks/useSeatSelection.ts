import { useCallback, useMemo, useState } from 'react';

import type { EventSeat } from '../types/api';

export interface SeatSelection {
  selectedSeats: EventSeat[];
  selectedSeatIds: ReadonlySet<number>;
  limitReached: boolean;
  toggle: (seat: EventSeat) => void;
  remove: (seat: EventSeat) => void;
  clear: () => void;
}

export function useSeatSelection(maxSeats: number): SeatSelection {
  const [selectedSeats, setSelectedSeats] = useState<EventSeat[]>([]);

  const selectedSeatIds = useMemo(() => new Set(selectedSeats.map((seat) => seat.id)), [selectedSeats]);

  const remove = useCallback((seat: EventSeat) => {
    setSelectedSeats((current) => current.filter((selected) => selected.id !== seat.id));
  }, []);

  const toggle = useCallback(
    (seat: EventSeat) => {
      setSelectedSeats((current) => {
        if (current.some((selected) => selected.id === seat.id)) {
          return current.filter((selected) => selected.id !== seat.id);
        }
        return current.length >= maxSeats ? current : [...current, seat];
      });
    },
    [maxSeats],
  );

  const clear = useCallback(() => setSelectedSeats([]), []);

  return {
    selectedSeats,
    selectedSeatIds,
    limitReached: selectedSeats.length >= maxSeats,
    toggle,
    remove,
    clear,
  };
}
