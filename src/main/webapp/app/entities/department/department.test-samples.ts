import { IDepartment, NewDepartment } from './department.model';

export const sampleWithRequiredData: IDepartment = {
  id: 32001,
};

export const sampleWithPartialData: IDepartment = {
  id: 8345,
};

export const sampleWithFullData: IDepartment = {
  id: 13276,
  name: 'endow',
  code: 'headline save',
};

export const sampleWithNewData: NewDepartment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
