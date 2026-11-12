import type { VenueType } from '../types/api';

export const MAX_SEATS_PER_RESERVATION = 10;

export const COMPACT_SEAT_MAP_THRESHOLD = 16;

interface SeatMapLayout {
  frontLabel: string;
  aisleAfterSeat: number | null;
}

export const SEAT_MAP_LAYOUTS: Record<VenueType, SeatMapLayout> = {
  CINEMA: { frontLabel: 'Ekran', aisleAfterSeat: null },
  TRAIN: { frontLabel: 'Kierunek jazdy ↑', aisleAfterSeat: 2 },
  CONCERT_HALL: { frontLabel: 'Scena', aisleAfterSeat: 10 },
  LECTURE_HALL: { frontLabel: 'Tablica', aisleAfterSeat: 8 },
};
