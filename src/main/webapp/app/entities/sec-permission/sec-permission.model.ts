export type TargetType = 'ENTITY' | 'ATTRIBUTE' | 'ROW_POLICY' | 'FETCH_PLAN';

export type SecPermissionEffect = 'ALLOW' | 'DENY';

export interface ISecPermission {
  id: number;
  roleCode?: string | null;
  targetType?: TargetType | null;
  target?: string | null;
  action?: string | null;
  effect?: SecPermissionEffect | null;
}

export type NewSecPermission = Omit<ISecPermission, 'id'> & { id: null };
