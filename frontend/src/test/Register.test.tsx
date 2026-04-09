import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Register from '../app/pages/Register';

// ─── Mocks ──────────────────────────────────────────────────────────────────

const mockNavigate = vi.fn();
vi.mock('react-router', () => ({
  useNavigate: () => mockNavigate,
}));

const mockRegister = vi.fn();
vi.mock('../app/contexts/AuthContext', () => ({
  useAuth: () => ({ register: mockRegister }),
}));

// ─── Tests ──────────────────────────────────────────────────────────────────

describe('Register page', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('shows error when username is empty', async () => {
    const user = userEvent.setup();
    render(<Register />);

    await user.type(screen.getByLabelText('Contrasena'), 'pass123');
    await user.type(screen.getByLabelText('Confirmar contrasena'), 'pass123');
    await user.click(screen.getByRole('button', { name: /registrarme/i }));

    expect(screen.getByText('El nombre de usuario es obligatorio')).toBeInTheDocument();
    expect(mockRegister).not.toHaveBeenCalled();
  });

  it('shows error when passwords do not match', async () => {
    const user = userEvent.setup();
    render(<Register />);

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'profesor1');
    await user.type(screen.getByLabelText('Contrasena'), 'pass123');
    await user.type(screen.getByLabelText('Confirmar contrasena'), 'different');
    await user.click(screen.getByRole('button', { name: /registrarme/i }));

    expect(screen.getByText('Las contrasenas no coinciden')).toBeInTheDocument();
    expect(mockRegister).not.toHaveBeenCalled();
  });

  it('shows success message and navigates to dashboard on registration', async () => {
    mockRegister.mockResolvedValue(true);
    const user = userEvent.setup();
    render(<Register />);

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'profesor1');
    await user.type(screen.getByLabelText('Contrasena'), 'pass123');
    await user.type(screen.getByLabelText('Confirmar contrasena'), 'pass123');
    await user.click(screen.getByRole('button', { name: /registrarme/i }));

    expect(mockRegister).toHaveBeenCalledWith('profesor1', 'pass123');
    expect(screen.getByText(/cuenta creada exitosamente/i)).toBeInTheDocument();

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
    }, { timeout: 2000 });
  });

  it('navigates to /login when login link is clicked', async () => {
    const user = userEvent.setup();
    render(<Register />);

    await user.click(screen.getByText(/inicia sesion/i));

    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });
});
