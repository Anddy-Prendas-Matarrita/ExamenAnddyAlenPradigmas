# Resultados esperados de los casos de prueba

Todos los valores de esta tabla fueron verificados ejecutando realmente
las etapas Java y Python del pipeline (no son valores inventados).

## Caso 1 — `caso1_valido.mini`
Programa completo y válido.

- `programa.ir`: `DATA|3,8,5,10,12` / `FILTER|>|5` / `MAP|*|2` / `REDUCE|SUM` / `PRINT`
- Traza: FILTER > 5 => [8, 10, 12] ; MAP * 2 => [16, 20, 24] ; REDUCE SUM => 60
- **RESULT=60, OPERATIONS=3**
- Comportamiento esperado: pipeline completo, `firma.txt` generado sin errores.

## Caso 2 — `caso2_operador_invalido.mini`
Usa el comparador inválido `>>` en la línea 2.

- Comportamiento esperado (**caso obligatorio de error**): la etapa Java
  detiene el pipeline y reporta:
  `ERROR DE COMPILACIÓN: Error en línea 2: comparador inválido '>>'`
- **No se genera `programa.ir`**, por lo que las etapas Python y MIPS
  nunca se ejecutan. Este es el caso de error exigido en la sección 8
  del enunciado ("al menos un caso de error donde el pipeline se
  detenga correctamente").

## Caso 3 — `caso3_sin_data.mini`
El programa no inicia con `DATA`.

- Comportamiento esperado: error en la línea 1:
  `ERROR DE COMPILACIÓN: Error en línea 1: el programa debe iniciar con DATA`
- No se genera `programa.ir`.

## Caso 4 — `caso4_reduce_max.mini`
Usa `REDUCE MAX`.

- Traza: FILTER >= 3 => [4, 9, 15, 7] ; REDUCE MAX => 15
- **RESULT=15, OPERATIONS=2**

## Caso 5 — `caso5_filter_vacio.mini`
El `FILTER` deja la lista vacía.

- Traza: FILTER > 100 => [] ; REDUCE SUM => 0
- **RESULT=0, OPERATIONS=2**
- Nota de diseño: `REDUCE SUM` sobre una lista vacía está definido como
  0 (identidad de la suma), por lo que este caso termina de forma
  válida, no como error. En cambio, `REDUCE MAX` o `REDUCE MIN` sobre
  una lista vacía sí se tratan como error en `ejecutar.py`, porque el
  máximo/mínimo de un conjunto vacío no está definido.

## Caso 6 — `caso6_multiples_ops.mini`
Dos pares de `FILTER`/`MAP` consecutivos.

- Traza: FILTER > 4 => [6, 8, 10, 12] ; MAP * 3 => [18, 24, 30, 36] ;
  FILTER < 30 => [18, 24] ; MAP - 1 => [17, 23] ; REDUCE SUM => 40
- **RESULT=40, OPERATIONS=5**

---

Para completar la evidencia pedida en el entregable 4 (`programa.ir`,
`resultado.txt`, `firma.txt` "generados por una ejecución real"), la
pareja debe correr estos mismos casos en su propia máquina siguiendo el
README y adjuntar los archivos de salida reales, incluyendo el de MIPS
(`firma.txt`), que aquí no se pudo generar por no tener un simulador
MIPS disponible en este entorno de preparación.
