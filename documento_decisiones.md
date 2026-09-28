# Decisiones de lenguaje y paradigmas — MiniLang Pipeline

> Nota para la pareja: este documento es un punto de partida. Deben
> leerlo, ajustarlo a su propia implementación y estar listos para
> defender cada decisión, tal como exige la sección 12 del examen.

## 1. ¿Por qué Java resulta adecuado para la etapa de análisis y modelado de instrucciones?

Java es un lenguaje de tipado estático y orientado a objetos, lo que
encaja bien con una etapa cuyo trabajo central es *reconocer estructura*
(lexer/parser) y *modelarla como datos* (la jerarquía `Instruccion`).
El tipado estático detecta en tiempo de compilación errores de
estructura del propio compilador (por ejemplo, olvidar implementar
`toIR()` en una subclase), y las clases abstractas con herencia
permiten representar cada tipo de instrucción del mini-lenguaje como
un objeto independiente, sin una gran cadena de `if/else` sobre
cadenas de texto. Además, el manejo de excepciones de Java (`try/catch`)
es natural para reportar errores léxicos y sintácticos con su número
de línea, deteniendo el proceso de forma controlada.

## 2. ¿Qué cambia conceptualmente entre describir una transformación con estilo imperativo y funcional?

En estilo imperativo, una transformación se describe como una
secuencia de pasos que modifican una variable de estado (por ejemplo,
un ciclo `for` que acumula un total). El foco está en *cómo* se llega
al resultado, paso a paso, y en el estado intermedio de las variables.
En estilo funcional (como en la etapa Python de este pipeline), la
transformación se describe como la aplicación de una función pura
sobre una colección completa (`filter`, `map`, `reduce`): no hay una
variable mutable que se va actualizando manualmente, sino una
composición de funciones que produce un nuevo valor a partir del
anterior. El foco pasa de *cómo iterar* a *qué transformación aplicar*.

## 3. ¿Qué información se pierde o se conserva al convertir `programa.mini` a `programa.ir`?

Se conserva toda la información *semántica* necesaria para ejecutar el
programa: los datos iniciales, cada operación con sus parámetros
(comparador/operador y número) y el tipo de reducción. Se pierde,
en cambio, información puramente *sintáctica* del texto original: los
números de línea, el formato exacto de espacios, y cualquier
posibilidad de error léxico o sintáctico (que ya fue resuelta y
descartada en la etapa Java). En otras palabras, `programa.ir` es una
forma "limpia" y sin ambigüedad de las mismas instrucciones, lista
para ser interpretada mecánicamente sin volver a validar la gramática.

## 4. ¿Por qué la representación intermedia puede compararse con una fase de un compilador?

Un compilador real suele traducir el código fuente a una representación
intermedia (IR) antes de generar código final o interpretarlo, porque
esa IR es más simple, uniforme y desacoplada del lenguaje de entrada.
En este proyecto, `programa.ir` cumple exactamente ese rol: es el
resultado del análisis léxico/sintáctico (y aquí también semántico
básico) de la etapa Java, y sirve de contrato estable para que la
etapa Python (un lenguaje y paradigma distintos) pueda ejecutar el
programa sin tener que conocer la gramática original de `programa.mini`.

## 5. ¿Qué ventajas y costos aparecen al integrar tres lenguajes en lugar de resolver todo con uno?

**Ventajas:** cada etapa usa el lenguaje/paradigma más natural para su
tarea (Java para modelar estructura con OOP, Python para expresar
transformaciones de datos en estilo funcional y de forma concisa, MIPS
para mostrar el nivel más bajo de ejecución con registros y memoria
explícitos). Esto también obliga a definir contratos de datos claros
entre etapas, lo cual es una práctica realista en sistemas grandes
(microservicios, pipelines de datos, etc.).

**Costos:** hay más piezas que coordinar (tres entornos de ejecución,
tres sintaxis distintas), la depuración de errores que cruzan etapas es
más difícil, y el "acoplamiento" pasa a vivir en el formato de los
archivos intermedios (`programa.ir`, `resultado.txt`, `entrada_mips.txt`)
en vez de en llamadas a función directas. Un cambio en el formato de un
archivo intermedio obliga a revisar todas las etapas que lo leen o
escriben.
