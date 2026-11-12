import { Button } from './Button';
import styles from './Feedback.module.css';

interface ErrorMessageProps {
  error: Error;
  onRetry?: () => void;
}

export function ErrorMessage({ error, onRetry }: ErrorMessageProps) {
  return (
    <div className={`${styles.box} ${styles.error}`} role="alert">
      <h3 className={styles.title}>Coś poszło nie tak</h3>
      <p className={styles.text}>{error.message}</p>
      {onRetry && (
        <Button variant="secondary" onClick={onRetry}>
          Spróbuj ponownie
        </Button>
      )}
    </div>
  );
}
