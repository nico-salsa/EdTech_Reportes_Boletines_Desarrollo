import { useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuth } from '../contexts/AuthContext';
import { useAppData } from '../contexts/AppDataContext';
import { Button } from '../components/ui/button';
import { BookOpen, Plus, Users, LogOut, FolderOpen } from 'lucide-react';
import { CreateCourseModal } from '../components/CreateCourseModal';

export default function Dashboard() {
  const { user, logout } = useAuth();
  const { courses } = useAppData();
  const navigate = useNavigate();
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-background">
      {/* Header */}
      <header className="bg-card border-b border-border">
        <div className="max-w-7xl mx-auto px-6 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center">
                <BookOpen className="w-6 h-6 text-primary-foreground" />
              </div>
              <div>
                <h1 className="text-lg font-semibold text-foreground">EdTech</h1>
                <p className="text-xs text-muted-foreground">Gestión Académica</p>
              </div>
            </div>
            <div className="flex items-center gap-4">
              <div className="text-right">
                <p className="text-sm font-medium text-foreground">{user?.username}</p>
                <p className="text-xs text-muted-foreground">Docente</p>
              </div>
              <Button variant="outline" size="sm" onClick={handleLogout}>
                <LogOut className="w-4 h-4 mr-2" />
                Salir
              </Button>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        <div className="mb-8">
          <div className="flex items-center justify-between mb-2">
            <h2 className="text-2xl font-semibold text-foreground">Mis Cursos</h2>
            <Button onClick={() => setIsCreateModalOpen(true)}>
              <Plus className="w-4 h-4 mr-2" />
              Crear curso
            </Button>
          </div>
          <p className="text-sm text-muted-foreground">
            Gestiona tus cursos, estudiantes y evaluaciones
          </p>
        </div>

        {courses.length === 0 ? (
          <div className="bg-card border border-dashed border-border rounded-lg p-12 text-center">
            <div className="flex justify-center mb-4">
              <div className="w-16 h-16 bg-muted rounded-full flex items-center justify-center">
                <FolderOpen className="w-8 h-8 text-muted-foreground" />
              </div>
            </div>
            <h3 className="text-lg font-medium text-foreground mb-2">
              No tienes cursos creados
            </h3>
            <p className="text-sm text-muted-foreground mb-6 max-w-md mx-auto">
              Comienza creando tu primer curso para registrar estudiantes, actividades evaluativas
              y calificaciones
            </p>
            <Button onClick={() => setIsCreateModalOpen(true)}>
              <Plus className="w-4 h-4 mr-2" />
              Crear primer curso
            </Button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {courses.map((course) => (
              <button
                key={course.id}
                onClick={() => navigate(`/courses/${course.id}`)}
                className="bg-card border border-border rounded-lg p-6 text-left hover:border-primary hover:shadow-md transition-all group"
              >
                <div className="flex items-start justify-between mb-4">
                  <div className="w-12 h-12 bg-accent rounded-lg flex items-center justify-center group-hover:bg-primary group-hover:text-primary-foreground transition-colors">
                    <BookOpen className="w-6 h-6" />
                  </div>
                </div>
                <h3 className="text-lg font-semibold text-foreground mb-2 group-hover:text-primary transition-colors">
                  {course.name}
                </h3>
                <div className="flex items-center gap-4 text-sm text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <Users className="w-4 h-4" />
                    <span>{course.students.length} estudiantes</span>
                  </div>
                </div>
                <div className="mt-4 pt-4 border-t border-border">
                  <p className="text-xs text-muted-foreground">
                    {course.activities.length} actividades evaluativas
                  </p>
                </div>
              </button>
            ))}
          </div>
        )}
      </main>

      <CreateCourseModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
      />
    </div>
  );
}
