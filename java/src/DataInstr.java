import java.util.List;

/**
 * Representa la instruccion inicial DATA, que carga los numeros a evaluar.
 */
public class DataInstr extends Instruccion {
    /**
     * Lista de numeros enteros cargados.
     */
    private final List<Integer> numeros;

    public DataInstr(List<Integer> numeros) {
        this.numeros = numeros;
    }

    public List<Integer> getNumeros() {
        return numeros;
    }

    @Override
    public String toIR() {
        StringBuilder sb = new StringBuilder("DATA|");
        for (int i = 0; i < numeros.size(); i++) {
            sb.append(numeros.get(i));
            if (i < numeros.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
