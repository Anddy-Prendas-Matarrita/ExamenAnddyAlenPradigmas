# Diagrama del pipeline y contratos de archivos

## Diagrama

```
programa.mini
     |
     v
+-----------------------------+
|  JAVA (Etapa 1)             |
|  Lexer -> Parser -> OOP     |
|  Jerarquia: Instruccion     |
|  (Data/Filter/Map/Reduce/   |
|   Print)Instr               |
+---------------+-------------+
                | programa.ir
                v
+-----------------------------+
|  PYTHON (Etapa 2)           |
|  filter() / map() /         |
|  functools.reduce()         |
+---------------+-------------+
                | resultado.txt
                v
+-----------------------------+
|  preparar_entrada.py        |
|  (extrae RESULT/OPERATIONS) |
+---------------+-------------+
                | entrada_mips.txt
                v
+-----------------------------+
|  MIPS (Etapa 3)             |
|  checksum / verificacion    |
+---------------+-------------+
                |
                v
            firma.txt
```

## Contratos de archivos

**`programa.mini`** — texto plano, una instrucción por línea, según la
gramática del enunciado (`DATA`, `FILTER`, `MAP`, `REDUCE`, `PRINT`).

**`programa.ir`** — texto plano, una instrucción intermedia por línea,
generado solo si `programa.mini` es válido:
```
DATA|n1,n2,n3,...
FILTER|<comparador>|<numero>
MAP|<operador>|<numero>
REDUCE|<SUM|MAX|MIN>
PRINT
```

**`resultado.txt`** — texto plano generado por Python:
```
<una línea de traza por cada operación ejecutada>
RESULT=<entero>
OPERATIONS=<entero>
```

**`entrada_mips.txt`** — puente hacia MIPS, dos líneas, un entero cada
una (en este orden): `RESULT` y luego `OPERATIONS`. Se alimenta al
programa MIPS por entrada estándar.

**`firma.txt`** — texto plano generado por el programa MIPS:
```
RESULTADO=<entero>
OPERACIONES=<entero>
DIGITOS_RESULTADO=<entero>
CHECKSUM=<entero>
PARIDAD=<PAR|IMPAR>
```
