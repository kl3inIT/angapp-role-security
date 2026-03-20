Tôi sẽ thiết kế một Security + Data Access Engine cho JHipster theo kiểu Jmix với 4 trụ:
• quyền entity/object
• row policy
• fetch plan
• secure merge
Tinh thần giữ đúng như Jmix: mọi truy cập dữ liệu nghiệp vụ đi qua một lớp trung tâm kiểu DataManager, nơi security được áp trong data access flow; fetch plan dùng để kiểm soát graph và local attributes được nạp; còn quyền được tách thành resource roles và row-level roles.

---

1. Mục tiêu kiến trúc
   Ta muốn thay vì:
   Controller -> Repository -> Entity
   thì chuyển thành:
   Controller -> SecureDataManager -> AccessManager -> QueryRewriter/FetchPlanResolver -> Repository
   Và khi update:
   Controller -> SecureDataManager.save() -> Entity permission -> Row update policy -> Secure merge -> save
   Điểm mấu chốt học từ Jmix là: security được áp ở data access layer, không chỉ ở controller/UI; và các security constraints chỉ được framework áp khi đi qua DataManager.

---

2. Kiến trúc tổng thể
   2.1 Các module chính
   com.myapp.security.access
   com.myapp.security.policy
   com.myapp.security.fetchplan
   com.myapp.security.merge
   com.myapp.data.secure
   com.myapp.domain
   com.myapp.repository
   com.myapp.service
   com.myapp.web.rest
   2.2 Các thành phần lõi
   Access layer
   • AccessContext
   • AccessConstraint
   • AccessManager
   Permission layer
   • EntityPermissionEvaluator
   • AttributePermissionEvaluator
   • RolePermissionService
   Row-level layer
   • RowLevelPolicyProvider
   • RowLevelSpecificationBuilder
   Fetch layer
   • FetchPlan
   • FetchPlanRepository
   • FetchPlanResolver
   • JpaEntityGraphBuilder
   Merge layer
   • SecureMergeService
   • AttributeWriteGuard
   Data access layer
   • SecureDataManager
   • SecureDataManagerImpl

---

3. Security model trong DB
   Tôi khuyên làm metadata động trong DB.
   3.1 Bảng vai trò và quyền
   sec_role

- id
- code
- name
- type // RESOURCE, ROW_LEVEL

sec_permission

- id
- role_id
- target_type // ENTITY, ATTRIBUTE, ROW_POLICY, FETCH_PLAN
- target // ORDER, ORDER.unitPrice, ORDER#DELIVERY_SCOPE, ORDER#accounting
- action // READ, CREATE, UPDATE, DELETE, VIEW, EDIT, APPLY
- effect // ALLOW, DENY
  3.2 Bảng row policy
  sec_row_policy
- id
- code
- entity_name // Order
- operation // READ, UPDATE, DELETE
- policy_type // SPECIFICATION, JPQL, JAVA
- expression
  3.3 Bảng fetch plan
  sec_fetch_plan
- id
- code // order-accounting, order-delivery
- entity_name // Order
- definition_json

---

4.  Access contexts kiểu Jmix
    Jmix dùng access context + constraint chain để quyết định cho phép hay không. Ta làm bản tương đương.
    4.1 AccessContext
    public interface AccessContext {
    }
    CrudEntityContext
    public class CrudEntityContext implements AccessContext {
    private final Class<?> entityClass;
    private final EntityOp operation;
    private boolean permitted;

        public CrudEntityContext(Class<?> entityClass, EntityOp operation) {
            this.entityClass = entityClass;
            this.operation = operation;
            this.permitted = false;
        }

        public Class<?> getEntityClass() { return entityClass; }
        public EntityOp getOperation() { return operation; }
        public boolean isPermitted() { return permitted; }
        public void setPermitted(boolean permitted) { this.permitted = permitted; }

    }
    AttributeAccessContext
    public class AttributeAccessContext implements AccessContext {
    private final Class<?> entityClass;
    private final String attribute;
    private final AttributeOp operation;
    private boolean permitted;

        public AttributeAccessContext(Class<?> entityClass, String attribute, AttributeOp operation) {
            this.entityClass = entityClass;
            this.attribute = attribute;
            this.operation = operation;
            this.permitted = false;
        }

        // getters/setters

    }
    RowLevelAccessContext
    public class RowLevelAccessContext<T> implements AccessContext {
    private final Class<T> entityClass;
    private final EntityOp operation;
    private Specification<T> specification;

        public RowLevelAccessContext(Class<T> entityClass, EntityOp operation) {
            this.entityClass = entityClass;
            this.operation = operation;
            this.specification = Specification.where(null);
        }

        public void and(Specification<T> spec) {
            this.specification = this.specification.and(spec);
        }

        public Specification<T> getSpecification() {
            return specification;
        }

    }
    FetchPlanAccessContext
    public class FetchPlanAccessContext implements AccessContext {
    private final Class<?> entityClass;
    private final String planCode;
    private boolean permitted = true;

        public FetchPlanAccessContext(Class<?> entityClass, String planCode) {
            this.entityClass = entityClass;
            this.planCode = planCode;
        }

        // getters/setters

    }

