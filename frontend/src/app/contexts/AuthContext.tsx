import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { clearSessionToken, requestJson, setSessionToken } from '../lib/api';

interface User {
  id: string;
  username: string;
}

interface AuthSessionResponse {
  user: User;
  token: string;
}

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => Promise<boolean>;
  register: (username: string, password: string) => Promise<boolean>;
  logout: () => Promise<void>;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);
const USER_STORAGE_KEY = 'edtech_current_user';

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const storedUser = localStorage.getItem(USER_STORAGE_KEY);
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }

    requestJson<AuthSessionResponse>('/auth/session')
      .then((session) => {
        persistSession(session);
      })
      .catch(() => {
        clearSession();
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  const register = async (username: string, password: string): Promise<boolean> => {
    try {
      const session = await requestJson<AuthSessionResponse>('/auth/register', {
        method: 'POST',
        body: JSON.stringify({ username, password }),
      });
      persistSession(session);
      return true;
    } catch {
      return false;
    }
  };

  const login = async (username: string, password: string): Promise<boolean> => {
    try {
      const session = await requestJson<AuthSessionResponse>('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password }),
      });
      persistSession(session);
      return true;
    } catch {
      return false;
    }
  };

  const logout = async (): Promise<void> => {
    try {
      await requestJson<void>('/auth/logout', { method: 'POST' });
    } catch {
      // Ignore logout network errors and clear local state anyway.
    }
    clearSession();
  };

  const persistSession = (session: AuthSessionResponse) => {
    setSessionToken(session.token);
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(session.user));
    setUser(session.user);
  };

  const clearSession = () => {
    clearSessionToken();
    localStorage.removeItem(USER_STORAGE_KEY);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        register,
        logout,
        isAuthenticated: !!user,
        isLoading,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
