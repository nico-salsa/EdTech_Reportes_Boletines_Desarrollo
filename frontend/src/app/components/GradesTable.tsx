import { useState } from 'react';
import { Course } from '../contexts/AppDataContext';
import { useAppData } from '../contexts/AppDataContext';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Download } from 'lucide-react';
import { ExportReportModal } from './ExportReportModal';

interface GradesTableProps {
  course: Course;
}

export function GradesTable({ course }: GradesTableProps) {
  const { updateGrade } = useAppData();
  const [editingCell, setEditingCell] = useState<string | null>(null);
  const [exportStudentId, setExportStudentId] = useState<string | null>(null);

  const getGrade = (studentId: string, activityId: string): number | null => {
    const gradeEntry = course.grades.find(
      (g) => g.studentId === studentId && g.activityId === activityId
    );
    return gradeEntry?.grade ?? null;
  };

  const handleGradeChange = (studentId: string, activityId: string, value: string) => {
    if (value === '') {
      updateGrade(course.id, studentId, activityId, null);
    } else {
      const numValue = parseFloat(value);
      if (!isNaN(numValue) && numValue >= 0 && numValue <= 5) {
        updateGrade(course.id, studentId, activityId, numValue);
      }
    }
  };

  const calculateAverage = (studentId: string): number | null => {
    const studentGrades = course.activities.map((activity) =>
      getGrade(studentId, activity.id)
    );
    
    if (studentGrades.every((g) => g === null)) {
      return null;
    }

    const validGrades = studentGrades.filter((g) => g !== null) as number[];
    if (validGrades.length === 0) {
      return null;
    }

    return validGrades.reduce((sum, g) => sum + g, 0) / validGrades.length;
  };

  const calculateWeightedAverage = (studentId: string): number | null => {
    let totalWeighted = 0;
    let totalPercentage = 0;

    for (const activity of course.activities) {
      const grade = getGrade(studentId, activity.id);
      if (grade !== null) {
        totalWeighted += grade * activity.percentage;
        totalPercentage += activity.percentage;
      }
    }

    if (totalPercentage === 0) {
      return null;
    }

    return totalWeighted / totalPercentage;
  };

  const hasEmptyGrades = (studentId: string): boolean => {
    return course.activities.some(
      (activity) => getGrade(studentId, activity.id) === null
    );
  };

  return (
    <div className="overflow-x-auto">
      <table className="w-full border-collapse">
        <thead>
          <tr className="bg-muted/50">
            <th className="border border-border p-3 text-left font-semibold text-foreground min-w-[200px]">
              Estudiante
            </th>
            {course.activities.map((activity) => (
              <th
                key={activity.id}
                className="border border-border p-3 text-center font-semibold text-foreground min-w-[140px]"
              >
                <div>{activity.name}</div>
                <div className="text-xs font-normal text-muted-foreground mt-1">
                  {activity.percentage}%
                </div>
              </th>
            ))}
            <th className="border border-border p-3 text-center font-semibold text-foreground min-w-[120px]">
              Promedio Simple
            </th>
            <th className="border border-border p-3 text-center font-semibold text-foreground min-w-[120px]">
              Promedio Ponderado
            </th>
            <th className="border border-border p-3 text-center font-semibold text-foreground min-w-[120px]">
              Acciones
            </th>
          </tr>
        </thead>
        <tbody>
          {course.students.map((student) => {
            const average = calculateAverage(student.studentId);
            const weightedAverage = calculateWeightedAverage(student.studentId);

            return (
              <tr key={student.id} className="hover:bg-muted/30">
                <td className="border border-border p-3">
                  <div className="font-medium text-foreground">{student.name}</div>
                  <div className="text-xs text-muted-foreground">ID: {student.studentId}</div>
                </td>
                {course.activities.map((activity) => {
                  const grade = getGrade(student.studentId, activity.id);
                  const cellId = `${student.studentId}-${activity.id}`;
                  const isEditing = editingCell === cellId;

                  return (
                    <td
                      key={activity.id}
                      className="border border-border p-2 text-center"
                      onClick={() => setEditingCell(cellId)}
                    >
                      {isEditing ? (
                        <Input
                          type="number"
                          step="0.1"
                          min="0"
                          max="5"
                          value={grade ?? ''}
                          onChange={(e) =>
                            handleGradeChange(student.studentId, activity.id, e.target.value)
                          }
                          onBlur={() => setEditingCell(null)}
                          autoFocus
                          className="w-full text-center"
                          placeholder="0.0"
                        />
                      ) : (
                        <div
                          className={`p-2 rounded cursor-pointer hover:bg-accent transition-colors ${
                            grade === null
                              ? 'text-muted-foreground italic'
                              : 'text-foreground font-medium'
                          }`}
                        >
                          {grade !== null ? grade.toFixed(1) : 'Sin nota'}
                        </div>
                      )}
                    </td>
                  );
                })}
                <td className="border border-border p-3 text-center">
                  {average !== null ? (
                    <span className="font-semibold text-foreground">{average.toFixed(2)}</span>
                  ) : (
                    <span className="text-muted-foreground italic">—</span>
                  )}
                </td>
                <td className="border border-border p-3 text-center">
                  {weightedAverage !== null ? (
                    <span className="font-semibold text-foreground">
                      {weightedAverage.toFixed(2)}
                    </span>
                  ) : (
                    <span className="text-muted-foreground italic">—</span>
                  )}
                </td>
                <td className="border border-border p-3 text-center">
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={() => setExportStudentId(student.studentId)}
                  >
                    <Download className="w-4 h-4 mr-2" />
                    Exportar
                  </Button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>

      {exportStudentId && (
        <ExportReportModal
          course={course}
          studentId={exportStudentId}
          hasEmptyGrades={hasEmptyGrades(exportStudentId)}
          onClose={() => setExportStudentId(null)}
        />
      )}
    </div>
  );
}
