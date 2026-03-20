export enum SecRoleType {
  RESOURCE = 'RESOURCE',
  ROW_LEVEL = 'ROW_LEVEL',
}

export interface ISecRole {
  id: number;
  code?: string | null;
  name?: string | null;
  type?: SecRoleType | null;
}

export type NewSecRole = Omit<ISecRole, 'id'> & { id: null };
