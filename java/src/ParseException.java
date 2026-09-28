/**
 * Excepcion propia para errores lexicos y sintacticos.
 * Siempre incluye el numero de linea donde ocurrio el problema,
 * como pide el enunciado ("reportar numero de linea").
 */
public class ParseException extends Exception {
    private final int linea;

    public ParseException(String mensaje, int linea) {
        super("Error en línea " + linea + ": " + mensaje);
        this.linea = linea;
    }

    public int getLinea() {
        return linea;
    }
}
