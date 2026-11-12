import type { ReactNode } from 'react';

import styles from './Feedback.module.css';

interface EmptyStateProps {
  title: string;
  description?: string;
  action?: ReactNode;
}

export function EmptyState({ title, description, action }: EmptyStateProps) {
  return (
    <div className={styles.box}>
      <h3 className={styles.title}>{title}</h3>
      {description && <p className={styles.text}>{description}</p>}
      {action}
    </div>
  );
}
