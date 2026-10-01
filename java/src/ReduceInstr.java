/**
 * Representa la instruccion REDUCE, que condensa la lista en un unico valor.
 */
public class ReduceInstr extends Instruccion {
    /** Tipo de reduccion a aplicar: SUM, MAX o MIN */
    private final String tipo; // SUM, MAX o MIN

    public ReduceInstr(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toIR() {
        return "REDUCE|" + tipo;
    }
}
