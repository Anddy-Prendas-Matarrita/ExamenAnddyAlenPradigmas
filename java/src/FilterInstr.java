/**
 * Representa la instruccion FILTER, encargada de filtrar valores segun un comparador.
 */
public class FilterInstr extends Instruccion {
    /** El operador de comparacion (ej. ">", "<", "==") */
    private final String comparador;
    /** El valor numerico contra el que se compara */
    private final int numero;

    public FilterInstr(String comparador, int numero) {
        this.comparador = comparador;
        this.numero = numero;
    }

    @Override
    public String toIR() {
        return "FILTER|" + comparador + "|" + numero;
    }
}
