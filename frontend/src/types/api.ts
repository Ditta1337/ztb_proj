export type EventCategory = 'MOVIE' | 'TRAIN_RIDE' | 'CONCERT' | 'LECTURE';
export type VenueType = 'CINEMA' | 'TRAIN' | 'CONCERT_HALL' | 'LECTURE_HALL';
export type SeatCategory = 'STANDARD' | 'VIP' | 'ACCESSIBLE';
export type SeatStatus = 'AVAILABLE' | 'RESERVED';
export type ReservationStatus = 'CONFIRMED' | 'CANCELLED';

export type IsoDateTime = string;

export interface Venue {
  id: number;
  name: string;
  city: string;
  venueType: VenueType;
  capacity: number;
}

export interface EventSummary {
  id: number;
  title: string;
  category: EventCategory;
  startsAt: IsoDateTime;
  endsAt: IsoDateTime;
  venueName: string;
  city: string;
  venueType: VenueType;
  totalSeats: number;
  availableSeats: number;
  minPrice: number;
}

export interface EventDetails {
  id: number;
  title: string;
  description: string;
  category: EventCategory;
  startsAt: IsoDateTime;
  endsAt: IsoDateTime;
  venue: Venue;
  totalSeats: number;
  availableSeats: number;
}

export interface EventSeat {
  id: number;
  seatId: number;
  rowLabel: string;
  seatNumber: number;
  category: SeatCategory;
  price: number;
  status: SeatStatus;
}

export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
}

export interface Reservation {
  id: number;
  userId: number;
  eventId: number;
  eventTitle: string;
  eventStartsAt: IsoDateTime;
  venueName: string;
  eventSeatId: number;
  rowLabel: string;
  seatNumber: number;
  price: number;
  status: ReservationStatus;
  createdAt: IsoDateTime;
  cancelledAt: IsoDateTime | null;
}

export interface CreateReservationRequest {
  userId: number;
  eventId: number;
  eventSeatIds: number[];
}

export interface ApiProblem {
  status: number;
  title?: string;
  detail?: string;
  fieldErrors?: Record<string, string>;
  unavailableEventSeatIds?: number[];
}
