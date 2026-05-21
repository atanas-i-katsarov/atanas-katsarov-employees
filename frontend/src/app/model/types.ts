
export interface EmployeeCsvResponse {
  employee1Id?: number;
  employee2Id?: number;
  totalDaysWorked: number;
  projectOverlaps?: ProjectOverlap[];
}

export interface ProjectOverlap {
  projectId: number;
  daysWorked: number;
}