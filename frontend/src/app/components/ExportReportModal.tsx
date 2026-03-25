import { useState } from 'react';
import { Course } from '../contexts/AppDataContext';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';
import { Button } from './ui/button';
import { Label } from './ui/label';
import { Alert, AlertDescription } from './ui/alert';
import { AlertCircle, CheckCircle2, FileText, FileJson, Download } from 'lucide-react';
import { ApiError, requestBlob } from '../lib/api';

interface ExportReportModalProps {
  course: Course;
  studentId: string;
  hasEmptyGrades: boolean;
  onClose: () => void;
}

export function ExportReportModal({ course, studentId, hasEmptyGrades, onClose }: ExportReportModalProps) {
  const [format, setFormat] = useState<'pdf' | 'html' | 'json'>('pdf');
  const [exported, setExported] = useState(false);
  const [error, setError] = useState('');
  const [isExporting, setIsExporting] = useState(false);

  const student = course.students.find((item) => item.studentId === studentId);

  if (!student) {
    return null;
  }

  const handleExport = async () => {
    setError('');
    setIsExporting(true);

    try {
      const { blob, filename } = await requestBlob(`/courses/${course.id}/students/${student.studentId}/report?format=${format}`);
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = filename ?? `boletin-${student.studentId}.${format}`;
      link.click();
      URL.revokeObjectURL(url);
      setExported(true);
      setTimeout(() => onClose(), 1200);
    } catch (exception) {
      if (exception instanceof ApiError) {
        setError(exception.message);
      } else {
        setError('No fue posible exportar el boletin');
      }
    } finally {
      setIsExporting(false);
    }
  };

  return (
    <Dialog open={true} onOpenChange={onClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Exportar Boletin</DialogTitle>
        </DialogHeader>
        <div className="space-y-4 py-4">
          <div>
            <p className="text-sm text-muted-foreground mb-2">Estudiante:</p>
            <p className="font-medium text-foreground">{student.name}</p>
            <p className="text-sm text-muted-foreground">ID: {student.studentId}</p>
          </div>

          {hasEmptyGrades && !exported && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>
                Este estudiante tiene notas vacias. El boletin se generara con esas notas tratadas como 0 en el promedio.
              </AlertDescription>
            </Alert>
          )}

          {error && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {exported && (
            <Alert className="border-green-200 bg-green-50 text-green-900">
              <CheckCircle2 className="h-4 w-4 text-green-600" />
              <AlertDescription>Boletin exportado exitosamente en formato {format.toUpperCase()}</AlertDescription>
            </Alert>
          )}

          <div className="space-y-3">
            <Label>Selecciona el formato de exportacion:</Label>
            <div className="space-y-2">
              <button onClick={() => setFormat('pdf')} className={`w-full flex items-center gap-3 p-4 rounded-lg border-2 transition-all ${format === 'pdf' ? 'border-primary bg-accent' : 'border-border bg-card hover:border-primary/50'}`}>
                <FileText className="w-5 h-5 text-primary" />
                <div className="text-left">
                  <p className="font-medium text-foreground">PDF</p>
                  <p className="text-xs text-muted-foreground">Documento portable para imprimir</p>
                </div>
              </button>

              <button onClick={() => setFormat('html')} className={`w-full flex items-center gap-3 p-4 rounded-lg border-2 transition-all ${format === 'html' ? 'border-primary bg-accent' : 'border-border bg-card hover:border-primary/50'}`}>
                <FileText className="w-5 h-5 text-primary" />
                <div className="text-left">
                  <p className="font-medium text-foreground">HTML</p>
                  <p className="text-xs text-muted-foreground">Pagina web para navegadores</p>
                </div>
              </button>

              <button onClick={() => setFormat('json')} className={`w-full flex items-center gap-3 p-4 rounded-lg border-2 transition-all ${format === 'json' ? 'border-primary bg-accent' : 'border-border bg-card hover:border-primary/50'}`}>
                <FileJson className="w-5 h-5 text-primary" />
                <div className="text-left">
                  <p className="font-medium text-foreground">JSON</p>
                  <p className="text-xs text-muted-foreground">Datos estructurados para integracion</p>
                </div>
              </button>
            </div>
          </div>
        </div>
        <DialogFooter>
          <Button type="button" variant="outline" onClick={onClose}>Cancelar</Button>
          <Button onClick={() => void handleExport()} disabled={exported || isExporting}>
            <Download className="w-4 h-4 mr-2" />
            {isExporting ? 'Exportando...' : `Exportar ${format.toUpperCase()}`}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
