import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { useAuth } from './AuthContext';
import { requestJson } from '../lib/api';

export interface Student {
  id: string;
  studentId: string;
  name: string;
  email: string;
}

export interface Activity {
  id: string;
  name: string;
  percentage: number;
}

export interface Grade {
  studentId: string;
  activityId: string;
  grade: number | null;
}

export interface Course {
  id: string;
  name: string;
  teacherId: string;
  students: Student[];
  activities: Activity[];
  grades: Grade[];
  createdAt: string;
}

interface CourseListItem {
  id: string;
}

interface AppDataContextType {
  courses: Course[];
  isLoading: boolean;
  addCourse: (name: string) => Promise<boolean>;
  getCourse: (id: string) => Course | undefined;
  addStudentToCourse: (courseId: string, student: Student) => Promise<void>;
  updateCourseActivities: (courseId: string, activities: Activity[]) => Promise<boolean>;
  updateGrade: (courseId: string, studentId: string, activityId: string, grade: number | null) => Promise<void>;
  getStudentById: (studentId: string) => Promise<Student | null>;
  refreshCourses: () => Promise<void>;
}

const AppDataContext = createContext<AppDataContextType | undefined>(undefined);

export function AppDataProvider({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  const [courses, setCourses] = useState<Course[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!isAuthenticated) {
      setCourses([]);
      setIsLoading(false);
      return;
    }

    void refreshCourses();
  }, [isAuthenticated]);

  const refreshCourses = async () => {
    if (!isAuthenticated) {
      setCourses([]);
      return;
    }

    setIsLoading(true);
    try {
      const summaries = await requestJson<CourseListItem[]>('/courses');
      const detailedCourses = await Promise.all(
        summaries.map((course) => requestJson<Course>(`/courses/${course.id}`)),
      );
      setCourses(detailedCourses);
    } finally {
      setIsLoading(false);
    }
  };

  const addCourse = async (name: string): Promise<boolean> => {
    try {
      const course = await requestJson<Course>('/courses', {
        method: 'POST',
        body: JSON.stringify({ name }),
      });
      setCourses((current) => [course, ...current]);
      return true;
    } catch {
      return false;
    }
  };

  const getCourse = (id: string): Course | undefined => {
    return courses.find((course) => course.id === id);
  };

  const replaceCourse = (updatedCourse: Course) => {
    setCourses((current) => current.map((course) => (course.id === updatedCourse.id ? updatedCourse : course)));
  };

  const addStudentToCourse = async (courseId: string, student: Student) => {
    const updatedCourse = await requestJson<Course>(`/courses/${courseId}/students`, {
      method: 'POST',
      body: JSON.stringify(student),
    });
    replaceCourse(updatedCourse);
  };

  const updateCourseActivities = async (courseId: string, activities: Activity[]): Promise<boolean> => {
    try {
      const updatedCourse = await requestJson<Course>(`/courses/${courseId}/activities`, {
        method: 'PUT',
        body: JSON.stringify(activities),
      });
      replaceCourse(updatedCourse);
      return true;
    } catch {
      return false;
    }
  };

  const updateGrade = async (courseId: string, studentId: string, activityId: string, grade: number | null) => {
    const updatedCourse = await requestJson<Course>(`/courses/${courseId}/grades`, {
      method: 'PUT',
      body: JSON.stringify({ studentId, activityId, grade }),
    });
    replaceCourse(updatedCourse);
  };

  const getStudentById = async (studentId: string): Promise<Student | null> => {
    try {
      return await requestJson<Student>(`/students/${studentId}`);
    } catch {
      return null;
    }
  };

  return (
    <AppDataContext.Provider
      value={{
        courses,
        isLoading,
        addCourse,
        getCourse,
        addStudentToCourse,
        updateCourseActivities,
        updateGrade,
        getStudentById,
        refreshCourses,
      }}
    >
      {children}
    </AppDataContext.Provider>
  );
}

export function useAppData() {
  const context = useContext(AppDataContext);
  if (!context) {
    throw new Error('useAppData must be used within AppDataProvider');
  }
  return context;
}
