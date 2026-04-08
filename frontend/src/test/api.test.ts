import { describe, it, expect, beforeEach, vi } from 'vitest';
import { getSessionToken, setSessionToken, clearSessionToken, ApiError, requestJson, requestBlob } from '../app/lib/api';

// ─── Token management ───────────────────────────────────────────────────────

describe('session token helpers', () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it('getSessionToken returns null when no token is stored', () => {
    expect(getSessionToken()).toBeNull();
  });

  it('setSessionToken stores and getSessionToken retrieves the token', () => {
    setSessionToken('abc-123');
    expect(getSessionToken()).toBe('abc-123');
  });

  it('clearSessionToken removes the stored token', () => {
    setSessionToken('abc-123');
    clearSessionToken();
    expect(getSessionToken()).toBeNull();
  });
});

// ─── ApiError ───────────────────────────────────────────────────────────────

describe('ApiError', () => {
  it('should be an instance of Error', () => {
    const err = new ApiError('fail', 400);
    expect(err).toBeInstanceOf(Error);
  });

  it('should expose status and message', () => {
    const err = new ApiError('Not found', 404);
    expect(err.message).toBe('Not found');
    expect(err.status).toBe(404);
  });

  it('should default details to empty object', () => {
    const err = new ApiError('fail', 500);
    expect(err.details).toEqual({});
  });

  it('should accept custom details', () => {
    const err = new ApiError('fail', 400, { field: 'name' });
    expect(err.details).toEqual({ field: 'name' });
  });
});

// ─── requestJson ────────────────────────────────────────────────────────────

describe('requestJson', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('should call fetch with API base URL and path', async () => {
    const spy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify({ ok: true }), {
        status: 200,
        headers: { 'content-type': 'application/json' },
      }),
    );

    await requestJson('/courses');
    expect(spy).toHaveBeenCalledOnce();
    const url = spy.mock.calls[0][0] as string;
    expect(url).toContain('/courses');
  });

  it('should include X-Session-Token header when token exists', async () => {
    setSessionToken('my-token');
    const spy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify({}), {
        status: 200,
        headers: { 'content-type': 'application/json' },
      }),
    );

    await requestJson('/courses');
    const headers = spy.mock.calls[0][1]?.headers as Headers;
    expect(headers.get('X-Session-Token')).toBe('my-token');
  });

  it('should NOT include X-Session-Token header when no token', async () => {
    const spy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify({}), {
        status: 200,
        headers: { 'content-type': 'application/json' },
      }),
    );

    await requestJson('/courses');
    const headers = spy.mock.calls[0][1]?.headers as Headers;
    expect(headers.has('X-Session-Token')).toBe(false);
  });

  it('should set Content-Type to application/json when body is provided', async () => {
    const spy = vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify({}), {
        status: 200,
        headers: { 'content-type': 'application/json' },
      }),
    );

    await requestJson('/auth/login', { method: 'POST', body: JSON.stringify({ user: 'x' }) });
    const headers = spy.mock.calls[0][1]?.headers as Headers;
    expect(headers.get('Content-Type')).toBe('application/json');
  });

  it('should return parsed JSON on success', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify({ id: '1', name: 'Test' }), {
        status: 200,
        headers: { 'content-type': 'application/json' },
      }),
    );

    const result = await requestJson<{ id: string; name: string }>('/courses/1');
    expect(result).toEqual({ id: '1', name: 'Test' });
  });

  it('should return undefined for 204 No Content', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(null, { status: 204 }),
    );

    const result = await requestJson('/auth/logout', { method: 'POST' });
    expect(result).toBeUndefined();
  });

  it('should throw ApiError with message from JSON error response', async () => {
    vi.spyOn(globalThis, 'fetch').mockImplementation(() =>
      Promise.resolve(
        new Response(JSON.stringify({ message: 'No autorizado', details: {} }), {
          status: 401,
          headers: { 'content-type': 'application/json' },
        }),
      ),
    );

    const error = await requestJson('/auth/session').catch((e) => e);
    expect(error).toBeInstanceOf(ApiError);
    expect(error.message).toBe('No autorizado');
    expect(error.status).toBe(401);
  });

  it('should throw ApiError with text for non-JSON error response', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response('Server Error', {
        status: 500,
        headers: { 'content-type': 'text/plain' },
      }),
    );

    await expect(requestJson('/fail')).rejects.toThrow(ApiError);
  });
});

// ─── requestBlob ────────────────────────────────────────────────────────────

describe('requestBlob', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('should return blob and parsed filename from Content-Disposition', async () => {
    const body = new Uint8Array([0x25, 0x50, 0x44, 0x46]);
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(body, {
        status: 200,
        headers: {
          'content-type': 'application/pdf',
          'content-disposition': 'attachment; filename="report.pdf"',
        },
      }),
    );

    const result = await requestBlob('/courses/1/students/2/report?format=pdf');
    expect(result.filename).toBe('report.pdf');
    expect(result.blob.size).toBe(4);
    expect(result.blob.type).toBe('application/pdf');
  });

  it('should return null filename when Content-Disposition is absent', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response('ok', { status: 200, headers: { 'content-type': 'text/html' } }),
    );

    const result = await requestBlob('/report');
    expect(result.filename).toBeNull();
  });

  it('should throw ApiError on non-ok response', async () => {
    vi.spyOn(globalThis, 'fetch').mockResolvedValue(
      new Response(JSON.stringify({ message: 'Forbidden' }), {
        status: 403,
        headers: { 'content-type': 'application/json' },
      }),
    );

    await expect(requestBlob('/report')).rejects.toThrow(ApiError);
  });
});
