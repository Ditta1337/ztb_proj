import { RouterProvider } from 'react-router-dom';

import { ToastProvider } from './components/ui/toast/ToastProvider';
import { CurrentUserProvider } from './context/CurrentUserProvider';
import { router } from './router';

export function App() {
  return (
    <ToastProvider>
      <CurrentUserProvider>
        <RouterProvider router={router} />
      </CurrentUserProvider>
    </ToastProvider>
  );
}
