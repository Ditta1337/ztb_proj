import type { Reservation, User } from '../types/api';
import { http } from './httpClient';

export const usersApi = {
  getUsers: () => http.get<User[]>('/users'),
  getUserReservations: (userId: number) => http.get<Reservation[]>(`/users/${userId}/reservations`),
};
