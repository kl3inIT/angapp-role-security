export type SecRowPolicyType = 'SPECIFICATION' | 'JPQL' | 'JAVA';
export type EntityOp = 'READ' | 'CREATE' | 'UPDATE' | 'DELETE';

export interface ISecRowPolicy {
  id: number;
  code?: string | null;
  entityName?: string | null;
  operation?: EntityOp | null;
  policyType?: SecRowPolicyType | null;
  expression?: string | null;
}

export type NewSecRowPolicy = Omit<ISecRowPolicy, 'id'> & { id: null };
