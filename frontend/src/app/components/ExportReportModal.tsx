import { useState } from 'react';
import { Course } from '../contexts/AppDataContext';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';
import { Button } from './ui/button';
import { Label } from './ui/label';
import { Alert, AlertDescription } from './ui/alert';
import { AlertCircle, CheckCircle2, FileText, FileJson, Download } from 'lucide-react';

interface ExportReportModalProps {
  course: Course;
  studentId: string;
  hasEmptyGrades: boolean;
  onClose: () => void;
}

export function ExportReportModal({
  course,
  studentId,
  hasEmptyGrades,
  onClose,
}: ExportReportModalProps) {
  const [format, setFormat] = useState<'pdf' | 'html' | 'json'>('pdf');
  const [showWarning, setShowWarning] = useState(hasEmptyGrades);
  const [exported, setExported] = useState(false);

  const student = course.students.find((s) => s.studentId === studentId);

  if (!student) {
    return null;
  }

  const getGrade = (activityId: string): number | null => {
    const gradeEntry = course.grades.find(
      (g) => g.studentId === studentId && g.activityId === activityId
    );
    return gradeEntry?.grade ?? null;
  };

  const calculateWeightedAverage = (): number | null => {
    let totalWeighted = 0;
    let totalPercentage = 0;

    for (const activity of course.activities) {
      const grade = getGrade(activity.id);
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

  const handleExport = () => {
    const weightedAverage = calculateWeightedAverage();
    
    const reportData = {
      course: course.name,
      student: {
        id: student.studentId,
        name: student.name,
        email: student.email,
      },
      grades: course.activities.map((activity) => ({
        activity: activity.name,
        percentage: activity.percentage,
        grade: getGrade(activity.id),
      })),
      weightedAverage,
      exportDate: new Date().toISOString(),
      format,
    };

    // Simulate export based on format
    if (format === 'json') {
      const dataStr = JSON.stringify(reportData, null, 2);
      const dataBlob = new Blob([dataStr], { type: 'application/json' });
      const url = URL.createObjectURL(dataBlob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `boletin-${student.studentId}-${course.name}.json`;
      link.click();
      URL.revokeObjectURL(url);
    } else if (format === 'html') {
      const html = generateHTMLReport(reportData);
      const dataBlob = new Blob([html], { type: 'text/html' });
      const url = URL.createObjectURL(dataBlob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `boletin-${student.studentId}-${course.name}.html`;
      link.click();
      URL.revokeObjectURL(url);
    } else {
      // For PDF, we'll show a success message (in a real app, you'd use a PDF library)
      console.log('PDF export:', reportData);
    }

    setExported(true);
    setTimeout(() => {
      onClose();
    }, 1500);
  };

  const generateHTMLReport = (data: any): string => {
    return `
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Boletín - ${data.student.name}</title>
  <style>
    body { font-family: Arial, sans-serif; max-width: 800px; margin: 40px auto; padding: 20px; }
    h1 { color: #1e3a8a; border-bottom: 2px solid #1e3a8a; padding-bottom: 10px; }
    table { width: 100%; border-collapse: collapse; margin: 20px 0; }
    th, td { padding: 12px; text-align: left; border: 1px solid #ddd; }
    th { background-color: #f1f5f9; font-weight: 600; }
    .summary { background-color: #dbeafe; padding: 15px; border-radius: 8px; margin-top: 20px; }
    .footer { margin-top: 40px; font-size: 12px; color: #666; }
  </style>
</head>
<body>
  <h1>Boletín Académico</h1>
  <h2>${data.course}</h2>
  <p><strong>Estudiante:</strong> ${data.student.name}</p>
  <p><strong>ID:</strong> ${data.student.id}</p>
  <p><strong>Correo:</strong> ${data.student.email}</p>
  
  <table>
    <thead>
      <tr>
        <th>Actividad</th>
        <th>Ponderación</th>
        <th>Calificación</th>
      </tr>
    </thead>
    <tbody>
      ${data.grades.map((g: any) => `
        <tr>
          <td>${g.activity}</td>
          <td>${g.percentage}%</td>
          <td>${g.grade !== null ? g.grade.toFixed(1) : 'Sin nota'}</td>
        </tr>
      `).join('')}
    </tbody>
  </table>
  
  <div class="summary">
    <strong>Promedio Ponderado:</strong> ${data.weightedAverage !== null ? data.weightedAverage.toFixed(2) : 'No disponible'}
  </div>
  
  <div class="footer">
    <p>Fecha de exportación: ${new Date(data.exportDate).toLocaleDateString('es-ES')}</p>
    <p>Generado por EdTech - Gestión Académica</p>
  </div>
</body>
</html>
    `;
  };

  return (
    <Dialog open={true} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Exportar Boletín</DialogTitle>
        </DialogHeader>
        <div className="space-y-4 py-4">
          <div>
            <p className="text-sm text-muted-foreground mb-2">Estudiante:</p>
            <p className="font-medium text-foreground">{student.name}</p>
            <p className="text-sm text-muted-foreground">ID: {student.studentId}</p>
          </div>

          {showWarning && !exported && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>
                Este estudiante tiene notas vacías. El boletín mostrará "Sin nota" en esas
                actividades.
              </AlertDescription>
            </Alert>
          )}

          {exported && (
            <Alert className="border-green-200 bg-green-50 text-green-900">
              <CheckCircle2 className="h-4 w-4 text-green-600" />
              <AlertDescription>
                Boletín exportado exitosamente en formato {format.toUpperCase()}
              </AlertDescription>
            </Alert>
          )}

          <div className="space-y-3">
            <Label>Selecciona el formato de exportación:</Label>
            <div className="space-y-2">
              <button
                onClick={() => setFormat('pdf')}
                className={`w-full flex items-center gap-3 p-4 rounded-lg border-2 transition-all ${
                  format === 'pdf'
                    ? 'border-primary bg-accent'
                    : 'border-border bg-card hover:border-primary/50'
                }`}
              >
                <FileText className="w-5 h-5 text-primary" />
                <div className="text-left">
                  <p className="font-medium text-foreground">PDF</p>
                  <p className="text-xs text-muted-foreground">
                    Documento portable para imprimir
                  </p>
                </div>
              </button>

              <button
                onClick={() => setFormat('html')}
                className={`w-full flex items-center gap-3 p-4 rounded-lg border-2 transition-all ${
                  format === 'html'
                    ? 'border-primary bg-accent'
                    : 'border-border bg-card hover:border-primary/50'
                }`}
              >
                <FileText className="w-5 h-5 text-primary" />
                <div className="text-left">
                  <p className="font-medium text-foreground">HTML</p>
                  <p className="text-xs text-muted-foreground">Página web para navegadores</p>
                </div>
              </button>

              <button
                onClick={() => setFormat('json')}
                className={`w-full flex items-center gap-3 p-4 rounded-lg border-2 transition-all ${
                  format === 'json'
                    ? 'border-primary bg-accent'
                    : 'border-border bg-card hover:border-primary/50'
                }`}
              >
                <FileJson className="w-5 h-5 text-primary" />
                <div className="text-left">
                  <p className="font-medium text-foreground">JSON</p>
                  <p className="text-xs text-muted-foreground">
                    Datos estructurados para integración
                  </p>
                </div>
              </button>
            </div>
          </div>
        </div>
        <DialogFooter>
          <Button type="button" variant="outline" onClick={onClose}>
            Cancelar
          </Button>
          <Button onClick={handleExport} disabled={exported}>
            <Download className="w-4 h-4 mr-2" />
            Exportar {format.toUpperCase()}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
