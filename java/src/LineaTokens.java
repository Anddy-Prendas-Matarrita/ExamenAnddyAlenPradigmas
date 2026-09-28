/**
 * Representa una linea del archivo fuente ya separada en tokens,
 * junto con el numero de linea original (para reportar errores).
 */
public class LineaTokens {
    public final int numeroLinea;
    public final String[] tokens;

    public LineaTokens(int numeroLinea, String[] tokens) {
        this.numeroLinea = numeroLinea;
        this.tokens = tokens;
    }
}
