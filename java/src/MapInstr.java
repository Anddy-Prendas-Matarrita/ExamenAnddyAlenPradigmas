/**
 * Representa la instruccion MAP, que aplica una operacion aritmetica a cada elemento.
 */
public class MapInstr extends Instruccion {
    /** El operador aritmetico (ej. "+", "-", "*") */
    private final String operador;
    /** El valor constante que se aplicara en la operacion */
    private final int numero;

    public MapInstr(String operador, int numero) {
        this.operador = operador;
        this.numero = numero;
    }

    @Override
    public String toIR() {
        return "MAP|" + operador + "|" + numero;
    }
}
