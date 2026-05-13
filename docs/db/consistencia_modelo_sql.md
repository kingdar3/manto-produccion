# Consistencia entre modelo y script SQL

## Fuente de verdad
- Modelo: entidades JPA en `src/main/java/com/guardianapp/infrastructure/adapter/out/persistence/entity`
- Script SQL: `src/main/resources/db/schema.sql`

## Metodologia de verificacion
1) Exportar DDL desde Postgres (DBeaver/pg_dump).
2) Comparar nombres de tablas y columnas.
3) Validar tipos de datos principales.
4) Confirmar claves primarias/foraneas y checks.
5) Verificar indices relevantes para consultas del sistema.

## Resultados
Se confirma consistencia en:
- Nombres de tablas y columnas
- Tipos (uuid, timestamp, varchar, bool)
- Constraints de integridad y checks de estados
- Indices para rendimiento (busquedas por usuario y estado)

## Observaciones
- El proyecto usa `spring.jpa.hibernate.ddl-auto=update` en desarrollo.
- Para la entrega academica, el DDL versionado es el archivo `schema.sql`.

## Evidencias
- ERD: `docs/erd/guardian_db_erd.png`
- DDL exportado desde Postgres
