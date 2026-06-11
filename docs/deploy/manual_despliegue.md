# Manual de Despliegue

## Requisitos
- Docker + Docker Compose
- Java 21
- Node/Android Studio (app movil)

## Backend
1) Configurar variables (si aplica)
2) Levantar Postgres
```
docker compose up -d
```
3) Ejecutar backend
```
./mvnw spring-boot:run
```

## Ngrok
```
ngrok http 8080
```
Copiar URL y colocarla en `RetrofitClient`.

## App movil
1) Abrir en Android Studio
2) Actualizar URL base
3) Ejecutar en emulador o dispositivo

## Validaciones
- Verificar endpoints con Postman
- Verificar tablas en DB
