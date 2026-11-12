import type { EventSeat } from '../types/api';

export interface SeatRow {
  label: string;
  seats: EventSeat[];
}

export function groupSeatsByRow(seats: EventSeat[]): SeatRow[] {
  const rows = new Map<string, EventSeat[]>();
  for (const seat of seats) {
    const row = rows.get(seat.rowLabel) ?? [];
    row.push(seat);
    rows.set(seat.rowLabel, row);
  }
  return Array.from(rows, ([label, rowSeats]) => ({
    label,
    seats: [...rowSeats].sort((a, b) => a.seatNumber - b.seatNumber),
  }));
}
