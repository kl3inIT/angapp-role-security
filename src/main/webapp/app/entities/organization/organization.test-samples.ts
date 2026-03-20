import { IOrganization, NewOrganization } from './organization.model';

export const sampleWithRequiredData: IOrganization = {
  id: 20534,
  code: 'flawed mockingly',
  name: 'relieve draft immediately',
};

export const sampleWithPartialData: IOrganization = {
  id: 27772,
  code: 'regularly equate',
  name: 'against service onto',
};

export const sampleWithFullData: IOrganization = {
  id: 12562,
  code: 'coarse',
  name: 'bonnet minus hm',
  description: 'incidentally fooey once',
};

export const sampleWithNewData: NewOrganization = {
  code: 'negative coolly and',
  name: 'indeed fiddle council',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
