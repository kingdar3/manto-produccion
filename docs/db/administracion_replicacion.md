# Informe de Administracion y Replicacion

## Contexto y alcance
El sistema utiliza PostgreSQL 16 como base de datos principal en un contenedor Docker. Este informe documenta la administracion local, respaldos, monitoreo y lineamientos de replicacion. El objetivo es cumplir los requisitos academicos y asegurar continuidad operativa.

**Repositorio y contenedor**
- Backend: `guardian-api`
- Compose: `docker-compose.yml`
- Contenedor: `guardian-postgres`

## Administracion de la base de datos

### Parametros de conexion (entorno local)
- Host: `localhost`
- Puerto: `5432`
- Base de datos: `guardian_db`
- Usuario: `guardian_user`
- Password: `guardian_pass`

### Inicializacion y arranque
1) Iniciar contenedor
```
docker compose up -d
```
2) Verificar disponibilidad
```
pg_isready -U guardian_user -d guardian_db
```
3) Verificar datos
```
psql -h localhost -p 5432 -U guardian_user -d guardian_db -c "\\dt"
```

### Backups y restauracion
Se recomienda mantener respaldos del esquema y de datos para evidencias.

**Backup (solo esquema):**
```
pg_dump -h localhost -p 5432 -U guardian_user -s guardian_db > schema.sql
```

**Backup (schema + datos):**
```
pg_dump -h localhost -p 5432 -U guardian_user guardian_db > backup.sql
```

**Restauracion:**
```
psql -h localhost -p 5432 -U guardian_user -d guardian_db -f backup.sql
```

### Politica de mantenimiento
- Frecuencia sugerida: diario para backup completo, semanal para backup de esquema.
- Retencion: 7 dias para backups diarios, 4 semanas para semanales.
- Rotacion: eliminar backups mayores a la ventana de retencion.

### Monitoreo basico
Consultas sugeridas para evidencias:
```
SELECT now();
SELECT datname, numbackends FROM pg_stat_database WHERE datname = 'guardian_db';
SELECT relname, n_live_tup FROM pg_stat_user_tables ORDER BY n_live_tup DESC;
SELECT * FROM pg_stat_activity WHERE datname = 'guardian_db';
```

## Replicacion

### Opcion 1: Replicacion streaming (fisica)
Adecuada para alta disponibilidad. Requiere parametros en `postgresql.conf`:
- `wal_level = replica`
- `max_wal_senders = 5`
- `wal_keep_size = 64MB`

**Pasos base (resumen):**
1) Crear usuario de replicacion
```
CREATE ROLE repl WITH REPLICATION LOGIN PASSWORD 'repl_pass';
```
2) Configurar `pg_hba.conf` para la replica.
3) Ejecutar `pg_basebackup` desde el nodo secundario.

### Opcion 2: Replicacion logica
Adecuada para replicar tablas especificas o integrar servicios externos.

**Publicacion:**
```
CREATE PUBLICATION guardian_pub FOR ALL TABLES;
```

**Suscripcion (en replica):**
```
CREATE SUBSCRIPTION guardian_sub
CONNECTION 'host=... port=5432 dbname=guardian_db user=guardian_user password=guardian_pass'
PUBLICATION guardian_pub;
```

### Consideraciones
- En entorno academico, se documentan parametros y comandos aun sin replica real.
- La consistencia transaccional y el recovery son parte del plan de continuidad.

## Conclusiones
- La administracion local se soporta con Docker y backups automatizables.
- El esquema fisico se versiona en `src/main/resources/db/schema.sql`.
- La replicacion se documenta con opciones streaming y logica, alineadas a la rubrica.
