import { useCallback, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

import { ApiError } from '../api/httpClient';
import { eventsApi } from '../api/eventsApi';
import { reservationsApi } from '../api/reservationsApi';
import { OccupancyBar } from '../components/events/OccupancyBar';
import { ReservationSummary } from '../components/reservation/ReservationSummary';
import { SeatMap } from '../components/seat-map/SeatMap';
import { Badge } from '../components/ui/Badge';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { Spinner } from '../components/ui/Spinner';
import { useToast } from '../components/ui/toast/useToast';
import { EVENT_CATEGORY_LABELS, VENUE_TYPE_LABELS } from '../constants/labels';
import { MAX_SEATS_PER_RESERVATION } from '../constants/seatMap';
import { useAsync } from '../hooks/useAsync';
import { useCurrentUser } from '../hooks/useCurrentUser';
import { useSeatSelection } from '../hooks/useSeatSelection';
import { formatDateTime, formatTime, pluralizeSeats } from '../utils/formatters';
import styles from './EventDetailsPage.module.css';
import { NotFoundPage } from './NotFoundPage';

export function EventDetailsPage() {
  const { eventId } = useParams();
  const id = Number(eventId);

  if (!Number.isInteger(id) || id <= 0) {
    return <NotFoundPage />;
  }

  return <EventDetails key={id} eventId={id} />;
}

function EventDetails({ eventId }: { eventId: number }) {
  const loadEvent = useCallback(
    () => Promise.all([eventsApi.getEvent(eventId), eventsApi.getEventSeats(eventId)]),
    [eventId],
  );
  const { data, error, loading, reload } = useAsync(loadEvent);
  const { currentUser } = useCurrentUser();
  const { showToast } = useToast();
  const selection = useSeatSelection(MAX_SEATS_PER_RESERVATION);
  const [submitting, setSubmitting] = useState(false);

  if (loading) {
    return <Spinner label="Ładowanie mapy miejsc…" />;
  }
  if (error instanceof ApiError && error.status === 404) {
    return <NotFoundPage />;
  }
  if (error || !data) {
    return (
      <div className="container">
        <ErrorMessage error={error ?? new Error('Brak danych')} onRetry={reload} />
      </div>
    );
  }

  const [event, seats] = data;

  const handleSubmit = async () => {
    if (!currentUser) return;
    setSubmitting(true);
    try {
      const created = await reservationsApi.createReservation({
        userId: currentUser.id,
        eventId: event.id,
        eventSeatIds: selection.selectedSeats.map((seat) => seat.id),
      });
      showToast({
        tone: 'success',
        title: `Zarezerwowano ${created.length} ${pluralizeSeats(created.length)}`,
        description: 'Tryb demo – odpowiedź z mocka, rezerwacja nie została zapisana w bazie.',
      });
      selection.clear();
    } catch (submitError) {
      const conflict = submitError instanceof ApiError && submitError.status === 409;
      showToast({
        tone: 'error',
        title: conflict ? 'Miejsce zostało już zajęte' : 'Nie udało się zarezerwować',
        description: conflict
          ? 'Ktoś zarezerwował wybrane miejsce przed Tobą. Odświeżamy mapę miejsc.'
          : submitError instanceof Error
            ? submitError.message
            : undefined,
      });
      if (conflict) {
        selection.clear();
        reload();
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container">
      <Link to="/" className={styles.back}>
        ← Wszystkie wydarzenia
      </Link>

      <header className={styles.header}>
        <div className={styles.headline}>
          <div className={styles.badges}>
            <Badge tone="accent">{EVENT_CATEGORY_LABELS[event.category]}</Badge>
            <Badge>{VENUE_TYPE_LABELS[event.venue.venueType]}</Badge>
          </div>
          <h1 className={styles.title}>{event.title}</h1>
          <p className={styles.description}>{event.description}</p>
        </div>
        <dl className={styles.facts}>
          <div>
            <dt>Termin</dt>
            <dd>
              {formatDateTime(event.startsAt)} – {formatTime(event.endsAt)}
            </dd>
          </div>
          <div>
            <dt>Miejsce</dt>
            <dd>
              {event.venue.name}, {event.venue.city}
            </dd>
          </div>
          <div>
            <dt>Dostępność</dt>
            <dd>
              <OccupancyBar available={event.availableSeats} total={event.totalSeats} />
            </dd>
          </div>
        </dl>
      </header>

      <div className={styles.layout}>
        <SeatMap
          seats={seats}
          venueType={event.venue.venueType}
          selectedSeatIds={selection.selectedSeatIds}
          selectionLimitReached={selection.limitReached}
          onToggleSeat={selection.toggle}
        />
        <div className={styles.sidebar}>
          <ReservationSummary
            selectedSeats={selection.selectedSeats}
            currentUser={currentUser}
            submitting={submitting}
            onRemoveSeat={selection.remove}
            onClear={selection.clear}
            onSubmit={handleSubmit}
          />
        </div>
      </div>
    </div>
  );
}
