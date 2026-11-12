import type { CreateReservationRequest, Reservation } from '../types/api';
import { http } from './httpClient';

export const reservationsApi = {
  createReservation: (request: CreateReservationRequest) => http.post<Reservation[]>('/reservations', request),
  cancelReservation: (reservationId: number) => http.delete(`/reservations/${reservationId}`),
};
