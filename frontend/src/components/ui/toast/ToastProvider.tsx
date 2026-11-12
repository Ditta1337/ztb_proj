import { useCallback, useMemo, useRef, useState, type ReactNode } from 'react';

import styles from './Toast.module.css';
import { ToastContext, type ToastMessage } from './ToastContext';

const TOAST_DURATION_MS = 5000;

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);
  const nextId = useRef(1);

  const dismiss = useCallback((id: number) => {
    setToasts((current) => current.filter((toast) => toast.id !== id));
  }, []);

  const showToast = useCallback(
    (toast: Omit<ToastMessage, 'id'>) => {
      const id = nextId.current++;
      setToasts((current) => [...current, { ...toast, id }]);
      window.setTimeout(() => dismiss(id), TOAST_DURATION_MS);
    },
    [dismiss],
  );

  const value = useMemo(() => ({ showToast }), [showToast]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className={styles.viewport} aria-live="polite">
        {toasts.map((toast) => (
          <div key={toast.id} className={`${styles.toast} ${styles[toast.tone]}`} role="status">
            <div className={styles.content}>
              <strong className={styles.title}>{toast.title}</strong>
              {toast.description && <p className={styles.description}>{toast.description}</p>}
            </div>
            <button className={styles.close} onClick={() => dismiss(toast.id)} aria-label="Zamknij powiadomienie">
              ×
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}
