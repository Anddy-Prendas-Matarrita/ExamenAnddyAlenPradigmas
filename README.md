# MiniLang Pipeline — Reto Práctico (Parte B)

Pipeline de 3 etapas (Java → Python → MIPS) que lee un programa escrito
en el mini-lenguaje `MiniLang`, lo valida, lo traduce a una
representación intermedia, ejecuta las transformaciones en estilo
funcional y genera una firma de verificación en MIPS.

## Estructura del proyecto

```
minilang-pipeline/
├── README.md                    <- este archivo
├── documento_decisiones.md      <- respuestas a las 5 preguntas de reflexión
├── diagrama_pipeline.md         <- diagrama del pipeline y contratos de archivos
├── programa.mini                <- programa de ejemplo (el del enunciado)
├── java/
│   └── src/
│       ├── Main.java
│       ├── Lexer.java
│       ├── LineaTokens.java
│       ├── Parser.java
│       ├── ParseException.java
│       ├── Instruccion.java     <- clase abstracta base
│       ├── DataInstr.java
│       ├── FilterInstr.java
│       ├── MapInstr.java
│       ├── ReduceInstr.java
│       └── PrintInstr.java
├── python/
│   └── ejecutar.py              <- etapa 2 (estilo funcional)
├── mips/
│   ├── preparar_entrada.py      <- puente resultado.txt -> entrada MIPS
│   └── firma.asm                <- etapa 3 (checksum de verificación)
└── tests/
    ├── caso1_valido.mini
    ├── caso2_operador_invalido.mini
    ├── caso3_sin_data.mini
    ├── caso4_reduce_max.mini
    ├── caso5_filter_vacio.mini
    ├── caso6_multiples_ops.mini
    └── resultados_esperados.md  <- qué debe dar cada caso
```

## Requisitos

- JDK 11 o superior (`javac`, `java`)
- Python 3.8 o superior
- Un simulador MIPS. Se recomienda **MARS** (`Mars.jar`), disponible en
  https://dpetersanderson.github.io/ (también sirve SPIM si se prefiere,
  ajustando la sintaxis de syscalls si difiere).

## Cómo ejecutar el pipeline completo

Desde la carpeta raíz `minilang-pipeline/`:

### 1. Etapa Java (compilar una sola vez)

```bash
cd java/src
javac *.java
```

Ejecutar sobre el programa de ejemplo:

```bash
java -cp . Main ../../programa.mini ../../programa.ir
```

- Si el programa es válido: se imprime cada instrucción y se genera
  `programa.ir`.
- Si hay un error léxico o sintáctico: se imprime
  `ERROR DE COMPILACIÓN: Error en línea N: ...` y **no** se genera
  `programa.ir` (el pipeline se detiene ahí).

### 2. Etapa Python

```bash
cd ../../python
python3 ejecutar.py ../programa.ir ../resultado.txt
```

Genera `resultado.txt` con la traza de cada operación y las líneas
`RESULT=<valor>` y `OPERATIONS=<cantidad>`.

### 3. Etapa MIPS

```bash
cd ../mips
python3 preparar_entrada.py ../resultado.txt entrada_mips.txt
```

Esto crea `entrada_mips.txt` con dos líneas (`RESULT` y `OPERATIONS`
extraídos de `resultado.txt`).

Luego correr `firma.asm` en MARS, redirigiendo esa entrada por
consola. Con MARS en modo texto (`nc` = no GUI):

```bash
java -jar Mars.jar nc firma.asm < entrada_mips.txt
```

(ajustar la ruta a `Mars.jar` según donde lo tengan instalado). El
programa MIPS crea `firma.txt` en la carpeta desde donde se ejecuta,
con `RESULTADO`, `OPERACIONES`, `DIGITOS_RESULTADO`, `CHECKSUM` y
`PARIDAD`.

> Si usan la interfaz gráfica de MARS en vez de línea de comandos,
> simplemente carguen `firma.asm`, ejecuten, y cuando pida los dos
> enteros por consola (Run I/O) escriban los valores de
> `entrada_mips.txt` (primero `RESULT`, luego `OPERATIONS`).

## Ejecutar los casos de prueba obligatorios

Cada archivo en `tests/*.mini` se corre repitiendo los 3 pasos
anteriores, cambiando la ruta del archivo de entrada. Por ejemplo,
para el caso 4:

```bash
java -cp java/src Main tests/caso4_reduce_max.mini caso4.ir
python3 python/ejecutar.py caso4.ir caso4_resultado.txt
```

El resultado que debe dar cada caso (ya verificado) está documentado
en `tests/resultados_esperados.md`. El **caso 2** (`caso2_operador_invalido.mini`)
es intencionalmente inválido: sirve como el caso de error obligatorio
donde el pipeline debe detenerse mostrando evidencia clara del error
(sección 8 del enunciado).

## Notas de diseño importantes (para la defensa)

- La jerarquía `Instruccion` (Java) usa **herencia y polimorfismo
  reales**: cada subclase implementa `toIR()` a su manera, y `Main`
  simplemente llama `instr.toIR()` sin preguntar de qué tipo es cada
  instrucción.
- En Python, `FILTER` y `MAP` se implementan sin `for`/`while` en su
  núcleo (usan `filter()` y `map()`); `REDUCE` usa siempre
  `functools.reduce()`, incluso para `MAX`/`MIN`, para mantener el
  estilo funcional de forma consistente.
- `REDUCE SUM` sobre una lista vacía da `0` (caso válido, no error).
  `REDUCE MAX`/`MIN` sobre una lista vacía sí se trata como error,
  porque el máximo/mínimo de un conjunto vacío no está definido.
- El programa MIPS nunca usa constantes fijas para el checksum: lee
  `resultado` y `operaciones` como entrada real, y además hace un
  recorrido (ciclo) para contar los dígitos del resultado, que también
  se incorpora al cálculo.

## Entregables pendientes que debe completar la pareja

Este proyecto ya cubre el código de las tres etapas, el diagrama, el
documento de decisiones y los 6 casos de prueba con su documentación.
Aún deben:

1. Ejecutar realmente el pipeline completo (incluyendo MIPS en MARS o
   SPIM) y adjuntar los archivos `programa.ir`, `resultado.txt` y
   `firma.txt` reales de esa ejecución.
2. Grabar el video de 4 a 6 minutos mostrando una ejecución completa y
   el caso de error (caso 2).
3. Revisar y personalizar `documento_decisiones.md` para poder
   defenderlo con sus propias palabras.
4. Si agregan alguna operación extra al mini-lenguaje, documentarla y
   añadir sus propios casos de prueba (sin eliminar DATA/FILTER/MAP/
   REDUCE/PRINT).
