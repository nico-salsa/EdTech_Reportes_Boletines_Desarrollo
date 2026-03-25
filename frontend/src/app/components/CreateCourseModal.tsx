import { useState } from 'react';
import { useAppData } from '../contexts/AppDataContext';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Alert, AlertDescription } from './ui/alert';
import { AlertCircle, CheckCircle2 } from 'lucide-react';

interface CreateCourseModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export function CreateCourseModal({ isOpen, onClose }: CreateCourseModalProps) {
  const [courseName, setCourseName] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const { addCourse } = useAppData();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess(false);

    if (!courseName.trim()) {
      setError('El nombre del curso es obligatorio');
      return;
    }

    const result = addCourse(courseName);
    if (result) {
      setSuccess(true);
      setTimeout(() => {
        setCourseName('');
        setSuccess(false);
        onClose();
      }, 1000);
    } else {
      setError('Ya existe un curso con este nombre');
    }
  };

  const handleClose = () => {
    setCourseName('');
    setError('');
    setSuccess(false);
    onClose();
  };

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Crear nuevo curso</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="courseName">Nombre del curso</Label>
              <Input
                id="courseName"
                value={courseName}
                onChange={(e) => setCourseName(e.target.value)}
                placeholder="Ej: Matemáticas Avanzadas"
                className="w-full"
              />
            </div>

            {error && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            {success && (
              <Alert className="border-green-200 bg-green-50 text-green-900">
                <CheckCircle2 className="h-4 w-4 text-green-600" />
                <AlertDescription>Curso creado exitosamente</AlertDescription>
              </Alert>
            )}
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancelar
            </Button>
            <Button type="submit">Guardar curso</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
