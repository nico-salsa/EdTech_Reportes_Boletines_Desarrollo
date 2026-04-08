import { describe, it, expect } from 'vitest';

/**
 * Extract the pure calculation logic from GradesTable for isolated testing.
 * These mirror the exact formulas used in GradesTable.tsx.
 */

interface Activity { id: string; name: string; percentage: number }
interface GradeEntry { studentId: string; activityId: string; grade: number | null }

function getGrade(grades: GradeEntry[], studentId: string, activityId: string): number | null {
  return grades.find((g) => g.studentId === studentId && g.activityId === activityId)?.grade ?? null;
}

function calculateAverage(activities: Activity[], grades: GradeEntry[], studentId: string): number | null {
  if (activities.length === 0) return null;
  const total = activities.reduce((sum, a) => sum + (getGrade(grades, studentId, a.id) ?? 0), 0);
  return total / activities.length;
}

function calculateWeightedAverage(activities: Activity[], grades: GradeEntry[], studentId: string): number | null {
  if (activities.length === 0) return null;
  const total = activities.reduce(
    (sum, a) => sum + (getGrade(grades, studentId, a.id) ?? 0) * a.percentage,
    0,
  );
  return total / 100;
}

function hasEmptyGrades(activities: Activity[], grades: GradeEntry[], studentId: string): boolean {
  return activities.some((a) => getGrade(grades, studentId, a.id) === null);
}

// ─── Tests ──────────────────────────────────────────────────────────────────

describe('GradesTable calculation logic', () => {
  const activities: Activity[] = [
    { id: 'act-1', name: 'Parcial 1', percentage: 30 },
    { id: 'act-2', name: 'Parcial 2', percentage: 30 },
    { id: 'act-3', name: 'Final', percentage: 40 },
  ];

  const grades: GradeEntry[] = [
    { studentId: 'STU-001', activityId: 'act-1', grade: 80 },
    { studentId: 'STU-001', activityId: 'act-2', grade: 90 },
    { studentId: 'STU-001', activityId: 'act-3', grade: 100 },
    { studentId: 'STU-002', activityId: 'act-1', grade: 70 },
    // STU-002 has no grade for act-2 and act-3
  ];

  // ─── calculateAverage ─────────────────────────────────────────────────────

  describe('calculateAverage (simple)', () => {
    it('returns null when there are no activities', () => {
      expect(calculateAverage([], grades, 'STU-001')).toBeNull();
    });

    it('computes simple average correctly with all grades', () => {
      // (80 + 90 + 100) / 3 = 90
      expect(calculateAverage(activities, grades, 'STU-001')).toBe(90);
    });

    it('treats missing grades as 0', () => {
      // (70 + 0 + 0) / 3 ≈ 23.33
      expect(calculateAverage(activities, grades, 'STU-002')).toBeCloseTo(23.33, 1);
    });

    it('returns 0 average when all grades are missing', () => {
      expect(calculateAverage(activities, [], 'STU-999')).toBe(0);
    });
  });

  // ─── calculateWeightedAverage ─────────────────────────────────────────────

  describe('calculateWeightedAverage', () => {
    it('returns null when there are no activities', () => {
      expect(calculateWeightedAverage([], grades, 'STU-001')).toBeNull();
    });

    it('computes weighted average correctly', () => {
      // (80×30 + 90×30 + 100×40) / 100 = (2400 + 2700 + 4000) / 100 = 91
      expect(calculateWeightedAverage(activities, grades, 'STU-001')).toBe(91);
    });

    it('treats missing grades as 0 in weighted calculation', () => {
      // (70×30 + 0×30 + 0×40) / 100 = 2100 / 100 = 21
      expect(calculateWeightedAverage(activities, grades, 'STU-002')).toBe(21);
    });

    it('returns 0 when all grades are missing', () => {
      expect(calculateWeightedAverage(activities, [], 'STU-999')).toBe(0);
    });
  });

  // ─── hasEmptyGrades ───────────────────────────────────────────────────────

  describe('hasEmptyGrades', () => {
    it('returns false when all grades exist', () => {
      expect(hasEmptyGrades(activities, grades, 'STU-001')).toBe(false);
    });

    it('returns true when some grades are missing', () => {
      expect(hasEmptyGrades(activities, grades, 'STU-002')).toBe(true);
    });

    it('returns true when no grades exist at all', () => {
      expect(hasEmptyGrades(activities, [], 'STU-001')).toBe(true);
    });

    it('returns false when there are no activities', () => {
      expect(hasEmptyGrades([], grades, 'STU-001')).toBe(false);
    });
  });
});
