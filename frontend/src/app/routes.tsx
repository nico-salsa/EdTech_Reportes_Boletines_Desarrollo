import { createBrowserRouter, Navigate } from 'react-router';
import Register from './pages/Register';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import CourseDetail from './pages/CourseDetail';
import { ProtectedRoute } from './components/ProtectedRoute';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <Navigate to="/login" replace />,
  },
  {
    path: '/register',
    element: <Register />,
  },
  {
    path: '/login',
    element: <Login />,
  },
  {
    path: '/dashboard',
    element: <ProtectedRoute><Dashboard /></ProtectedRoute>,
  },
  {
    path: '/courses/:courseId',
    element: <ProtectedRoute><CourseDetail /></ProtectedRoute>,
  },
  {
    path: '*',
    element: <Navigate to="/login" replace />,
  },
]);
