# Implementacion del Patron de Acceso a Datos

## Objetivo
Describir la implementacion del patron de acceso a datos en el backend (Spring Boot), asegurando separacion de responsabilidades y consistencia con la arquitectura hexagonal.

## Enfoque arquitectonico
El proyecto utiliza arquitectura hexagonal:
- **Dominio**: entidades y reglas (sin dependencias de framework)
- **Aplicacion**: casos de uso
- **Infraestructura**: adaptadores de entrada/salida

## Patrón aplicado
Se implementa un patrón de acceso a datos basado en puertos y adaptadores:
- **Ports (interfaces)**: definen contratos de persistencia
- **Adapters**: implementan los contratos usando JPA

## Componentes clave

### Ports (contratos)
Ubicacion: `src/main/java/com/guardianapp/domain/port/out`

Ejemplos:
- `UserRepositoryPort`
- `LinkRepositoryPort`
- `EmergencyAlertRepositoryPort`
- `EmergencyAudioRepositoryPort`

### Adapters (implementacion JPA)
Ubicacion: `src/main/java/com/guardianapp/infrastructure/adapter/out/persistence/adapter`

Ejemplos:
- `UserRepositoryAdapter`
- `LinkRepositoryAdapter`
- `EmergencyAlertRepositoryAdapter`

### Repositorios JPA
Ubicacion: `src/main/java/com/guardianapp/infrastructure/adapter/out/persistence/repository`

Ejemplos:
- `UserJpaRepository`
- `LinkJpaRepository`
- `EmergencyAlertJpaRepository`

### Mapeadores
Ubicacion: `src/main/java/com/guardianapp/infrastructure/adapter/out/persistence/mapper`

Se usan mappers para transformar entre entidades JPA y modelos de dominio, asegurando independencia del framework.

## Beneficios
- Bajo acoplamiento entre dominio y persistencia
- Sustitucion sencilla de tecnología de almacenamiento
- Facil testeo de casos de uso (mocking de ports)

## Evidencia en codigo
- Interfaces en `domain/port/out`
- Implementaciones en `infrastructure/adapter/out/persistence/adapter`
- Uso desde servicios de aplicacion en `application/service`
