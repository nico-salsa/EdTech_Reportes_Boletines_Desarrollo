import { createContext, useContext, useState, useEffect, ReactNode } from 'react';

interface User {
  id: string;
  username: string;
}

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => boolean;
  register: (username: string, password: string) => boolean;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    // Check if user is logged in
    const storedUser = localStorage.getItem('edtech_current_user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const register = (username: string, password: string): boolean => {
    if (!username || !password) {
      return false;
    }

    const users = JSON.parse(localStorage.getItem('edtech_users') || '[]');
    
    // Check if username already exists
    if (users.some((u: any) => u.username === username)) {
      return false;
    }

    const newUser = {
      id: Date.now().toString(),
      username,
      password, // In a real app, this should be hashed
    };

    users.push(newUser);
    localStorage.setItem('edtech_users', JSON.stringify(users));
    
    const userWithoutPassword = { id: newUser.id, username: newUser.username };
    setUser(userWithoutPassword);
    localStorage.setItem('edtech_current_user', JSON.stringify(userWithoutPassword));
    
    return true;
  };

  const login = (username: string, password: string): boolean => {
    const users = JSON.parse(localStorage.getItem('edtech_users') || '[]');
    const foundUser = users.find(
      (u: any) => u.username === username && u.password === password
    );

    if (foundUser) {
      const userWithoutPassword = { id: foundUser.id, username: foundUser.username };
      setUser(userWithoutPassword);
      localStorage.setItem('edtech_current_user', JSON.stringify(userWithoutPassword));
      return true;
    }

    return false;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('edtech_current_user');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        register,
        logout,
        isAuthenticated: !!user,
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
