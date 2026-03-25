import { useState } from 'react';
import { useAppData, Student } from '../contexts/AppDataContext';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Alert, AlertDescription } from './ui/alert';
import { AlertCircle, CheckCircle2, Search } from 'lucide-react';

interface AddStudentModalProps {
  courseId: string;
  isOpen: boolean;
  onClose: () => void;
}

export function AddStudentModal({ courseId, isOpen, onClose }: AddStudentModalProps) {
  const [studentId, setStudentId] = useState('');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const [searched, setSearched] = useState(false);
  const [foundStudent, setFoundStudent] = useState<Student | null>(null);
  const { addStudentToCourse, getStudentById } = useAppData();

  const handleSearch = () => {
    setError('');
    setSuccess(false);
    
    if (!studentId.trim()) {
      setError('El ID del estudiante es obligatorio');
      return;
    }

    const existing = getStudentById(studentId);
    if (existing) {
      setFoundStudent(existing);
      setName(existing.name);
      setEmail(existing.email);
    } else {
      setFoundStudent(null);
      setName('');
      setEmail('');
    }
    setSearched(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess(false);

    if (!studentId.trim()) {
      setError('El ID del estudiante es obligatorio');
      return;
    }

    if (!name.trim()) {
      setError('El nombre completo es obligatorio');
      return;
    }

    if (!email.trim()) {
      setError('El correo electrónico es obligatorio');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError('El correo electrónico no es válido');
      return;
    }

    const newStudent: Student = {
      id: Date.now().toString(),
      studentId,
      name,
      email,
    };

    addStudentToCourse(courseId, newStudent);
    setSuccess(true);
    setTimeout(() => {
      resetForm();
      onClose();
    }, 1000);
  };

  const resetForm = () => {
    setStudentId('');
    setName('');
    setEmail('');
    setError('');
    setSuccess(false);
    setSearched(false);
    setFoundStudent(null);
  };

  const handleClose = () => {
    resetForm();
    onClose();
  };

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-lg">
        <DialogHeader>
          <DialogTitle>Agregar estudiante al curso</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="space-y-4 py-4">
            <div className="space-y-2">
              <Label htmlFor="studentId">ID del estudiante</Label>
              <div className="flex gap-2">
                <Input
                  id="studentId"
                  value={studentId}
                  onChange={(e) => {
                    setStudentId(e.target.value);
                    setSearched(false);
                    setFoundStudent(null);
                  }}
                  placeholder="Ingresa el ID"
                  className="flex-1"
                />
                <Button type="button" onClick={handleSearch}>
                  <Search className="w-4 h-4 mr-2" />
                  Buscar
                </Button>
              </div>
            </div>

            {searched && foundStudent && (
              <Alert className="border-blue-200 bg-blue-50 text-blue-900">
                <CheckCircle2 className="h-4 w-4 text-blue-600" />
                <AlertDescription>
                  Estudiante encontrado. Los datos se autocompletaron.
                </AlertDescription>
              </Alert>
            )}

            {searched && !foundStudent && (
              <Alert className="border-yellow-200 bg-yellow-50 text-yellow-900">
                <AlertCircle className="h-4 w-4 text-yellow-600" />
                <AlertDescription>
                  ID no encontrado. Completa los datos para registrar un nuevo estudiante.
                </AlertDescription>
              </Alert>
            )}

            {searched && (
              <>
                <div className="space-y-2">
                  <Label htmlFor="name">Nombre completo</Label>
                  <Input
                    id="name"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Nombre del estudiante"
                    className="w-full"
                    disabled={!!foundStudent}
                  />
                </div>

                <div className="space-y-2">
                  <Label htmlFor="email">Correo electrónico</Label>
                  <Input
                    id="email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="correo@ejemplo.com"
                    className="w-full"
                    disabled={!!foundStudent}
                  />
                </div>
              </>
            )}

            {error && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            {success && (
              <Alert className="border-green-200 bg-green-50 text-green-900">
                <CheckCircle2 className="h-4 w-4 text-green-600" />
                <AlertDescription>Estudiante agregado exitosamente</AlertDescription>
              </Alert>
            )}
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancelar
            </Button>
            <Button type="submit" disabled={!searched}>
              Agregar al curso
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
