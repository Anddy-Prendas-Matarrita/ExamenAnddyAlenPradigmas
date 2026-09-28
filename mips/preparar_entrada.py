"""
Puente entre Python (etapa 2) y MIPS (etapa 3).

Lee resultado.txt, extrae RESULT y OPERATIONS, y los deja en dos
líneas de texto plano (entrada_mips.txt) para alimentarlos como
enteros al programa MIPS via entrada estandar (syscall 5 = read_int).

Esto NO fabrica ni edita resultado.txt: solo lee lo que la etapa
Python ya generó realmente.
"""

import sys


def main():
    ruta_resultado = sys.argv[1] if len(sys.argv) > 1 else "resultado.txt"
    ruta_salida = sys.argv[2] if len(sys.argv) > 2 else "entrada_mips.txt"

    resultado = None
    operaciones = None

    with open(ruta_resultado, "r") as f:
        for linea in f:
            linea = linea.strip()
            if linea.startswith("RESULT="):
                resultado = linea.split("=")[1]
            elif linea.startswith("OPERATIONS="):
                operaciones = linea.split("=")[1]

    if resultado is None or operaciones is None:
        print("ERROR: {} no contiene RESULT y OPERATIONS".format(ruta_resultado),
              file=sys.stderr)
        sys.exit(1)

    with open(ruta_salida, "w") as f:
        f.write(resultado + "\n")
        f.write(operaciones + "\n")

    print("Entrada para MIPS generada en {}: RESULT={} OPERATIONS={}".format(
        ruta_salida, resultado, operaciones))


if __name__ == "__main__":
    main()
