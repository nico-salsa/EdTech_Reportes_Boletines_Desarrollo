const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api';
const SESSION_TOKEN_KEY = 'edtech_session_token';

export class ApiError extends Error {
  status: number;
  details: Record<string, unknown>;

  constructor(message: string, status: number, details: Record<string, unknown> = {}) {
    super(message);
    this.status = status;
    this.details = details;
  }
}

export function getSessionToken(): string | null {
  return localStorage.getItem(SESSION_TOKEN_KEY);
}

export function setSessionToken(token: string): void {
  localStorage.setItem(SESSION_TOKEN_KEY, token);
}

export function clearSessionToken(): void {
  localStorage.removeItem(SESSION_TOKEN_KEY);
}

async function parseResponse<T>(response: Response): Promise<T> {
  if (response.status === 204) {
    return undefined as T;
  }

  const contentType = response.headers.get('content-type') ?? '';
  const isJson = contentType.includes('application/json');
  const payload = isJson ? await response.json() : await response.text();

  if (!response.ok) {
    if (isJson && payload && typeof payload === 'object') {
      const errorPayload = payload as { message?: string; details?: Record<string, unknown> };
      throw new ApiError(errorPayload.message ?? 'Error inesperado', response.status, errorPayload.details ?? {});
    }
    throw new ApiError(typeof payload === 'string' ? payload : 'Error inesperado', response.status);
  }

  return payload as T;
}

export async function requestJson<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers ?? {});
  if (!headers.has('Content-Type') && init.body !== undefined) {
    headers.set('Content-Type', 'application/json');
  }

  const token = getSessionToken();
  if (token) {
    headers.set('X-Session-Token', token);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers,
  });

  return parseResponse<T>(response);
}

export async function requestBlob(path: string, init: RequestInit = {}): Promise<{ blob: Blob; filename: string | null }> {
  const headers = new Headers(init.headers ?? {});
  const token = getSessionToken();
  if (token) {
    headers.set('X-Session-Token', token);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers,
  });

  if (!response.ok) {
    await parseResponse(response);
  }

  const contentDisposition = response.headers.get('content-disposition');
  const filename = contentDisposition?.match(/filename="?([^\"]+)"?/)?.[1] ?? null;
  return {
    blob: await response.blob(),
    filename,
  };
}
