package com.mycompany.myapp.web.rest;

import com.mycompany.core.security.access.AccessManager;
import com.mycompany.core.security.access.CrudEntityContext;
import com.mycompany.core.security.permission.EntityOp;
import com.mycompany.myapp.domain.Employee;
import com.mycompany.myapp.repository.EmployeeRepository;
import com.mycompany.myapp.service.EmployeeService;
import com.mycompany.myapp.service.dto.EmployeeDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/employees")
public class EmployeeResource {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeResource.class);

    private static final String ENTITY_NAME = "employee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final AccessManager accessManager;

    public EmployeeResource(EmployeeService employeeService, EmployeeRepository employeeRepository, AccessManager accessManager) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.accessManager = accessManager;
    }

    @PostMapping("")
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO employeeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Employee : {}", employeeDTO);
        checkCrudPermission(EntityOp.CREATE);
        if (employeeDTO.getId() != null) {
            throw new BadRequestAlertException("A new employee cannot already have an ID", ENTITY_NAME, "idexists");
        }
        employeeDTO = employeeService.save(employeeDTO);
        return ResponseEntity.created(new URI("/api/employees/" + employeeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, employeeDTO.getId().toString()))
            .body(employeeDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EmployeeDTO employeeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Employee : {}, {}", id, employeeDTO);
        checkCrudPermission(EntityOp.UPDATE);
        if (employeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, employeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!employeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        employeeDTO = employeeService.update(employeeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employeeDTO.getId().toString()))
            .body(employeeDTO);
    }

    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EmployeeDTO> partialUpdateEmployee(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EmployeeDTO employeeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Employee partially : {}, {}", id, employeeDTO);
        checkCrudPermission(EntityOp.UPDATE);
        if (employeeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, employeeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }
        if (!employeeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }
        Optional<EmployeeDTO> result = employeeService.partialUpdate(employeeDTO);
        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employeeDTO.getId().toString())
        );
    }

    @GetMapping("")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Employees");
        checkCrudPermission(EntityOp.READ);
        Page<EmployeeDTO> page = employeeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Employee : {}", id);
        checkCrudPermission(EntityOp.READ);
        Optional<EmployeeDTO> employeeDTO = employeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(employeeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Employee : {}", id);
        checkCrudPermission(EntityOp.DELETE);
        employeeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    private void checkCrudPermission(EntityOp op) {
        CrudEntityContext context = accessManager.applyRegisteredConstraints(new CrudEntityContext(Employee.class, op));
        if (!context.isPermitted()) {
            throw new AccessDeniedException("No " + op + " permission for Employee");
        }
    }
}
