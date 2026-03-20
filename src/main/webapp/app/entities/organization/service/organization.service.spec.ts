import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { IOrganization } from '../organization.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../organization.test-samples';

import { OrganizationService } from './organization.service';

const requireRestSample: IOrganization = {
  ...sampleWithRequiredData,
};

describe('Organization Service', () => {
  let service: OrganizationService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrganization | IOrganization[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(OrganizationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Organization', () => {
      const organization = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(organization).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Organization', () => {
      const organization = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(organization).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Organization', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Organization', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Organization', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOrganizationToCollectionIfMissing', () => {
      it('should add a Organization to an empty array', () => {
        const organization: IOrganization = sampleWithRequiredData;
        expectedResult = service.addOrganizationToCollectionIfMissing([], organization);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(organization);
      });

      it('should not add a Organization to an array that contains it', () => {
        const organization: IOrganization = sampleWithRequiredData;
        const organizationCollection: IOrganization[] = [
          {
            ...organization,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrganizationToCollectionIfMissing(organizationCollection, organization);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Organization to an array that doesn't contain it", () => {
        const organization: IOrganization = sampleWithRequiredData;
        const organizationCollection: IOrganization[] = [sampleWithPartialData];
        expectedResult = service.addOrganizationToCollectionIfMissing(organizationCollection, organization);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(organization);
      });

      it('should add only unique Organization to an array', () => {
        const organizationArray: IOrganization[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const organizationCollection: IOrganization[] = [sampleWithRequiredData];
        expectedResult = service.addOrganizationToCollectionIfMissing(organizationCollection, ...organizationArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const organization: IOrganization = sampleWithRequiredData;
        const organization2: IOrganization = sampleWithPartialData;
        expectedResult = service.addOrganizationToCollectionIfMissing([], organization, organization2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(organization);
        expect(expectedResult).toContain(organization2);
      });

      it('should accept null and undefined values', () => {
        const organization: IOrganization = sampleWithRequiredData;
        expectedResult = service.addOrganizationToCollectionIfMissing([], null, organization, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(organization);
      });

      it('should return initial array if no Organization is added', () => {
        const organizationCollection: IOrganization[] = [sampleWithRequiredData];
        expectedResult = service.addOrganizationToCollectionIfMissing(organizationCollection, undefined, null);
        expect(expectedResult).toEqual(organizationCollection);
      });
    });

    describe('compareOrganization', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrganization(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15986 };
        const entity2 = null;

        const compareResult1 = service.compareOrganization(entity1, entity2);
        const compareResult2 = service.compareOrganization(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15986 };
        const entity2 = { id: 21219 };

        const compareResult1 = service.compareOrganization(entity1, entity2);
        const compareResult2 = service.compareOrganization(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15986 };
        const entity2 = { id: 15986 };

        const compareResult1 = service.compareOrganization(entity1, entity2);
        const compareResult2 = service.compareOrganization(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
