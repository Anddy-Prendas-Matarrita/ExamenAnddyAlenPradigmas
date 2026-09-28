"""
Etapa 2 - PYTHON: ejecucion funcional del pipeline MiniLang.

Lee programa.ir (generado por la etapa Java) y ejecuta las operaciones
FILTER / MAP / REDUCE usando estilo funcional: filter(), map() y
functools.reduce(). No se usan ciclos for/while en el nucleo de las
transformaciones FILTER y MAP (el unico "for" del archivo recorre las
lineas del IR, que es control del pipeline, no la transformacion en si).
"""

import sys
import operator
from functools import reduce as freduce

COMPARADORES = {
    ">": operator.gt,
    "<": operator.lt,
    ">=": operator.ge,
    "<=": operator.le,
    "==": operator.eq,
}

ARITMETICOS = {
    "+": operator.add,
    "-": operator.sub,
    "*": operator.mul,
}


def leer_ir(ruta):
    with open(ruta, "r") as f:
        return [linea.strip() for linea in f.readlines() if linea.strip()]


def ejecutar(lineas_ir):
    datos = []
    trazas = []
    operaciones_ejecutadas = 0
    resultado_final = None

    for linea in lineas_ir:
        partes = linea.split("|")
        tipo = partes[0]

        if tipo == "DATA":
            datos = list(map(int, partes[1].split(",")))
            trazas.append("DATA => {}".format(datos))

        elif tipo == "FILTER":
            comparador, valor_texto = partes[1], partes[2]
            valor = int(valor_texto)
            if comparador not in COMPARADORES:
                raise ValueError(
                    "Comparador no soportado en tiempo de ejecución: {}".format(comparador))
            funcion = lambda x, c=comparador, v=valor: COMPARADORES[c](x, v)
            datos = list(filter(funcion, datos))
            operaciones_ejecutadas += 1
            trazas.append("FILTER {} {} => {}".format(comparador, valor, datos))

        elif tipo == "MAP":
            operador_texto, valor_texto = partes[1], partes[2]
            valor = int(valor_texto)
            if operador_texto not in ARITMETICOS:
                raise ValueError(
                    "Operador no soportado en tiempo de ejecución: {}".format(operador_texto))
            funcion = lambda x, o=operador_texto, v=valor: ARITMETICOS[o](x, v)
            datos = list(map(funcion, datos))
            operaciones_ejecutadas += 1
            trazas.append("MAP {} {} => {}".format(operador_texto, valor, datos))

        elif tipo == "REDUCE":
            tipo_reduce = partes[1]
            if tipo_reduce == "SUM":
                resultado_final = freduce(operator.add, datos, 0)
            elif tipo_reduce == "MAX":
                if not datos:
                    raise ValueError("REDUCE MAX sobre una lista vacía no está definido")
                resultado_final = freduce(lambda a, b: a if a > b else b, datos)
            elif tipo_reduce == "MIN":
                if not datos:
                    raise ValueError("REDUCE MIN sobre una lista vacía no está definido")
                resultado_final = freduce(lambda a, b: a if a < b else b, datos)
            else:
                raise ValueError("Tipo de REDUCE desconocido: {}".format(tipo_reduce))
            operaciones_ejecutadas += 1
            trazas.append("REDUCE {} => {}".format(tipo_reduce, resultado_final))

        elif tipo == "PRINT":
            trazas.append("PRINT")

        else:
            raise ValueError("Instrucción de IR desconocida: {}".format(tipo))

    return resultado_final, operaciones_ejecutadas, trazas


def main():
    ruta_ir = sys.argv[1] if len(sys.argv) > 1 else "programa.ir"
    ruta_salida = sys.argv[2] if len(sys.argv) > 2 else "resultado.txt"

    print("=== Etapa 2: Python (ejecución funcional) ===")
    print("Leyendo: {}".format(ruta_ir))

    try:
        lineas_ir = leer_ir(ruta_ir)
        resultado, operaciones, trazas = ejecutar(lineas_ir)

        if resultado is None:
            raise ValueError(
                "El programa no contiene una operación REDUCE; no hay resultado final")

        with open(ruta_salida, "w") as f:
            for t in trazas:
                f.write(t + "\n")
            f.write("RESULT={}\n".format(resultado))
            f.write("OPERATIONS={}\n".format(operaciones))

        print("Ejecución correcta. Se generó: {}".format(ruta_salida))
        for t in trazas:
            print("  " + t)
        print("RESULT={}  OPERATIONS={}".format(resultado, operaciones))

    except FileNotFoundError:
        print("ERROR: no se encontró el archivo {}".format(ruta_ir), file=sys.stderr)
        sys.exit(1)
    except ValueError as ve:
        print("ERROR DE EJECUCIÓN: {}".format(ve), file=sys.stderr)
        sys.exit(2)


if __name__ == "__main__":
    main()
