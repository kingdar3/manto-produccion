# Evidencias de monitoreo y administracion de BD

## Consultas sugeridas
```
SELECT now();
SELECT datname, numbackends FROM pg_stat_database WHERE datname = 'guardian_db';
SELECT relname, n_live_tup FROM pg_stat_user_tables ORDER BY n_live_tup DESC;
```

## Evidencias
- Capturas de resultados en DBeaver
- Captura de diagrama ER
