# Plan de Pruebas del Sistema

## Objetivo
Validar que el sistema cumple los requisitos funcionales, de seguridad y de integracion con base de datos.

## Alcance
- Registro y login
- Vinculos host-protegido
- Emergencias y audio
- Verificaciones de identidad

## Casos de prueba

### CP-01 Registro de usuario
- Precondicion: sistema activo
- Pasos: registrar usuario en app
- Resultado esperado: usuario creado en BD

### CP-02 Generar invitacion
- Precondicion: usuario host autenticado
- Pasos: generar codigo de invitacion
- Resultado esperado: invitacion creada

### CP-03 Confirmar vinculo
- Precondicion: invitacion valida
- Pasos: protegido ingresa PIN
- Resultado esperado: link activo

### CP-04 Emergencia
- Precondicion: vinculo activo
- Pasos: protegido presiona SOS
- Resultado esperado: emergencia activa + notificacion

### CP-05 Subida de audio
- Precondicion: emergencia activa/resuelta
- Pasos: terminar emergencia
- Resultado esperado: audio cargado y disponible

### CP-06 Resolucion
- Precondicion: emergencia activa
- Pasos: host resuelve
- Resultado esperado: estado RESOLVED

## Evidencias
- Capturas de pantalla
- Logs de backend
- Registro de DB
