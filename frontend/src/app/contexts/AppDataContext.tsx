import { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { useAuth } from './AuthContext';

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

interface AppDataContextType {
  courses: Course[];
  addCourse: (name: string) => boolean;
  getCourse: (id: string) => Course | undefined;
  addStudentToCourse: (courseId: string, student: Student) => void;
  updateCourseActivities: (courseId: string, activities: Activity[]) => boolean;
  updateGrade: (courseId: string, studentId: string, activityId: string, grade: number | null) => void;
  getStudentById: (studentId: string) => Student | null;
}

const AppDataContext = createContext<AppDataContextType | undefined>(undefined);

export function AppDataProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth();
  const [courses, setCourses] = useState<Course[]>([]);

  useEffect(() => {
    if (user) {
      const storedCourses = localStorage.getItem(`edtech_courses_${user.id}`);
      if (storedCourses) {
        setCourses(JSON.parse(storedCourses));
      }
    } else {
      setCourses([]);
    }
  }, [user]);

  const saveCourses = (newCourses: Course[]) => {
    if (user) {
      localStorage.setItem(`edtech_courses_${user.id}`, JSON.stringify(newCourses));
      setCourses(newCourses);
    }
  };

  const addCourse = (name: string): boolean => {
    if (!user || !name.trim()) return false;
    
    // Check for duplicate course name
    if (courses.some(c => c.name.toLowerCase() === name.toLowerCase())) {
      return false;
    }

    const newCourse: Course = {
      id: Date.now().toString(),
      name,
      teacherId: user.id,
      students: [],
      activities: [],
      grades: [],
      createdAt: new Date().toISOString(),
    };

    saveCourses([...courses, newCourse]);
    return true;
  };

  const getCourse = (id: string): Course | undefined => {
    return courses.find(c => c.id === id);
  };

  const addStudentToCourse = (courseId: string, student: Student) => {
    const updatedCourses = courses.map(course => {
      if (course.id === courseId) {
        // Check if student already exists in course
        if (course.students.some(s => s.studentId === student.studentId)) {
          return course;
        }
        return {
          ...course,
          students: [...course.students, student],
        };
      }
      return course;
    });
    saveCourses(updatedCourses);
    
    // Save student globally
    const allStudents = JSON.parse(localStorage.getItem('edtech_all_students') || '[]');
    if (!allStudents.some((s: Student) => s.studentId === student.studentId)) {
      allStudents.push(student);
      localStorage.setItem('edtech_all_students', JSON.stringify(allStudents));
    }
  };

  const updateCourseActivities = (courseId: string, activities: Activity[]): boolean => {
    // Validate activities
    const totalPercentage = activities.reduce((sum, a) => sum + a.percentage, 0);
    if (Math.abs(totalPercentage - 100) > 0.01) {
      return false;
    }

    const updatedCourses = courses.map(course => {
      if (course.id === courseId) {
        return {
          ...course,
          activities,
        };
      }
      return course;
    });
    saveCourses(updatedCourses);
    return true;
  };

  const updateGrade = (courseId: string, studentId: string, activityId: string, grade: number | null) => {
    const updatedCourses = courses.map(course => {
      if (course.id === courseId) {
        const gradeIndex = course.grades.findIndex(
          g => g.studentId === studentId && g.activityId === activityId
        );

        let newGrades = [...course.grades];
        if (gradeIndex >= 0) {
          newGrades[gradeIndex] = { studentId, activityId, grade };
        } else {
          newGrades.push({ studentId, activityId, grade });
        }

        return {
          ...course,
          grades: newGrades,
        };
      }
      return course;
    });
    saveCourses(updatedCourses);
  };

  const getStudentById = (studentId: string): Student | null => {
    const allStudents = JSON.parse(localStorage.getItem('edtech_all_students') || '[]');
    return allStudents.find((s: Student) => s.studentId === studentId) || null;
  };

  return (
    <AppDataContext.Provider
      value={{
        courses,
        addCourse,
        getCourse,
        addStudentToCourse,
        updateCourseActivities,
        updateGrade,
        getStudentById,
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
