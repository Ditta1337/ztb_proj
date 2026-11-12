import { useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';

import { eventsApi } from '../api/eventsApi';
import { CategoryFilter } from '../components/events/CategoryFilter';
import { EventCard } from '../components/events/EventCard';
import { EmptyState } from '../components/ui/EmptyState';
import { ErrorMessage } from '../components/ui/ErrorMessage';
import { Spinner } from '../components/ui/Spinner';
import { EVENT_CATEGORIES } from '../constants/labels';
import { useAsync } from '../hooks/useAsync';
import type { EventCategory } from '../types/api';
import styles from './EventsPage.module.css';

const CATEGORY_PARAM = 'category';

function parseCategory(value: string | null): EventCategory | null {
  return EVENT_CATEGORIES.find((category) => category === value) ?? null;
}

export function EventsPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const category = parseCategory(searchParams.get(CATEGORY_PARAM));

  const loadEvents = useCallback(() => eventsApi.getEvents(category ?? undefined), [category]);
  const { data: events, error, loading, reload } = useAsync(loadEvents);

  const handleCategoryChange = (next: EventCategory | null) => {
    setSearchParams(next ? { [CATEGORY_PARAM]: next } : {});
  };

  return (
    <div className="container">
      <div className={styles.toolbar}>
        <h1 className={styles.title}>Wydarzenia</h1>
        <CategoryFilter value={category} onChange={handleCategoryChange} />
      </div>

      {loading && <Spinner label="Ładowanie wydarzeń…" />}
      {error && <ErrorMessage error={error} onRetry={reload} />}
      {!loading && !error && events?.length === 0 && (
        <EmptyState title="Brak wydarzeń" description="W tej kategorii nie ma jeszcze żadnych wydarzeń." />
      )}
      {!loading && !error && events && events.length > 0 && (
        <div className={styles.grid}>
          {events.map((event) => (
            <EventCard key={event.id} event={event} />
          ))}
        </div>
      )}
    </div>
  );
}
