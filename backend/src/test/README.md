# Test Suite Documentation

## Overview
This test suite provides comprehensive testing for the Animal Disease Reporting System backend, covering authentication, user management, and security features.

## Test Structure

```
backend/src/test/
├── java/com/adrs/test/
│   ├── config/
│   │   └── JwtTokenProviderTest.java       # JWT token generation and validation tests
│   ├── controller/
│   │   ├── AuthControllerTest.java         # Authentication API endpoint tests
│   │   └── UserControllerTest.java         # User management API endpoint tests
│   ├── repository/
│   │   └── UserRepositoryTest.java         # Database operations tests
│   ├── service/
│   │   └── UserServiceTest.java            # Business logic tests
│   └── integration/
│       └── AuthenticationIntegrationTest.java  # End-to-end integration tests
└── resources/
    └── application-test.properties         # Test configuration
```

## Test Categories

### 1. Repository Tests (`UserRepositoryTest`)
**Purpose**: Test database CRUD operations and custom queries

**Coverage**:
- User creation and persistence
- Finding users by username and email
- Checking username/email existence
- Updating and deleting users
- Unique constraint validation

**Technology**: 
- `@DataJpaTest` for JPA repository testing
- H2 in-memory database
- `TestEntityManager` for entity persistence

### 2. Service Tests (`UserServiceTest`)
**Purpose**: Test business logic with mocked dependencies

**Coverage**:
- User authentication flow
- User creation with validation
- User updates and retrieval
- Duplicate username/email prevention
- Exception handling

**Technology**:
- `@ExtendWith(MockitoExtension.class)` for mocking
- Mockito for dependency mocking
- AssertJ for fluent assertions

### 3. Controller Tests

#### AuthControllerTest
**Purpose**: Test authentication REST API endpoints

**Coverage**:
- Successful login with valid credentials
- Login validation (missing/blank fields)
- Logout functionality
- Malformed request handling

**Technology**:
- `@WebMvcTest` for controller testing
- MockMvc for HTTP request simulation
- `@WithMockUser` for security context

#### UserControllerTest
**Purpose**: Test user management REST API endpoints

**Coverage**:
- CRUD operations (Create, Read, Update, Delete)
- Role-based access control (RBAC)
- Admin-only operations
- Authentication requirements
- Input validation

**Technology**:
- `@WebMvcTest` for controller testing
- MockMvc for HTTP request simulation
- `@WithMockUser` for different roles

### 4. Config Tests (`JwtTokenProviderTest`)
**Purpose**: Test JWT token generation and validation

**Coverage**:
- Token generation
- Username extraction from token
- Token validation
- Expired token handling
- Malformed token rejection
- Invalid signature detection

**Technology**:
- ReflectionTestUtils for setting private fields
- JWT testing utilities

### 5. Integration Tests (`AuthenticationIntegrationTest`)
**Purpose**: Test end-to-end functionality with real components

**Coverage**:
- Complete authentication flow
- Full user CRUD lifecycle
- Authentication enforcement
- Duplicate validation
- Database persistence verification

**Technology**:
- `@SpringBootTest` for full context
- `@AutoConfigureMockMvc` for HTTP testing
- `@Transactional` for test isolation
- Real database interactions

## Running Tests

### Run All Tests
```bash
cd backend
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=UserRepositoryTest
```

### Run Specific Test Method
```bash
mvn test -Dtest=UserRepositoryTest#testSaveUser
```

### Run Tests with Coverage
```bash
mvn clean test jacoco:report
```
View coverage report: `backend/target/site/jacoco/index.html`

### Run Integration Tests Only
```bash
mvn test -Dtest=*IntegrationTest
```

### Run Unit Tests Only
```bash
mvn test -Dtest=!*IntegrationTest
```

## Test Configuration

### Test Database
- **Type**: H2 in-memory database (PostgreSQL mode)
- **Connection**: `jdbc:h2:mem:testdb`
- **Auto-configured**: Creates/drops schema for each test
- **Location**: `src/test/resources/application-test.properties`

### Test Profile
Tests run with `@ActiveProfiles("test")` which loads:
- `application-test.properties`
- H2 database instead of PostgreSQL
- Test-specific configurations

## Test Best Practices

### 1. Test Isolation
- Each test is independent and doesn't rely on others
- `@Transactional` ensures database rollback after each test
- `@BeforeEach` sets up fresh test data

### 2. Naming Convention
- Test classes: `<ClassUnderTest>Test`
- Integration tests: `<Feature>IntegrationTest`
- Test methods: `test<MethodName>` or descriptive names
- Use `@DisplayName` for readable test descriptions

### 3. AAA Pattern
All tests follow the Arrange-Act-Assert pattern:
```java
@Test
void testExample() {
    // Arrange (Given)
    User user = createTestUser();
    
    // Act (When)
    User result = userService.save(user);
    
    // Assert (Then)
    assertThat(result.getId()).isNotNull();
}
```

### 4. Assertions
- Use AssertJ for fluent assertions
- Provide meaningful error messages
- Test both positive and negative scenarios

### 5. Mocking
- Mock external dependencies in unit tests
- Use real components in integration tests
- Verify mock interactions with `verify()`

## Code Coverage Goals

Target coverage levels:
- **Overall**: ≥ 80%
- **Service Layer**: ≥ 90%
- **Controller Layer**: ≥ 85%
- **Repository Layer**: ≥ 80%

## Common Test Utilities

### Creating Test Users
```java
User testUser = new User();
testUser.setUsername("testuser");
testUser.setEmail("test@example.com");
testUser.setPassword(passwordEncoder.encode("password123"));
testUser.setFirstName("Test");
testUser.setLastName("User");
testUser.setRole(User.Role.ADMIN);
testUser.setActive(true);
```

### Creating JWT Token for Tests
```java
LoginRequest loginRequest = new LoginRequest();
loginRequest.setUsername("testuser");
loginRequest.setPassword("password123");

MvcResult result = mockMvc.perform(post("/api/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
    .andReturn();

AuthResponse authResponse = objectMapper.readValue(
    result.getResponse().getContentAsString(),
    AuthResponse.class
);
String token = authResponse.getToken();
```

## Troubleshooting

### Test Fails with "Connection Refused"
- Ensure H2 database is properly configured
- Check `application-test.properties` is being loaded

### Test Fails with "Bean Not Found"
- Ensure test is using correct Spring Boot test annotations
- Check component scanning configuration

### Test Fails with "Authentication Failed"
- Use `@WithMockUser` for secured endpoints
- Provide valid JWT token in integration tests

### Test Fails Intermittently
- Check for test isolation issues
- Ensure tests don't depend on execution order
- Verify `@Transactional` is present for DB tests

## Continuous Integration

Tests are automatically run in CI/CD pipeline:
1. On every commit
2. Before merging pull requests
3. Before deployment

### CI Configuration
```yaml
# Example GitHub Actions configuration
- name: Run Tests
  run: mvn clean test
  
- name: Generate Coverage Report
  run: mvn jacoco:report
  
- name: Upload Coverage
  uses: codecov/codecov-action@v3
```

## Future Test Improvements

1. **Performance Tests**: Add tests for response time and throughput
2. **Security Tests**: Add penetration testing for common vulnerabilities
3. **Load Tests**: Test system behavior under high load
4. **Contract Tests**: Add API contract testing
5. **E2E Tests**: Add Selenium/Playwright tests for frontend

## Additional Resources

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Spring Security Test](https://docs.spring.io/spring-security/reference/servlet/test/index.html)
