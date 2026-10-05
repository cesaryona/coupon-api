# Coupon API

API REST para cadastro, consulta e exclusão (soft delete) de cupons de desconto.

O foco do projeto está nas regras de negócio de **Create** e **Delete**. Essas regras ficam encapsuladas em objetos de domínio e são cobertas por testes de unidade e de integração.

## Stack

- Java 21
- Spring Boot 4.1 (Web MVC, Data JPA, Validation)
- H2 (banco em memória)
- Flyway (migrations)
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito e MockMvc
- JaCoCo (cobertura mínima de 80%)
- Docker e Docker Compose

## Como rodar

### Local

Pré-requisito: JDK 21.

```bash
./gradlew bootRun
```

### Docker

```bash
docker compose up --build
```

Nos dois casos, a aplicação sobe em `http://localhost:8080`.

## Links úteis

| Recurso      | URL                                     |
|--------------|-----------------------------------------|
| Swagger UI   | http://localhost:8080/swagger-ui.html   |
| OpenAPI JSON | http://localhost:8080/v3/api-docs       |
| Console H2   | http://localhost:8080/h2-console        |

Dados de acesso ao console H2:

- JDBC URL: `jdbc:h2:mem:coupondb`
- Usuário: `sa`
- Senha: (vazia)

## Testes

```bash
./gradlew test     # roda todos os testes
./gradlew check    # roda os testes e valida a cobertura mínima de 80%
```

O relatório de cobertura fica em `build/reports/jacoco/test/html/index.html`.

| Tipo                | Onde                           | O que valida                                                         |
|---------------------|--------------------------------|----------------------------------------------------------------------|
| Domínio (JUnit puro)| `domain/model/*Test`           | Regras de negócio isoladas, sem Spring e sem mock                    |
| Use case (Mockito)  | `application/usecase/**/*Test` | Orquestração: o que é salvo e o que não é salvo                      |
| Integração          | `web/controller/CouponControllerIT` | Fluxo HTTP completo com H2 real, tentando quebrar cada regra    |

## Endpoints

### `POST /coupon`: criar cupom

Requisição:

```json
{
  "code": "ABC-123",
  "description": "Cupom de 10 reais",
  "discountValue": 10.5,
  "expirationDate": "2027-12-31T23:59:59",
  "published": true
}
```

Resposta `201 Created`, com o header `Location: /coupon/{id}`:

```json
{
  "id": "a54c4fd3-87e9-4672-8e99-be8120a90009",
  "code": "ABC123",
  "description": "Cupom de 10 reais",
  "discountValue": 10.5,
  "expirationDate": "2027-12-31T23:59:59",
  "status": "ACTIVE",
  "published": true,
  "redeemed": false
}
```

O campo `published` é opcional. Quando ausente, o valor é `false`.

### `GET /coupon/{id}`: consultar cupom

Resposta `200 OK` com o mesmo corpo do `POST`. Um cupom deletado também retorna `200`, com `"status": "DELETED"`.

### `DELETE /coupon/{id}`: deletar cupom (soft delete)

Resposta `204 No Content`.

### Erros

Todos os erros seguem o mesmo formato:

```json
{
  "status": 409,
  "message": "Coupon already deleted: a54c4fd3-87e9-4672-8e99-be8120a90009",
  "errors": []
}
```

| Situação                                         | Status |
|--------------------------------------------------|--------|
| Campo obrigatório ausente (lista em `errors`)    | 400    |
| Código sem 6 caracteres alfanuméricos após limpeza | 400  |
| Desconto abaixo de 0,5                           | 400    |
| Data de expiração no passado                     | 400    |
| JSON malformado ou UUID inválido na URL          | 400    |
| Cupom não encontrado                             | 404    |
| Cupom já deletado                                | 409    |

## Arquitetura

O projeto segue Clean Architecture, com ports and adapters na saída.

```
br.com.coupon.api
├── domain          regras de negócio (Coupon, CouponCode, DiscountValue, exceções)
├── application     casos de uso e portas (CreateCouponUseCase, DeleteCouponUseCase, FindCouponUseCase, CouponRepository)
├── infra           adaptadores: JPA, entidade, mapper e configuração dos beans
└── web             controller, DTOs e tratamento de erros
```

As dependências apontam sempre para dentro: `web` e `infra` dependem de `application`, e `application` depende de `domain`.

- **`domain`** é Java puro, sem nenhuma anotação de framework.
- **`application`** tem um caso de uso por intenção do usuário. Cada um tem um único método público, `execute`. Os use cases não importam Spring nem JPA, e o banco é acessado pela interface `CouponRepository` (porta de saída).
- **`infra`** implementa a porta com `CouponRepositoryAdapter` e registra os use cases como beans em `BeanConfig`. Por isso os use cases não têm `@Service`.
- **`web`** converte o HTTP em `CreateCouponInput` e mapeia as exceções de domínio para status HTTP.

Não existem portas de entrada (interfaces para os use cases). Cada use case tem uma única implementação, e o controller depende dele diretamente. Uma interface aqui não traria nenhum ganho.

## Regras de negócio

| Regra                                                                 | Onde está                                   |
|-----------------------------------------------------------------------|---------------------------------------------|
| `code`, `description`, `discountValue` e `expirationDate` são obrigatórios | `Coupon.create`, `CouponCode`, `DiscountValue` (e `@NotBlank`/`@NotNull` no request) |
| Código alfanumérico de 6 caracteres; caracteres especiais são removidos antes de salvar e de responder | `CouponCode.sanitize` |
| Desconto mínimo de 0,5, sem máximo                                    | `DiscountValue`                             |
| Data de expiração nunca no passado                                    | `Coupon.create`                             |
| Cupom pode ser criado já publicado                                    | `Coupon.create` (campo `published`)         |
| Cupom pode ser deletado a qualquer momento, inclusive depois de expirado | `Coupon.delete`                          |
| Soft delete: `status = DELETED` e `deletedAt` preenchido, sem perder os dados | `Coupon.delete`                     |
| Não é possível deletar um cupom já deletado                           | `Coupon.delete` (`CouponAlreadyDeletedException`) |

O request HTTP também valida o formato. Mesmo assim, o domínio valida tudo de novo: a regra continua valendo se o use case for chamado por outra entrada que não seja HTTP.
