import { EVENT_CATEGORIES, EVENT_CATEGORY_LABELS } from '../../constants/labels';
import type { EventCategory } from '../../types/api';
import styles from './CategoryFilter.module.css';

interface CategoryFilterProps {
  value: EventCategory | null;
  onChange: (category: EventCategory | null) => void;
}

export function CategoryFilter({ value, onChange }: CategoryFilterProps) {
  const options: Array<{ key: EventCategory | null; label: string }> = [
    { key: null, label: 'Wszystkie' },
    ...EVENT_CATEGORIES.map((category) => ({ key: category, label: EVENT_CATEGORY_LABELS[category] })),
  ];

  return (
    <div className={styles.filter} role="group" aria-label="Filtruj według kategorii">
      {options.map((option) => (
        <button
          key={option.key ?? 'ALL'}
          type="button"
          className={`${styles.chip} ${option.key === value ? styles.active : ''}`}
          aria-pressed={option.key === value}
          onClick={() => onChange(option.key)}
        >
          {option.label}
        </button>
      ))}
    </div>
  );
}
