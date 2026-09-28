public class ReduceInstr extends Instruccion {
    private final String tipo; // SUM, MAX o MIN

    public ReduceInstr(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toIR() {
        return "REDUCE|" + tipo;
    }
}
