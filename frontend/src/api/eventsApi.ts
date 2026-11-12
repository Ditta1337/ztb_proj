import type { EventCategory, EventDetails, EventSeat, EventSummary } from '../types/api';
import { http } from './httpClient';

export const eventsApi = {
  getEvents: (category?: EventCategory) => {
    const query = category ? `?category=${encodeURIComponent(category)}` : '';
    return http.get<EventSummary[]>(`/events${query}`);
  },
  getEvent: (eventId: number) => http.get<EventDetails>(`/events/${eventId}`),
  getEventSeats: (eventId: number) => http.get<EventSeat[]>(`/events/${eventId}/seats`),
};
