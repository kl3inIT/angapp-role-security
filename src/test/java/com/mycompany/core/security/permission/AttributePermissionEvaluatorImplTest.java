package com.mycompany.core.security.permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mycompany.core.security.core.SecurityService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AttributePermissionEvaluatorImplTest {

    @Test
    void shouldNormalizeAttributeTargetToUpperCase() {
        RolePermissionService rolePermissionService = Mockito.mock(RolePermissionService.class);
        SecurityService securityService = Mockito.mock(SecurityService.class);
        when(securityService.currentAuthorities()).thenReturn(List.of("ACCOUNTANT_ROLE"));
        when(
            rolePermissionService.hasPermission(anyCollection(), eq(TargetType.ATTRIBUTE), eq("ORGANIZATION.CODE"), eq("VIEW"))
        ).thenReturn(true);

        AttributePermissionEvaluatorImpl evaluator = new AttributePermissionEvaluatorImpl(rolePermissionService, securityService);

        boolean permitted = evaluator.canView(TestOrganization.class, "code");

        assertThat(permitted).isTrue();
        verify(rolePermissionService).hasPermission(anyCollection(), eq(TargetType.ATTRIBUTE), eq("ORGANIZATION.CODE"), eq("VIEW"));
    }

    private static final class TestOrganization {}
}
