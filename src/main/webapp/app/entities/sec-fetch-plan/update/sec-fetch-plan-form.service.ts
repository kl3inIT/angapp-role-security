import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ISecFetchPlan, NewSecFetchPlan } from '../sec-fetch-plan.model';

type SecFetchPlanFormGroupInput = ISecFetchPlan | (Partial<Omit<NewSecFetchPlan, 'id'>> & { id: NewSecFetchPlan['id'] });

type SecFetchPlanFormDefaults = Pick<NewSecFetchPlan, 'id'>;

type SecFetchPlanFormGroupContent = {
  id: FormControl<ISecFetchPlan['id'] | NewSecFetchPlan['id']>;
  code: FormControl<ISecFetchPlan['code']>;
  entityName: FormControl<ISecFetchPlan['entityName']>;
  definitionJson: FormControl<ISecFetchPlan['definitionJson']>;
};

export type SecFetchPlanFormGroup = FormGroup<SecFetchPlanFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SecFetchPlanFormService {
  createSecFetchPlanFormGroup(plan: SecFetchPlanFormGroupInput = { id: null }): SecFetchPlanFormGroup {
    const rawValue = {
      ...this.getFormDefaults(),
      ...plan,
    };
    return new FormGroup<SecFetchPlanFormGroupContent>({
      id: new FormControl(
        { value: rawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      code: new FormControl(rawValue.code, { validators: [Validators.required, Validators.maxLength(100)] }),
      entityName: new FormControl(rawValue.entityName, { validators: [Validators.required, Validators.maxLength(255)] }),
      definitionJson: new FormControl(rawValue.definitionJson, { validators: [Validators.required] }),
    });
  }

  getSecFetchPlan(form: SecFetchPlanFormGroup): ISecFetchPlan | NewSecFetchPlan {
    return form.getRawValue() as ISecFetchPlan | NewSecFetchPlan;
  }

  resetForm(form: SecFetchPlanFormGroup, plan: SecFetchPlanFormGroupInput): void {
    const rawValue = { ...this.getFormDefaults(), ...plan };
    form.reset({
      ...rawValue,
      id: { value: rawValue.id, disabled: true },
    } as any);
  }

  private getFormDefaults(): SecFetchPlanFormDefaults {
    return {
      id: null,
    };
  }
}
