import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Login from '../app/pages/Login';

// ─── Mocks ──────────────────────────────────────────────────────────────────

const mockNavigate = vi.fn();
vi.mock('react-router', () => ({
  useNavigate: () => mockNavigate,
}));

const mockLogin = vi.fn();
vi.mock('../app/contexts/AuthContext', () => ({
  useAuth: () => ({ login: mockLogin }),
}));

// ─── Tests ──────────────────────────────────────────────────────────────────

describe('Login page', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders the login form with username and password fields', () => {
    render(<Login />);

    expect(screen.getByLabelText(/nombre de usuario/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/contrasena/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /iniciar sesion/i })).toBeInTheDocument();
  });

  it('shows error when submitting with empty fields', async () => {
    const user = userEvent.setup();
    render(<Login />);

    await user.click(screen.getByRole('button', { name: /iniciar sesion/i }));

    expect(screen.getByText('Por favor completa todos los campos')).toBeInTheDocument();
    expect(mockLogin).not.toHaveBeenCalled();
  });

  it('shows error when submitting with only whitespace username', async () => {
    const user = userEvent.setup();
    render(<Login />);

    await user.type(screen.getByLabelText(/nombre de usuario/i), '   ');
    await user.type(screen.getByLabelText(/contrasena/i), 'pass');
    await user.click(screen.getByRole('button', { name: /iniciar sesion/i }));

    expect(screen.getByText('Por favor completa todos los campos')).toBeInTheDocument();
  });

  it('calls login and navigates to /dashboard on success', async () => {
    mockLogin.mockResolvedValue(true);
    const user = userEvent.setup();
    render(<Login />);

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'profesor1');
    await user.type(screen.getByLabelText(/contrasena/i), 'password123');
    await user.click(screen.getByRole('button', { name: /iniciar sesion/i }));

    expect(mockLogin).toHaveBeenCalledWith('profesor1', 'password123');
    expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
  });

  it('shows error message on failed login', async () => {
    mockLogin.mockResolvedValue(false);
    const user = userEvent.setup();
    render(<Login />);

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'baduser');
    await user.type(screen.getByLabelText(/contrasena/i), 'wrong');
    await user.click(screen.getByRole('button', { name: /iniciar sesion/i }));

    expect(screen.getByText('Usuario o contrasena incorrectos')).toBeInTheDocument();
    expect(mockNavigate).not.toHaveBeenCalled();
  });

  it('disables submit button while submitting', async () => {
    let resolveLogin: (v: boolean) => void;
    mockLogin.mockReturnValue(new Promise((r) => { resolveLogin = r; }));
    const user = userEvent.setup();
    render(<Login />);

    await user.type(screen.getByLabelText(/nombre de usuario/i), 'profesor1');
    await user.type(screen.getByLabelText(/contrasena/i), 'pass');
    await user.click(screen.getByRole('button', { name: /iniciar sesion/i }));

    expect(screen.getByRole('button', { name: /ingresando/i })).toBeDisabled();

    resolveLogin!(true);
  });

  it('navigates to /register when register link is clicked', async () => {
    const user = userEvent.setup();
    render(<Login />);

    await user.click(screen.getByText(/registrate aqui/i));

    expect(mockNavigate).toHaveBeenCalledWith('/register');
  });
});
