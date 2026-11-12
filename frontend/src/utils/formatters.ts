import type { IsoDateTime } from '../types/api';

const LOCALE = 'pl-PL';

const dateTimeFormat = new Intl.DateTimeFormat(LOCALE, {
  weekday: 'short',
  day: 'numeric',
  month: 'long',
  hour: '2-digit',
  minute: '2-digit',
});
const timeFormat = new Intl.DateTimeFormat(LOCALE, { hour: '2-digit', minute: '2-digit' });
const dayFormat = new Intl.DateTimeFormat(LOCALE, { day: '2-digit' });
const monthFormat = new Intl.DateTimeFormat(LOCALE, { month: 'short' });
const priceFormat = new Intl.NumberFormat(LOCALE, { style: 'currency', currency: 'PLN' });

export const formatDateTime = (value: IsoDateTime) => dateTimeFormat.format(new Date(value));
export const formatTime = (value: IsoDateTime) => timeFormat.format(new Date(value));
export const formatDay = (value: IsoDateTime) => dayFormat.format(new Date(value));
export const formatMonthShort = (value: IsoDateTime) => monthFormat.format(new Date(value)).replace('.', '');

export const formatAmount = (value: number) => priceFormat.format(value);

export const formatPrice = (value: number) => (value === 0 ? 'Bezpłatne' : formatAmount(value));

export const formatSeat = (rowLabel: string, seatNumber: number) => `Rząd ${rowLabel}, miejsce ${seatNumber}`;

export function pluralizeSeats(count: number): string {
  const lastDigit = count % 10;
  const lastTwoDigits = count % 100;
  if (count === 1) return 'miejsce';
  if (lastDigit >= 2 && lastDigit <= 4 && (lastTwoDigits < 12 || lastTwoDigits > 14)) return 'miejsca';
  return 'miejsc';
}
