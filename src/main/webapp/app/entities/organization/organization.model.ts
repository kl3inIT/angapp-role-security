export interface IOrganization {
  id: number;
  code?: string | null;
  name?: string | null;
  description?: string | null;
}

export type NewOrganization = Omit<IOrganization, 'id'> & { id: null };
