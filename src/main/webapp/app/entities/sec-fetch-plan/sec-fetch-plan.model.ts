export interface ISecFetchPlan {
  id: number;
  code?: string | null;
  entityName?: string | null;
  definitionJson?: string | null;
}

export type NewSecFetchPlan = Omit<ISecFetchPlan, 'id'> & { id: null };
