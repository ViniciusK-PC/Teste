# 🎟️ Coupon Management API

Sistema de gerenciamento de cupons de desconto desenvolvido com **Spring Boot 3**, seguindo princípios de **Domain-Driven Design (DDD)**, **Clean Code** e boas práticas de arquitetura.

---

## 📋 Índice

- [Tecnologias](#-tecnologias)
- [Arquitetura](#-arquitetura)
- [Como Executar](#-como-executar)
- [Endpoints da API](#-endpoints-da-api)
- [Documentação Swagger](#-documentação-swagger)
- [Testes](#-testes)
- [Decisões Técnicas](#-decisões-técnicas)

---

## 🛠 Tecnologias

| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.4 | Framework backend |
| Spring Data JPA | - | Persistência |
| H2 Database | - | Banco em memória |
| Lombok | - | Redução de boilerplate |
| SpringDoc OpenAPI | 2.5.0 | Documentação Swagger |
| JUnit 5 | - | Testes unitários |
| Mockito | - | Mocks para testes |
| Docker | - | Containerização |

---

## 🏗 Arquitetura

```
src/main/java/com/example/coupon/
├── config/               # Configurações (OpenAPI)
├── controller/           # Camada REST (sem lógica)
├── domain/
│   ├── model/            # Entidades com regras de negócio
│   └── repository/       # Interfaces JPA
├── dto/
│   ├── request/          # DTOs de entrada (com Bean Validation)
│   └── response/         # DTOs de saída
├── exception/            # BusinessException + GlobalExceptionHandler
└── service/              # Orquestração (sem regras de negócio)
```

**Princípio aplicado:** A camada **Domain** concentra todas as regras de negócio (validação, sanitização, soft delete). O **Service** apenas orquestra chamadas entre Repository e Domain. O **Controller** apenas recebe/devolve dados.

---

## 🚀 Como Executar

### Opção 1: Execução Local (Sem Docker)

**Pré-requisito:** Apenas Java 17 instalado. **Maven NÃO é necessário** (o projeto inclui Maven Wrapper).

**Windows — Duplo clique:**
```
run.bat
```

**Ou via terminal:**
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

### Opção 2: Via Docker
```bash
# 1. Gerar o .jar (requer Maven ou usar o wrapper)
.\mvnw.cmd clean package -DskipTests

# 2. Subir o container
docker-compose up --build
```

A API estará disponível em: `http://localhost:8080`


---

## 📡 Endpoints da API

Todas as respostas seguem o formato padronizado:
```json
{
  "status": 200,
  "message": "Descrição da operação",
  "timestamp": "2026-04-16T12:00:00",
  "data": { }
}
```

| Método | Endpoint | Descrição | Status |
|---|---|---|---|
| `POST` | `/api/coupons` | Criar novo cupom | `201` |
| `GET` | `/api/coupons` | Listar todos os cupons ativos | `200` |
| `GET` | `/api/coupons/{id}` | Buscar cupom por ID | `200` |
| `PUT` | `/api/coupons/{id}` | Atualizar cupom | `200` |
| `DELETE` | `/api/coupons/{id}` | Excluir cupom (soft delete) | `200` |

### Exemplos de Requisição

**Criar Cupom:**
```bash
curl -X POST http://localhost:8080/api/coupons \
  -H "Content-Type: application/json" \
  -d '{"code": "SAVE20", "discountValue": 20.00, "expirationDate": "2026-12-31"}'
```

**Atualizar Cupom:**
```bash
curl -X PUT http://localhost:8080/api/coupons/1 \
  -H "Content-Type: application/json" \
  -d '{"discountValue": 30.00, "expirationDate": "2027-06-30"}'
```

---

## 📖 Documentação Swagger

Acesse a documentação interativa em:  
👉 [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🧪 Testes

```bash
mvn test
```

### Cobertura por Camada

| Camada | Classe de Teste | Cenários |
|---|---|---|
| **Domain** | `CouponTest` | 16 testes (create, sanitização, validações, delete, update) |
| **Service** | `CouponServiceTest` | 3 testes (orquestração com Mockito) |
| **Integration** | `CouponControllerIT` | 6 testes (fluxo E2E com MockMvc + H2) |

**Total: 25 cenários de teste** para cobertura > 80%.

---

## 💡 Decisões Técnicas

### 1. Domínio Rico (vs. Serviço Anêmico)
Toda lógica de negócio está **dentro da entidade `Coupon`**:
- `Coupon.create(...)` — valida e sanitiza antes de instanciar
- `coupon.update(...)` — valida antes de alterar estado
- `coupon.delete()` — garante idempotência do soft delete

O **Service** não contém nenhuma regra — apenas coordena Repository ↔ Domain ↔ DTO.

### 2. Encapsulamento
- Sem `@Setter` na entidade — o estado só muda via métodos do domínio
- Construtor **privado** — obriga o uso do factory method `create()`
- `NoArgsConstructor(PROTECTED)` — necessário apenas para o JPA

### 3. Soft Delete
- Campo `boolean deleted` com valor padrão `false`
- Anotação `@Where(clause = "deleted = false")` para filtro automático no JPA
- Nunca é chamado `repository.delete()` — somente `coupon.delete()` + `save()`

### 4. Sanitização do Código
- Remove caracteres especiais via Regex: `[^a-zA-Z0-9]`
- Garante exatamente 6 caracteres alfanuméricos
- Converte para caixa alta para consistência

### 5. Respostas Padronizadas
- Classe `ApiResponseDTO<T>` unifica toda comunicação da API
- Inclui `status`, `message`, `timestamp`, `data` e `errors`
- Campos nulos são omitidos (`@JsonInclude.NON_NULL`)

### 6. Tratamento Global de Exceções
- `BusinessException` — erros de regra de negócio (400)
- `MethodArgumentNotValidException` — erros de validação Bean (400)
- `Exception` genérica — fallback sem expor stack trace (500)

---

Desenvolvido como parte do desafio técnico — nível Pleno/Sênior.
