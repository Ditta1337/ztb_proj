import type {
  EventCategory,
  ReservationStatus,
  SeatCategory,
  VenueType,
} from '../types/api';

export const EVENT_CATEGORY_LABELS: Record<EventCategory, string> = {
  MOVIE: 'Kino',
  TRAIN_RIDE: 'Pociąg',
  CONCERT: 'Koncert',
  LECTURE: 'Wykład',
};

export const EVENT_CATEGORIES = Object.keys(EVENT_CATEGORY_LABELS) as EventCategory[];

export const VENUE_TYPE_LABELS: Record<VenueType, string> = {
  CINEMA: 'Sala kinowa',
  TRAIN: 'Wagon',
  CONCERT_HALL: 'Sala koncertowa',
  LECTURE_HALL: 'Sala wykładowa',
};

export const SEAT_CATEGORY_LABELS: Record<SeatCategory, string> = {
  STANDARD: 'Standard',
  VIP: 'VIP',
  ACCESSIBLE: 'Dla osób z niepełnosprawnościami',
};

export const RESERVATION_STATUS_LABELS: Record<ReservationStatus, string> = {
  CONFIRMED: 'Potwierdzona',
  CANCELLED: 'Anulowana',
};
