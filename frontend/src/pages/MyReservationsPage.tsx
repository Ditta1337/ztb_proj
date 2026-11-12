import { useCallback, useState } from 'react';

import { reservationsApi } from '../api/reservationsApi';
import { usersApi } from '../api/usersApi';
import { ReservationCard } from '../components/reservation/ReservationCard';
import { ButtonLink } from '../components/ui/ButtonLink';
import { EmptyState } from '../components/ui/EmptyState';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { Spinner } from '../components/ui/Spinner';
import { useToast } from '../components/ui/toast/useToast';
import { useAsync } from '../hooks/useAsync';
import { useCurrentUser } from '../hooks/useCurrentUser';
import type { Reservation, User } from '../types/api';
import { formatAmount } from '../utils/formatters';
import styles from './MyReservationsPage.module.css';

export function MyReservationsPage() {
  const { currentUser, loading, error } = useCurrentUser();

  return (
    <div className="container">
      <header className={styles.header}>
        <p className="eyebrow">Konto</p>
        <h1 className={styles.title}>Moje rezerwacje</h1>
        {currentUser && (
          <p className={styles.subtitle}>
            {currentUser.firstName} {currentUser.lastName} · {currentUser.email}
          </p>
        )}
      </header>

      {loading && <Spinner />}
      {error && <ErrorMessage error={error} />}
      {currentUser && <UserReservations key={currentUser.id} user={currentUser} />}
    </div>
  );
}

function UserReservations({ user }: { user: User }) {
  const loadReservations = useCallback(() => usersApi.getUserReservations(user.id), [user.id]);
  const { data: reservations, error, loading, reload } = useAsync(loadReservations);
  const { showToast } = useToast();
  const [cancellingId, setCancellingId] = useState<number | null>(null);

  const handleCancel = async (reservation: Reservation) => {
    setCancellingId(reservation.id);
    try {
      await reservationsApi.cancelReservation(reservation.id);
      showToast({
        tone: 'info',
        title: 'Rezerwacja anulowana',
        description: 'Tryb demo – status nie zmieni się, bo nic nie jest zapisywane w bazie.',
      });
    } catch (cancelError) {
      showToast({
        tone: 'error',
        title: 'Nie udało się anulować',
        description: cancelError instanceof Error ? cancelError.message : undefined,
      });
    } finally {
      setCancellingId(null);
    }
  };

  if (loading) return <Spinner label="Ładowanie rezerwacji…" />;
  if (error) return <ErrorMessage error={error} onRetry={reload} />;
  if (!reservations || reservations.length === 0) {
    return (
      <EmptyState
        title="Brak rezerwacji"
        description="Nie masz jeszcze żadnych rezerwacji."
        action={<ButtonLink to="/">Przeglądaj wydarzenia</ButtonLink>}
      />
    );
  }

  const active = reservations.filter((reservation) => reservation.status === 'CONFIRMED');
  const cancelled = reservations.filter((reservation) => reservation.status === 'CANCELLED');
  const activeTotal = active.reduce((sum, reservation) => sum + reservation.price, 0);

  return (
    <>
      <dl className={styles.stats}>
        <div>
          <dt>Aktywne</dt>
          <dd>{active.length}</dd>
        </div>
        <div>
          <dt>Anulowane</dt>
          <dd>{cancelled.length}</dd>
        </div>
        <div>
          <dt>Wartość aktywnych</dt>
          <dd>{formatAmount(activeTotal)}</dd>
        </div>
      </dl>

      <ReservationSection
        title="Aktywne"
        reservations={active}
        cancellingId={cancellingId}
        onCancel={handleCancel}
      />
      <ReservationSection
        title="Anulowane"
        reservations={cancelled}
        cancellingId={cancellingId}
        onCancel={handleCancel}
      />
    </>
  );
}

interface ReservationSectionProps {
  title: string;
  reservations: Reservation[];
  cancellingId: number | null;
  onCancel: (reservation: Reservation) => void;
}

function ReservationSection({ title, reservations, cancellingId, onCancel }: ReservationSectionProps) {
  if (reservations.length === 0) return null;

  return (
    <section className={styles.section}>
      <h2 className={styles.sectionTitle}>{title}</h2>
      <div className={styles.list}>
        {reservations.map((reservation) => (
          <ReservationCard
            key={reservation.id}
            reservation={reservation}
            cancelling={cancellingId === reservation.id}
            onCancel={onCancel}
          />
        ))}
      </div>
    </section>
  );
}
