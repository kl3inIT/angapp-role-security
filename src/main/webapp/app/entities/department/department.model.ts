export interface IDepartment {
  id: number;
  name?: string | null;
  code?: string | null;
}

export type NewDepartment = Omit<IDepartment, 'id'> & { id: null };
