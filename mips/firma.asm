# ============================================================
# Etapa 3 - MIPS: firma de verificacion
#
# Lee 'resultado' y 'operaciones' por entrada estandar
# (syscall 5, dos enteros, uno por linea -> ver preparar_entrada.py
# y entrada_mips.txt), calcula un checksum de verificacion y
# escribe firma.txt con toda la informacion.
#
# Incluye: registros, acceso a memoria, un ciclo (recorrido para
# contar digitos), operacion aritmetica (add, sub, div), operacion
# logica (xor, andi) y salto condicional (beq/bne/blt/bgez).
#
# NOTA: si se cambia el texto de las etiquetas .asciiz, se debe
# actualizar a mano la longitud (li $a2, N) usada al escribirlas.
# ============================================================

.data
nombre_archivo: .asciiz "firma.txt"
buffer:         .space 64
etiqueta_res:   .asciiz "RESULTADO="
etiqueta_ops:   .asciiz "OPERACIONES="
etiqueta_dig:   .asciiz "DIGITOS_RESULTADO="
etiqueta_chk:   .asciiz "CHECKSUM="
etiqueta_par:   .asciiz "PARIDAD="
texto_par:      .asciiz "PAR\n"
texto_impar:    .asciiz "IMPAR\n"
salto_linea:    .asciiz "\n"

.text
.globl main

main:
    # ---- Leer resultado y operaciones (syscall 5 = read_int) ----
    li   $v0, 5
    syscall
    move $t0, $v0          # t0 = resultado

    li   $v0, 5
    syscall
    move $t1, $v0          # t1 = operaciones

    # ---- Abrir firma.txt para escritura (syscall 13) ----
    li   $v0, 13
    la   $a0, nombre_archivo
    li   $a1, 1             # 1 = escritura, crea el archivo si no existe
    li   $a2, 0
    syscall
    move $s0, $v0           # s0 = descriptor de archivo

    # ---- Escribir "RESULTADO=" + valor ----
    la   $a1, etiqueta_res
    li   $a2, 10
    move $a0, $s0
    li   $v0, 15
    syscall

    move $a0, $t0
    jal  escribir_entero

    la   $a1, salto_linea
    li   $a2, 1
    move $a0, $s0
    li   $v0, 15
    syscall

    # ---- Escribir "OPERACIONES=" + valor ----
    la   $a1, etiqueta_ops
    li   $a2, 12
    move $a0, $s0
    li   $v0, 15
    syscall

    move $a0, $t1
    jal  escribir_entero

    la   $a1, salto_linea
    li   $a2, 1
    move $a0, $s0
    li   $v0, 15
    syscall

    # ---- Ciclo obligatorio: contar digitos de 'resultado' ----
    move $t2, $t0           # copia de resultado para el ciclo
    li   $t3, 0             # contador de digitos

    bne  $t2, $zero, contar_valor_normal
    li   $t3, 1             # si resultado es 0, tiene 1 digito
    j    contar_fin

contar_valor_normal:
    bgez $t2, contar_ciclo
    sub  $t2, $zero, $t2    # valor absoluto si resultado es negativo

contar_ciclo:
    beq  $t2, $zero, contar_fin
    li   $t9, 10
    div  $t2, $t9           # hi = t2 % 10, lo = t2 / 10
    mflo $t2                # t2 = t2 / 10
    addi $t3, $t3, 1
    j    contar_ciclo

contar_fin:
    # ---- Escribir "DIGITOS_RESULTADO=" + valor ----
    la   $a1, etiqueta_dig
    li   $a2, 18
    move $a0, $s0
    li   $v0, 15
    syscall

    move $a0, $t3
    jal  escribir_entero

    la   $a1, salto_linea
    li   $a2, 1
    move $a0, $s0
    li   $v0, 15
    syscall

    # ---- Calcular checksum ----
    xor  $t4, $t0, $t1      # operacion logica: resultado XOR operaciones
    add  $t4, $t4, 17       # operacion aritmetica
    add  $t4, $t4, $t3      # se incorpora el conteo de digitos del ciclo

    # ---- Escribir "CHECKSUM=" + valor ----
    la   $a1, etiqueta_chk
    li   $a2, 9
    move $a0, $s0
    li   $v0, 15
    syscall

    move $a0, $t4
    jal  escribir_entero

    la   $a1, salto_linea
    li   $a2, 1
    move $a0, $s0
    li   $v0, 15
    syscall

    # ---- Paridad del checksum (logica + salto condicional) ----
    andi $t5, $t4, 1        # t5 = checksum AND 1

    la   $a1, etiqueta_par
    li   $a2, 8
    move $a0, $s0
    li   $v0, 15
    syscall

    beq  $t5, $zero, es_par
    la   $a1, texto_impar
    li   $a2, 6
    j    escribir_paridad
es_par:
    la   $a1, texto_par
    li   $a2, 4
escribir_paridad:
    move $a0, $s0
    li   $v0, 15
    syscall

    # ---- Cerrar archivo (syscall 16) ----
    li   $v0, 16
    move $a0, $s0
    syscall

    # ---- Fin del programa ----
    li   $v0, 10
    syscall

# ------------------------------------------------------------
# Subrutina: escribir_entero
# Convierte el entero en $a0 a texto ASCII (maneja signo negativo)
# y lo escribe en el archivo cuyo descriptor esta en $s0.
# ------------------------------------------------------------
escribir_entero:
    addi $sp, $sp, -32
    sw   $ra, 0($sp)
    sw   $s1, 4($sp)
    sw   $s2, 8($sp)

    move $s1, $a0           # s1 = numero a convertir
    la   $s2, buffer
    li   $t6, 0             # bandera de signo negativo

    bgez $s1, no_negativo
    li   $t6, 1
    sub  $s1, $zero, $s1
no_negativo:

    li   $t7, 30
    add  $t8, $s2, $t7      # puntero al final del buffer
    sb   $zero, 0($t8)      # terminador

    bne  $s1, $zero, convertir_ciclo
    addi $t8, $t8, -1
    li   $t9, 48            # caracter '0'
    sb   $t9, 0($t8)
    j    convertir_fin

convertir_ciclo:
    beq  $s1, $zero, convertir_fin
    li   $t9, 10
    div  $s1, $t9           # hi = s1 % 10, lo = s1 / 10
    mfhi $t9                # t9 = residuo (digito)
    mflo $s1                # s1 = cociente
    addi $t9, $t9, 48       # digito -> caracter ASCII
    addi $t8, $t8, -1
    sb   $t9, 0($t8)
    j    convertir_ciclo

convertir_fin:
    beq  $t6, $zero, sin_signo
    addi $t8, $t8, -1
    li   $t9, 45             # caracter '-'
    sb   $t9, 0($t8)
sin_signo:

    add  $t7, $s2, 30
    sub  $a2, $t7, $t8       # longitud de la cadena resultante

    move $a1, $t8            # direccion de inicio de la cadena
    move $a0, $s0            # descriptor de archivo
    li   $v0, 15
    syscall

    lw   $ra, 0($sp)
    lw   $s1, 4($sp)
    lw   $s2, 8($sp)
    addi $sp, $sp, 32
    jr   $ra
