import { useState, useEffect } from 'react';
import { useAppData, Activity } from '../contexts/AppDataContext';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from './ui/dialog';
import { Button } from './ui/button';
import { Input } from './ui/input';
import { Label } from './ui/label';
import { Alert, AlertDescription } from './ui/alert';
import { AlertCircle, CheckCircle2, Plus, Trash2, Percent } from 'lucide-react';

interface EditProgramModalProps {
  courseId: string;
  currentActivities: Activity[];
  isOpen: boolean;
  onClose: () => void;
}

interface ActivityForm {
  id: string;
  name: string;
  percentage: string;
  errors: { name?: string; percentage?: string };
}

export function EditProgramModal({
  courseId,
  currentActivities,
  isOpen,
  onClose,
}: EditProgramModalProps) {
  const [activities, setActivities] = useState<ActivityForm[]>([]);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const { updateCourseActivities } = useAppData();

  useEffect(() => {
    if (isOpen) {
      if (currentActivities.length > 0) {
        setActivities(
          currentActivities.map((a) => ({
            id: a.id,
            name: a.name,
            percentage: a.percentage.toString(),
            errors: {},
          }))
        );
      } else {
        setActivities([
          { id: Date.now().toString(), name: '', percentage: '', errors: {} },
        ]);
      }
    }
  }, [isOpen, currentActivities]);

  const addActivity = () => {
    setActivities([
      ...activities,
      { id: Date.now().toString(), name: '', percentage: '', errors: {} },
    ]);
  };

  const removeActivity = (id: string) => {
    if (activities.length > 1) {
      setActivities(activities.filter((a) => a.id !== id));
    }
  };

  const updateActivity = (id: string, field: 'name' | 'percentage', value: string) => {
    setActivities(
      activities.map((a) =>
        a.id === id ? { ...a, [field]: value, errors: {} } : a
      )
    );
  };

  const distributeEqually = () => {
    const percentage = (100 / activities.length).toFixed(2);
    setActivities(
      activities.map((a) => ({ ...a, percentage, errors: {} }))
    );
  };

  const validateActivities = (): boolean => {
    let isValid = true;
    const names = new Set<string>();
    let newActivities = [...activities];

    // Validate each activity
    newActivities = newActivities.map((activity) => {
      const errors: { name?: string; percentage?: string } = {};

      // Validate name
      if (!activity.name.trim()) {
        errors.name = 'El nombre es obligatorio';
        isValid = false;
      } else if (names.has(activity.name.toLowerCase())) {
        errors.name = 'Nombre duplicado';
        isValid = false;
      } else {
        names.add(activity.name.toLowerCase());
      }

      // Validate percentage
      const percentage = parseFloat(activity.percentage);
      if (isNaN(percentage) || percentage <= 0) {
        errors.percentage = 'Debe ser mayor a 0';
        isValid = false;
      }

      return { ...activity, errors };
    });

    setActivities(newActivities);

    // Validate total
    const total = newActivities.reduce(
      (sum, a) => sum + (parseFloat(a.percentage) || 0),
      0
    );

    if (Math.abs(total - 100) > 0.01) {
      setError(`La suma debe ser 100%. Actualmente es ${total.toFixed(1)}%`);
      isValid = false;
    }

    return isValid;
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess(false);

    if (!validateActivities()) {
      return;
    }

    const validActivities: Activity[] = activities.map((a) => ({
      id: a.id,
      name: a.name.trim(),
      percentage: parseFloat(a.percentage),
    }));

    const result = updateCourseActivities(courseId, validActivities);
    if (result) {
      setSuccess(true);
      setTimeout(() => {
        onClose();
      }, 1000);
    } else {
      setError('Error al guardar el programa evaluativo');
    }
  };

  const handleClose = () => {
    setError('');
    setSuccess(false);
    onClose();
  };

  const totalPercentage = activities.reduce(
    (sum, a) => sum + (parseFloat(a.percentage) || 0),
    0
  );
  const isTotal100 = Math.abs(totalPercentage - 100) < 0.01;

  return (
    <Dialog open={isOpen} onOpenChange={handleClose}>
      <DialogContent className="sm:max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>Programa Evaluativo</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit}>
          <div className="space-y-4 py-4">
            <div className="flex items-center justify-between">
              <p className="text-sm text-muted-foreground">
                Define las actividades y su ponderación
              </p>
              <Button type="button" variant="outline" size="sm" onClick={distributeEqually}>
                <Percent className="w-4 h-4 mr-2" />
                Distribuir equitativamente
              </Button>
            </div>

            <div className="space-y-3">
              {activities.map((activity, index) => (
                <div key={activity.id} className="bg-muted/50 rounded-lg p-4 border border-border">
                  <div className="flex items-start gap-3">
                    <div className="flex-1 grid grid-cols-2 gap-3">
                      <div className="space-y-1">
                        <Label htmlFor={`name-${activity.id}`} className="text-sm">
                          Actividad {index + 1}
                        </Label>
                        <Input
                          id={`name-${activity.id}`}
                          value={activity.name}
                          onChange={(e) =>
                            updateActivity(activity.id, 'name', e.target.value)
                          }
                          placeholder="Nombre de la actividad"
                          className={activity.errors.name ? 'border-red-500' : ''}
                        />
                        {activity.errors.name && (
                          <p className="text-xs text-red-600">{activity.errors.name}</p>
                        )}
                      </div>
                      <div className="space-y-1">
                        <Label htmlFor={`percentage-${activity.id}`} className="text-sm">
                          Porcentaje (%)
                        </Label>
                        <Input
                          id={`percentage-${activity.id}`}
                          type="number"
                          step="0.01"
                          min="0"
                          max="100"
                          value={activity.percentage}
                          onChange={(e) =>
                            updateActivity(activity.id, 'percentage', e.target.value)
                          }
                          placeholder="0.00"
                          className={activity.errors.percentage ? 'border-red-500' : ''}
                        />
                        {activity.errors.percentage && (
                          <p className="text-xs text-red-600">{activity.errors.percentage}</p>
                        )}
                      </div>
                    </div>
                    {activities.length > 1 && (
                      <Button
                        type="button"
                        variant="ghost"
                        size="sm"
                        onClick={() => removeActivity(activity.id)}
                        className="text-red-600 hover:text-red-700 hover:bg-red-50 mt-6"
                      >
                        <Trash2 className="w-4 h-4" />
                      </Button>
                    )}
                  </div>
                </div>
              ))}
            </div>

            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={addActivity}
              className="w-full"
            >
              <Plus className="w-4 h-4 mr-2" />
              Agregar actividad
            </Button>

            <div className="bg-card border border-border rounded-lg p-4">
              <div className="flex items-center justify-between">
                <span className="font-semibold text-foreground">Total</span>
                <span
                  className={`text-lg font-semibold ${
                    isTotal100 ? 'text-green-600' : 'text-orange-600'
                  }`}
                >
                  {totalPercentage.toFixed(2)}%
                </span>
              </div>
              {!isTotal100 && (
                <p className="text-xs text-orange-600 mt-2">
                  La suma debe ser exactamente 100%
                </p>
              )}
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
                <AlertDescription>Programa guardado exitosamente</AlertDescription>
              </Alert>
            )}
          </div>
          <DialogFooter>
            <Button type="button" variant="outline" onClick={handleClose}>
              Cancelar
            </Button>
            <Button type="submit">Guardar programa</Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
