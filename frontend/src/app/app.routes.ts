import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: 'dashboard',
    loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.dashboardRoutes)
  },
  {
    path: 'leads',
    loadChildren: () => import('./features/leads/leads.routes').then(m => m.leadsRoutes)
  },
  {
    path: 'students',
    loadChildren: () => import('./features/students/students.routes').then(m => m.studentsRoutes)
  },
  {
    path: 'batches',
    loadChildren: () => import('./features/batches/batches.routes').then(m => m.batchesRoutes)
  },
  {
    path: 'employees',
    loadChildren: () => import('./features/employees/employees.routes').then(m => m.employeesRoutes)
  },
  {
    path: 'placements',
    loadChildren: () => import('./features/placements/placements.routes').then(m => m.placementsRoutes)
  },
  {
    path: 'reports',
    loadChildren: () => import('./features/reports/reports.routes').then(m => m.reportsRoutes)
  },
  {
    path: '**',
    redirectTo: '/dashboard'
  }
];