---

5.  Constraint engine
    5.1 AccessConstraint
    public interface AccessConstraint<C extends AccessContext> {
    Class<C> supports();
    void applyTo(C context);
    int getOrder();
    }
    5.2 AccessManager
    public interface AccessManager {
    <C extends AccessContext> C applyRegisteredConstraints(C context);
    }
    5.3 AccessManagerImpl
    @Service
    public class AccessManagerImpl implements AccessManager {

        private final List<AccessConstraint<?>> constraints;

        public AccessManagerImpl(List<AccessConstraint<?>> constraints) {
            this.constraints = constraints;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <C extends AccessContext> C applyRegisteredConstraints(C context) {
            constraints.stream()
                .filter(c -> c.supports().isAssignableFrom(context.getClass()))
                .sorted(Comparator.comparingInt(AccessConstraint::getOrder))
                .forEach(c -> ((AccessConstraint<C>) c).applyTo(context));
            return context;
        }

    }

---

6.  Permission evaluators
    6.1 Entity permission
    public interface EntityPermissionEvaluator {
    boolean isPermitted(Class<?> entityClass, EntityOp op);
    }
    @Service
    public class EntityPermissionEvaluatorImpl implements EntityPermissionEvaluator {

        private final RolePermissionService rolePermissionService;
        private final SecurityService securityService;

        public EntityPermissionEvaluatorImpl(RolePermissionService rolePermissionService,
                                             SecurityService securityService) {
            this.rolePermissionService = rolePermissionService;
            this.securityService = securityService;
        }

        @Override
        public boolean isPermitted(Class<?> entityClass, EntityOp op) {
            String target = entityClass.getSimpleName().toUpperCase();
            return rolePermissionService.hasPermission(
                securityService.currentAuthorities(),
                TargetType.ENTITY,
                target,
                op.name()
            );
        }

    }
    6.2 Attribute permission
    public interface AttributePermissionEvaluator {
    boolean canView(Class<?> entityClass, String attribute);
    boolean canEdit(Class<?> entityClass, String attribute);
    }
    @Service
    public class AttributePermissionEvaluatorImpl implements AttributePermissionEvaluator {

        private final RolePermissionService rolePermissionService;
        private final SecurityService securityService;

        public AttributePermissionEvaluatorImpl(RolePermissionService rolePermissionService,
                                                SecurityService securityService) {
            this.rolePermissionService = rolePermissionService;
            this.securityService = securityService;
        }

        @Override
        public boolean canView(Class<?> entityClass, String attribute) {
            return check(entityClass, attribute, "VIEW");
        }

        @Override
        public boolean canEdit(Class<?> entityClass, String attribute) {
            return check(entityClass, attribute, "EDIT");
        }

        private boolean check(Class<?> entityClass, String attribute, String action) {
            String target = entityClass.getSimpleName().toUpperCase() + "." + attribute;
            return rolePermissionService.hasPermission(
                securityService.currentAuthorities(),
                TargetType.ATTRIBUTE,
                target,
                action
            );
        }

    }

---

7.  Access constraints cụ thể
    7.1 Constraint cho CRUD entity
    @Component
    public class CrudEntityConstraint implements AccessConstraint<CrudEntityContext> {

        private final EntityPermissionEvaluator evaluator;

        public CrudEntityConstraint(EntityPermissionEvaluator evaluator) {
            this.evaluator = evaluator;
        }

        @Override
        public Class<CrudEntityContext> supports() {
            return CrudEntityContext.class;
        }

        @Override
        public void applyTo(CrudEntityContext context) {
            context.setPermitted(
                evaluator.isPermitted(context.getEntityClass(), context.getOperation())
            );
        }

        @Override
        public int getOrder() {
            return 100;
        }

    }
    7.2 Constraint cho fetch plan
    @Component
    public class FetchPlanConstraint implements AccessConstraint<FetchPlanAccessContext> {

        private final RolePermissionService rolePermissionService;
        private final SecurityService securityService;

        public FetchPlanConstraint(RolePermissionService rolePermissionService,
                                   SecurityService securityService) {
            this.rolePermissionService = rolePermissionService;
            this.securityService = securityService;
        }

        @Override
        public Class<FetchPlanAccessContext> supports() {
            return FetchPlanAccessContext.class;
        }

        @Override
        public void applyTo(FetchPlanAccessContext context) {
            String target = context.getEntityClass().getSimpleName().toUpperCase()
                + "#" + context.getPlanCode();

            boolean allowed = rolePermissionService.hasPermission(
                securityService.currentAuthorities(),
                TargetType.FETCH_PLAN,
                target,
                "APPLY"
            );

            context.setPermitted(allowed);
        }

        @Override
        public int getOrder() {
            return 200;
        }

    }

---

8.  Row policy engine
    Jmix phân row-level roles riêng với resource roles; đó là đúng mô hình ta sẽ giữ.
    8.1 RowLevelPolicyProvider
    public interface RowLevelPolicyProvider {
    <T> List<RowPolicyDefinition<T>> getPolicies(Class<T> entityClass, EntityOp op);
    }
    8.2 RowPolicyDefinition
    public class RowPolicyDefinition<T> {
    private final String code;
    private final EntityOp operation;
    private final Specification<T> specification;

        public RowPolicyDefinition(String code, EntityOp operation, Specification<T> specification) {
            this.code = code;
            this.operation = operation;
            this.specification = specification;
        }

        public Specification<T> getSpecification() {
            return specification;
        }

    }
    8.3 RowLevelSpecificationBuilder
    @Service
    public class RowLevelSpecificationBuilder {

        private final RowLevelPolicyProvider policyProvider;

        public RowLevelSpecificationBuilder(RowLevelPolicyProvider policyProvider) {
            this.policyProvider = policyProvider;
        }

        public <T> Specification<T> build(Class<T> entityClass, EntityOp op) {
            List<RowPolicyDefinition<T>> policies = policyProvider.getPolicies(entityClass, op);

            Specification<T> result = Specification.where(null);
            for (RowPolicyDefinition<T> policy : policies) {
                result = result.and(policy.getSpecification());
            }
            return result;
        }

    }
    8.4 Ví dụ policy cho Order
    @Component
    public class OrderRowPolicyProvider implements RowLevelPolicyProvider {

        private final SecurityService securityService;

        public OrderRowPolicyProvider(SecurityService securityService) {
            this.securityService = securityService;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> List<RowPolicyDefinition<T>> getPolicies(Class<T> entityClass, EntityOp op) {
            if (!Order.class.equals(entityClass)) {
                return List.of();
            }

            Long userId = securityService.currentUserId();
            List<RowPolicyDefinition<Order>> result = new ArrayList<>();

            if (securityService.hasAuthority("ORDER_READ_ALL")) {
                return (List<RowPolicyDefinition<T>>) (List<?>) result;
            }

            if (securityService.hasAuthority("ORDER_READ_ASSIGNED") && op == EntityOp.READ) {
                result.add(new RowPolicyDefinition<>(
                    "order-assigned",
                    EntityOp.READ,
                    (root, query, cb) -> cb.equal(root.get("deliveryUser").get("id"), userId)
                ));
            }

            if (securityService.hasAuthority("ORDER_READ_WAREHOUSE") && op == EntityOp.READ) {
                Long warehouseId = securityService.currentWarehouseId();
                result.add(new RowPolicyDefinition<>(
                    "order-warehouse",
                    EntityOp.READ,
                    (root, query, cb) -> cb.equal(root.get("warehouse").get("id"), warehouseId)
                ));
            }

            return (List<RowPolicyDefinition<T>>) (List<?>) result;
        }

    }

---

9.  Fetch plan engine
    Jmix dùng fetch plans để kiểm soát graph và local attributes được tải, qua named fetch plan hoặc cấu hình code.
    9.1 FetchPlan
    public class FetchPlan {
    private String code;
    private Class<?> entityClass;
    private Set<String> scalarAttributes = new LinkedHashSet<>();
    private Map<String, FetchPlan> references = new LinkedHashMap<>();

        // getters/setters

    }
    9.2 FetchPlanResolver
    public interface FetchPlanResolver {
    <T> FetchPlan resolve(Class<T> entityClass, String planCode);
    }
    9.3 FetchPlanResolverImpl
    @Service
    public class FetchPlanResolverImpl implements FetchPlanResolver {

        private final FetchPlanRepository fetchPlanRepository;
        private final AccessManager accessManager;

        public FetchPlanResolverImpl(FetchPlanRepository fetchPlanRepository,
                                     AccessManager accessManager) {
            this.fetchPlanRepository = fetchPlanRepository;
            this.accessManager = accessManager;
        }

        @Override
        public <T> FetchPlan resolve(Class<T> entityClass, String planCode) {
            FetchPlanAccessContext context =
                accessManager.applyRegisteredConstraints(new FetchPlanAccessContext(entityClass, planCode));

            if (!context.isPermitted()) {
                throw new AccessDeniedException("Fetch plan not allowed: " + planCode);
            }

            return fetchPlanRepository.findByEntityAndCode(entityClass, planCode)
                .orElseThrow(() -> new IllegalArgumentException("Fetch plan not found: " + planCode));
        }

    }
    9.4 Ví dụ fetch plan cho Order
    order-basic
    {
    "code": "order-basic",
    "entity": "Order",
    "scalarAttributes": ["id", "orderCode", "status", "createdDate"],
    "references": {
    "customer": {
    "scalarAttributes": ["id", "name"]
    }
    }
    }
    order-accounting
    {
    "code": "order-accounting",
    "entity": "Order",
    "scalarAttributes": ["id", "orderCode", "status", "createdDate", "unitPrice", "stockQty", "totalAmount"],
    "references": {
    "customer": {
    "scalarAttributes": ["id", "name", "taxCode"]
    }
    }
    }
    order-delivery
    {
    "code": "order-delivery",
    "entity": "Order",
    "scalarAttributes": ["id", "orderCode", "status", "deliveryAddress", "deliveryPhone"],
    "references": {
    "customer": {
    "scalarAttributes": ["id", "name", "phone"]
    }
    }
    }

---

10. Ánh xạ fetch plan sang JPA
    JPA không có fetch plan giống Jmix đầy đủ, nhưng có thể mô phỏng bằng 2 bước:
    • dùng EntityGraph / fetch join để lấy reference cần thiết
    • dùng attribute filtering sau khi load để chỉ trả đúng scalar fields
    10.1 JpaEntityGraphBuilder
    public interface JpaEntityGraphBuilder {
    <T> EntityGraph<T> build(EntityManager em, Class<T> entityClass, FetchPlan plan);
    }
    @Service
    public class JpaEntityGraphBuilderImpl implements JpaEntityGraphBuilder {

        @Override
        public <T> EntityGraph<T> build(EntityManager em, Class<T> entityClass, FetchPlan plan) {
            EntityGraph<T> graph = em.createEntityGraph(entityClass);

            for (String ref : plan.getReferences().keySet()) {
                Subgraph<Object> subgraph = graph.addSubgraph(ref);
                FetchPlan nested = plan.getReferences().get(ref);
                for (String nestedAttr : nested.getScalarAttributes()) {
                    subgraph.addAttributeNodes(nestedAttr);
                }
            }

            return graph;
        }

    }
    Lưu ý: JPA EntityGraph rất tốt cho association graph, nhưng không đủ để “cắt local scalar field” như Jmix. Vì vậy ở JHipster bản tương đương phải thêm bước serialize/filter phía sau. Đây là khác biệt mà ta chấp nhận. Còn ý tưởng tổng thể vẫn bám đúng hướng Jmix fetch plan.

---

11. Secure read model
    Ta sẽ không tạo 5 DTO khác nhau cho từng vai trò nữa. Thay vào đó:
    • 1 entity/domain model
    • 1 FetchPlan
    • 1 SecureEntityView
    • 1 serializer/filter theo attribute permission
    11.1 SecureEntityView
    public class SecureEntityView {
    private final Class<?> entityClass;
    private final Map<String, Object> values = new LinkedHashMap<>();

        public SecureEntityView(Class<?> entityClass) {
            this.entityClass = entityClass;
        }

        public void put(String name, Object value) {
            values.put(name, value);
        }

        public Map<String, Object> getValues() {
            return values;
        }

        public Class<?> getEntityClass() {
            return entityClass;
        }

    }
    11.2 SecureEntitySerializer
    public interface SecureEntitySerializer {
    SecureEntityView serialize(Object entity, FetchPlan fetchPlan);
    }
    @Service
    public class SecureEntitySerializerImpl implements SecureEntitySerializer {

        private final AttributePermissionEvaluator attributePermissionEvaluator;

        public SecureEntitySerializerImpl(AttributePermissionEvaluator attributePermissionEvaluator) {
            this.attributePermissionEvaluator = attributePermissionEvaluator;
        }

        @Override
        public SecureEntityView serialize(Object entity, FetchPlan fetchPlan) {
            Class<?> entityClass = entity.getClass();
            SecureEntityView view = new SecureEntityView(entityClass);

            BeanWrapper wrapper = new BeanWrapperImpl(entity);

            for (String attr : fetchPlan.getScalarAttributes()) {
                if (attributePermissionEvaluator.canView(entityClass, attr)) {
                    view.put(attr, wrapper.getPropertyValue(attr));
                }
            }

            for (Map.Entry<String, FetchPlan> ref : fetchPlan.getReferences().entrySet()) {
                String attr = ref.getKey();

                if (!attributePermissionEvaluator.canView(entityClass, attr)) {
                    continue;
                }

                Object refValue = wrapper.getPropertyValue(attr);
                if (refValue == null) {
                    view.put(attr, null);
                } else {
                    view.put(attr, serialize(refValue, ref.getValue()).getValues());
                }
            }

            return view;
        }

    }

---

12. Secure merge khi update
    Đây là phần thay cho DTO update theo role.
    12.1 SecureMergeService
    public interface SecureMergeService {
    <T> T mergeForUpdate(T entity, Map<String, Object> payload);
    }
    12.2 SecureMergeServiceImpl
    @Service
    public class SecureMergeServiceImpl implements SecureMergeService {

        private final AttributePermissionEvaluator attributePermissionEvaluator;

        public SecureMergeServiceImpl(AttributePermissionEvaluator attributePermissionEvaluator) {
            this.attributePermissionEvaluator = attributePermissionEvaluator;
        }

        @Override
        public <T> T mergeForUpdate(T entity, Map<String, Object> payload) {
            Class<?> entityClass = entity.getClass();
            BeanWrapper wrapper = new BeanWrapperImpl(entity);

            for (Map.Entry<String, Object> entry : payload.entrySet()) {
                String attr = entry.getKey();

                if (!attributePermissionEvaluator.canEdit(entityClass, attr)) {
                    throw new AccessDeniedException("No EDIT permission for " + entityClass.getSimpleName() + "." + attr);
                }

                wrapper.setPropertyValue(attr, entry.getValue());
            }

            return entity;
        }

    }

---

13. SecureDataManager
    Đây là trung tâm của toàn bộ giải pháp.
    13.1 Interface
    public interface SecureDataManager {

        <T> Map<String, Object> loadOne(Class<T> entityClass, Object id, String fetchPlanCode);

        <T> List<Map<String, Object>> loadList(Class<T> entityClass,
                                               Specification<T> userSpec,
                                               String fetchPlanCode);

        <T> Map<String, Object> save(Class<T> entityClass,
                                     Object id,
                                     Map<String, Object> payload,
                                     String fetchPlanCode);

        <T> void delete(Class<T> entityClass, Object id);

    }
    13.2 Implementation
    @Service
    @Transactional
    public class SecureDataManagerImpl implements SecureDataManager {

        private final AccessManager accessManager;
        private final RowLevelSpecificationBuilder rowSpecBuilder;
        private final FetchPlanResolver fetchPlanResolver;
        private final SecureEntitySerializer serializer;
        private final SecureMergeService mergeService;
        private final RepositoryRegistry repositoryRegistry;

        public SecureDataManagerImpl(AccessManager accessManager,
                                     RowLevelSpecificationBuilder rowSpecBuilder,
                                     FetchPlanResolver fetchPlanResolver,
                                     SecureEntitySerializer serializer,
                                     SecureMergeService mergeService,
                                     RepositoryRegistry repositoryRegistry) {
            this.accessManager = accessManager;
            this.rowSpecBuilder = rowSpecBuilder;
            this.fetchPlanResolver = fetchPlanResolver;
            this.serializer = serializer;
            this.mergeService = mergeService;
            this.repositoryRegistry = repositoryRegistry;
        }

        @Override
        @Transactional(readOnly = true)
        public <T> Map<String, Object> loadOne(Class<T> entityClass, Object id, String fetchPlanCode) {
            checkCrud(entityClass, EntityOp.READ);

            Specification<T> rowSpec = rowSpecBuilder.build(entityClass, EntityOp.READ);
            JpaSpecificationExecutor<T> repo = repositoryRegistry.specRepository(entityClass);

            T entity = repo.findOne(byId(id).and(rowSpec))
                .orElseThrow(() -> new EntityNotFoundException(entityClass.getSimpleName() + " not found"));

            FetchPlan fetchPlan = fetchPlanResolver.resolve(entityClass, fetchPlanCode);
            return serializer.serialize(entity, fetchPlan).getValues();
        }

        @Override
        @Transactional(readOnly = true)
        public <T> List<Map<String, Object>> loadList(Class<T> entityClass,
                                                      Specification<T> userSpec,
                                                      String fetchPlanCode) {
            checkCrud(entityClass, EntityOp.READ);

            Specification<T> rowSpec = rowSpecBuilder.build(entityClass, EntityOp.READ);
            JpaSpecificationExecutor<T> repo = repositoryRegistry.specRepository(entityClass);
            FetchPlan fetchPlan = fetchPlanResolver.resolve(entityClass, fetchPlanCode);

            return repo.findAll(Specification.where(userSpec).and(rowSpec)).stream()
                .map(entity -> serializer.serialize(entity, fetchPlan).getValues())
                .toList();
        }

        @Override
        public <T> Map<String, Object> save(Class<T> entityClass,
                                            Object id,
                                            Map<String, Object> payload,
                                            String fetchPlanCode) {
            checkCrud(entityClass, EntityOp.UPDATE);

            JpaRepository<T, Object> repo = repositoryRegistry.repository(entityClass);
            JpaSpecificationExecutor<T> specRepo = repositoryRegistry.specRepository(entityClass);

            Specification<T> rowSpec = rowSpecBuilder.build(entityClass, EntityOp.UPDATE);

            T entity = specRepo.findOne(byId(id).and(rowSpec))
                .orElseThrow(() -> new AccessDeniedException("Entity not found or row-level denied"));

            mergeService.mergeForUpdate(entity, payload);
            T saved = repo.save(entity);

            FetchPlan fetchPlan = fetchPlanResolver.resolve(entityClass, fetchPlanCode);
            return serializer.serialize(saved, fetchPlan).getValues();
        }

        @Override
        public <T> void delete(Class<T> entityClass, Object id) {
            checkCrud(entityClass, EntityOp.DELETE);

            JpaRepository<T, Object> repo = repositoryRegistry.repository(entityClass);
            JpaSpecificationExecutor<T> specRepo = repositoryRegistry.specRepository(entityClass);

            Specification<T> rowSpec = rowSpecBuilder.build(entityClass, EntityOp.DELETE);

            T entity = specRepo.findOne(byId(id).and(rowSpec))
                .orElseThrow(() -> new AccessDeniedException("Entity not found or row-level denied"));

            repo.delete(entity);
        }

        private <T> void checkCrud(Class<T> entityClass, EntityOp op) {
            CrudEntityContext context =
                accessManager.applyRegisteredConstraints(new CrudEntityContext(entityClass, op));

            if (!context.isPermitted()) {
                throw new AccessDeniedException("No " + op + " permission for " + entityClass.getSimpleName());
            }
        }

        private <T> Specification<T> byId(Object id) {
            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

    }

---

14. Áp dụng vào Order
    14.1 Quyền mẫu
    Kế toán
    • ENTITY ORDER READ
    • ATTRIBUTE ORDER.unitPrice VIEW
    • ATTRIBUTE ORDER.stockQty VIEW
    • FETCH_PLAN ORDER#order-accounting APPLY
    Thủ kho
    • ENTITY ORDER READ
    • ATTRIBUTE ORDER.stockQty VIEW
    • ATTRIBUTE ORDER.stockQty EDIT
    • FETCH_PLAN ORDER#order-warehouse APPLY
    Nhân viên giao hàng
    • ENTITY ORDER READ
    • ENTITY ORDER UPDATE
    • ATTRIBUTE ORDER.deliveryAddress VIEW
    • ATTRIBUTE ORDER.deliveryPhone VIEW
    • ATTRIBUTE ORDER.deliveryStatus EDIT
    • ROW_POLICY ORDER#assigned READ
    • ROW_POLICY ORDER#assigned UPDATE
    • FETCH_PLAN ORDER#order-delivery APPLY

---

15. REST layer
    Controller bây giờ rất mỏng.
    @RestController
    @RequestMapping("/api/orders")
    public class OrderResource {

        private final SecureDataManager secureDataManager;

        public OrderResource(SecureDataManager secureDataManager) {
            this.secureDataManager = secureDataManager;
        }

        @GetMapping("/{id}")
        public ResponseEntity<Map<String, Object>> getOrder(@PathVariable Long id,
                                                            @RequestParam(defaultValue = "order-basic") String plan) {
            return ResponseEntity.ok(secureDataManager.loadOne(Order.class, id, plan));
        }

        @GetMapping
        public ResponseEntity<List<Map<String, Object>>> listOrders(
                @RequestParam(defaultValue = "order-basic") String plan) {
            return ResponseEntity.ok(secureDataManager.loadList(Order.class, null, plan));
        }

        @PatchMapping("/{id}")
        public ResponseEntity<Map<String, Object>> updateOrder(@PathVariable Long id,
                                                               @RequestBody Map<String, Object> payload,
                                                               @RequestParam(defaultValue = "order-basic") String plan) {
            return ResponseEntity.ok(secureDataManager.save(Order.class, id, payload, plan));
        }

    }

---

16. Ưu điểm của thiết kế này
    Đúng tinh thần Jmix
    • data access tập trung qua SecureDataManager
    • security không rải ở controller
    • row policy áp trước khi lấy dữ liệu
    • fetch plan đi cùng data loading
    • attribute permission áp khi đọc và khi ghi.
    Giảm số lượng DTO
    Thay vì:
    • OrderForAccountingDto
    • OrderForWarehouseDto
    • OrderForDeliveryDto
    ta chỉ cần:
    • FetchPlan
    • SecureEntitySerializer
    • SecureMergeService
    Mở rộng tốt
    Sau này thêm Invoice, Product, Shipment chỉ cần:
    • thêm metadata quyền
    • thêm row policy
    • thêm fetch plan
    • không phải nhân bản controller/service pattern

---

17. Điểm khác với Jmix thật
    Tôi nói rõ để tránh hiểu nhầm.
    Giống rất sát
    • triết lý DataManager
    • access context + constraints
    • resource role / row-level role
    • fetch plan theo use case
    • secure read + secure merge
    Không giống 1:1
    • JPA EntityGraph không mạnh bằng fetch plan nội bộ của Jmix cho local scalar attributes
    • JHipster/Spring Data không có sẵn metadata/fetch/security pipeline tích hợp sâu như Jmix framework
    • vì vậy cần thêm SecureEntitySerializer để hoàn tất phần cắt field
    Nói cách khác: hành vi nghiệp vụ rất sát Jmix, nhưng implementation runtime không thể 1:1 tuyệt đối.

---

18. Lộ trình triển khai thực tế
    Tôi khuyên triển khai theo 4 phase.
    Phase 1
    Làm lõi:
    • AccessManager
    • CrudEntityConstraint
    • AttributePermissionEvaluator
    • SecureMergeService
    Phase 2
    Làm row-level:
    • RowLevelPolicyProvider
    • RowLevelSpecificationBuilder
    • áp vào loadList/loadOne/save/delete
    Phase 3
    Làm fetch plan:
    • metadata JSON
    • FetchPlanResolver
    • SecureEntitySerializer
    Phase 4
    Chuẩn hóa framework:
    • RepositoryRegistry
    • metadata cache
    • audit log cho denied fields
    • admin UI quản trị quyền và fetch plan

---

19. Kết luận
    Đây là bản thiết kế tôi khuyến nghị cho JHipster kiểu Jmix:
    • entity permission quyết định có được thao tác trên entity không
    • row policy quyết định được thấy/sửa record nào
    • fetch plan quyết định cần load field/reference nào
    • secure merge quyết định field nào được phép update
    Công thức hoàn chỉnh là:
    SecureDataManager = CRUD permission + Row policy + Fetch plan + Attribute read/write security
