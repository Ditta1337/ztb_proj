import { ButtonLink } from '../components/ui/ButtonLink';
import { EmptyState } from '../components/ui/EmptyState';

export function NotFoundPage() {
  return (
    <div className="container">
      <EmptyState
        title="Nie znaleziono strony"
        description="To miejsce nie istnieje – albo ktoś zarezerwował je przed Tobą."
        action={
          <ButtonLink to="/" variant="secondary">
            Wróć do wydarzeń
          </ButtonLink>
        }
      />
    </div>
  );
}
