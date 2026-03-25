import { RouterProvider } from 'react-router';
import { AuthProvider } from './contexts/AuthContext';
import { AppDataProvider } from './contexts/AppDataContext';
import { router } from './routes.tsx';

export default function App() {
  return (
    <AuthProvider>
      <AppDataProvider>
        <RouterProvider router={router} />
      </AppDataProvider>
    </AuthProvider>
  );
}
