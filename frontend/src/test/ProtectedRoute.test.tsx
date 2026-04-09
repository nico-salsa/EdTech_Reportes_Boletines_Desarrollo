import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router';
import { ProtectedRoute } from '../app/components/ProtectedRoute';

// ─── Mock ───────────────────────────────────────────────────────────────────

const mockUseAuth = vi.fn();
vi.mock('../app/contexts/AuthContext', () => ({
  useAuth: () => mockUseAuth(),
}));

// ─── Tests ──────────────────────────────────────────────────────────────────

describe('ProtectedRoute', () => {
  it('renders children when user is authenticated', () => {
    mockUseAuth.mockReturnValue({ isAuthenticated: true, isLoading: false });

    render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Dashboard Content</div>
        </ProtectedRoute>
      </MemoryRouter>,
    );

    expect(screen.getByText('Dashboard Content')).toBeInTheDocument();
  });

  it('redirects to /login when user is not authenticated', () => {
    mockUseAuth.mockReturnValue({ isAuthenticated: false, isLoading: false });

    render(
      <MemoryRouter initialEntries={['/dashboard']}>
        <ProtectedRoute>
          <div>Dashboard Content</div>
        </ProtectedRoute>
      </MemoryRouter>,
    );

    expect(screen.queryByText('Dashboard Content')).not.toBeInTheDocument();
  });

  it('renders nothing while loading', () => {
    mockUseAuth.mockReturnValue({ isAuthenticated: false, isLoading: true });

    const { container } = render(
      <MemoryRouter>
        <ProtectedRoute>
          <div>Dashboard Content</div>
        </ProtectedRoute>
      </MemoryRouter>,
    );

    expect(screen.queryByText('Dashboard Content')).not.toBeInTheDocument();
    expect(container.innerHTML).toBe('');
  });
});
