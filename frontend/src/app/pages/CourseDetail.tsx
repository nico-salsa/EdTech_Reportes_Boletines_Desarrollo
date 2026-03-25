import { useParams, useNavigate } from 'react-router';
import { useAppData } from '../contexts/AppDataContext';
import { Button } from '../components/ui/button';
import { ArrowLeft, BookOpen, Users, ClipboardList, Plus } from 'lucide-react';
import { AddStudentModal } from '../components/AddStudentModal';
import { EditProgramModal } from '../components/EditProgramModal';
import { GradesTable } from '../components/GradesTable';
import { useState } from 'react';

export default function CourseDetail() {
  const { courseId } = useParams<{ courseId: string }>();
  const navigate = useNavigate();
  const { getCourse, isLoading } = useAppData();
  const [isAddStudentModalOpen, setIsAddStudentModalOpen] = useState(false);
  const [isEditProgramModalOpen, setIsEditProgramModalOpen] = useState(false);

  const course = getCourse(courseId || '');

  if (isLoading && !course) {
    return <div className="min-h-screen bg-background flex items-center justify-center text-muted-foreground">Cargando curso...</div>;
  }

  if (!course) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="text-center">
          <h2 className="text-xl font-semibold text-foreground mb-2">Curso no encontrado</h2>
          <Button onClick={() => navigate('/dashboard')}>Volver al dashboard</Button>
        </div>
      </div>
    );
  }

  const totalPercentage = course.activities.reduce((sum, activity) => sum + activity.percentage, 0);
  const isProgramValid = Math.abs(totalPercentage - 100) < 0.01;

  return (
    <div className="min-h-screen bg-background">
      <header className="bg-card border-b border-border">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-4">
              <Button variant="ghost" size="sm" onClick={() => navigate('/dashboard')}>
                <ArrowLeft className="w-4 h-4 mr-2" />
                Volver
              </Button>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center">
                  <BookOpen className="w-6 h-6 text-primary-foreground" />
                </div>
                <div>
                  <h1 className="text-lg font-semibold text-foreground">{course.name}</h1>
                  <p className="text-xs text-muted-foreground">Gestion del curso</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
          <div className="bg-card border border-border rounded-lg p-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-blue-100 rounded-lg flex items-center justify-center">
                <Users className="w-5 h-5 text-blue-600" />
              </div>
              <div>
                <p className="text-2xl font-semibold text-foreground">{course.students.length}</p>
                <p className="text-sm text-muted-foreground">Estudiantes</p>
              </div>
            </div>
          </div>
          <div className="bg-card border border-border rounded-lg p-4">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <ClipboardList className="w-5 h-5 text-green-600" />
              </div>
              <div>
                <p className="text-2xl font-semibold text-foreground">{course.activities.length}</p>
                <p className="text-sm text-muted-foreground">Actividades evaluativas</p>
              </div>
            </div>
          </div>
          <div className="bg-card border border-border rounded-lg p-4">
            <div className="flex items-center gap-3">
              <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${isProgramValid ? 'bg-green-100' : 'bg-orange-100'}`}>
                <ClipboardList className={`w-5 h-5 ${isProgramValid ? 'text-green-600' : 'text-orange-600'}`} />
              </div>
              <div>
                <p className="text-2xl font-semibold text-foreground">{totalPercentage.toFixed(0)}%</p>
                <p className="text-sm text-muted-foreground">{isProgramValid ? 'Programa completo' : 'Programa incompleto'}</p>
              </div>
            </div>
          </div>
        </div>

        <div className="bg-card border border-border rounded-lg p-6 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-foreground">Estudiantes</h2>
            <Button size="sm" onClick={() => setIsAddStudentModalOpen(true)}>
              <Plus className="w-4 h-4 mr-2" />
              Agregar estudiante
            </Button>
          </div>
          {course.students.length === 0 ? (
            <p className="text-sm text-muted-foreground py-4">No hay estudiantes registrados. Agrega estudiantes para comenzar.</p>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              {course.students.map((student) => (
                <div key={student.id} className="bg-muted/50 rounded-lg p-3 border border-border">
                  <p className="font-medium text-foreground">{student.name}</p>
                  <p className="text-sm text-muted-foreground">ID: {student.studentId}</p>
                  <p className="text-xs text-muted-foreground">{student.email}</p>
                </div>
              ))}
            </div>
          )}
        </div>

        <div className="bg-card border border-border rounded-lg p-6 mb-6">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h2 className="text-lg font-semibold text-foreground">Programa Evaluativo</h2>
              <p className="text-sm text-muted-foreground">Define las actividades y su ponderacion (debe sumar 100%)</p>
            </div>
            <Button size="sm" onClick={() => setIsEditProgramModalOpen(true)}>
              {course.activities.length === 0 ? (<><Plus className="w-4 h-4 mr-2" />Definir programa</>) : 'Editar programa'}
            </Button>
          </div>
          {course.activities.length === 0 ? (
            <p className="text-sm text-muted-foreground py-4">No hay actividades evaluativas definidas. Define el programa para comenzar a registrar calificaciones.</p>
          ) : (
            <div className="space-y-2">
              {course.activities.map((activity) => (
                <div key={activity.id} className="flex items-center justify-between bg-muted/50 rounded-lg p-3 border border-border">
                  <span className="font-medium text-foreground">{activity.name}</span>
                  <span className="text-sm text-muted-foreground">{activity.percentage}%</span>
                </div>
              ))}
              <div className="flex items-center justify-between pt-2 border-t border-border">
                <span className="font-semibold text-foreground">Total</span>
                <span className={`font-semibold ${isProgramValid ? 'text-green-600' : 'text-orange-600'}`}>{totalPercentage.toFixed(1)}%</span>
              </div>
            </div>
          )}
        </div>

        {course.students.length > 0 && course.activities.length > 0 && (
          <div className="bg-card border border-border rounded-lg p-6">
            <div className="mb-4">
              <h2 className="text-lg font-semibold text-foreground">Tabla de Calificaciones</h2>
              <p className="text-sm text-muted-foreground">Registra las notas de cada estudiante por actividad</p>
            </div>
            <GradesTable course={course} />
          </div>
        )}
      </main>

      <AddStudentModal courseId={course.id} isOpen={isAddStudentModalOpen} onClose={() => setIsAddStudentModalOpen(false)} />
      <EditProgramModal courseId={course.id} currentActivities={course.activities} isOpen={isEditProgramModalOpen} onClose={() => setIsEditProgramModalOpen(false)} />
    </div>
  );
}
