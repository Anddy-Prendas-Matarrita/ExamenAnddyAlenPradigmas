import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Analizador sintactico. Recibe las lineas ya tokenizadas por el Lexer
 * y construye la lista de Instruccion (jerarquia OOP), validando la
 * gramatica definida en el enunciado:
 *
 * <programa>   ::= <data> <operacion> { <operacion> } "PRINT"
 * <data>       ::= "DATA" <numero> { <numero> }
 * <operacion>  ::= <filter> | <map> | <reduce>
 * <filter>     ::= "FILTER" <comparador> <numero>
 * <map>        ::= "MAP" <aritmetico> <numero>
 * <reduce>     ::= "REDUCE" ("SUM" | "MAX" | "MIN")
 */
public class Parser {

    private static final Set<String> COMPARADORES =
            new HashSet<>(Arrays.asList(">", "<", ">=", "<=", "=="));
    private static final Set<String> ARITMETICOS =
            new HashSet<>(Arrays.asList("+", "-", "*"));
    private static final Set<String> REDUCE_TIPOS =
            new HashSet<>(Arrays.asList("SUM", "MAX", "MIN"));

    public List<Instruccion> parsear(List<LineaTokens> lineas) throws ParseException {
        List<Instruccion> instrucciones = new ArrayList<>();

        if (lineas.isEmpty()) {
            throw new ParseException("el programa está vacío", 0);
        }

        LineaTokens primera = lineas.get(0);
        if (!primera.tokens[0].equals("DATA")) {
            throw new ParseException("el programa debe iniciar con DATA", primera.numeroLinea);
        }

        boolean vistoPrint = false;

        for (int i = 0; i < lineas.size(); i++) {
            LineaTokens lt = lineas.get(i);
            String[] tk = lt.tokens;
            String palabraClave = tk[0];

            if (vistoPrint) {
                throw new ParseException(
                        "no se permiten instrucciones después de PRINT", lt.numeroLinea);
            }

            switch (palabraClave) {
                case "DATA": {
                    if (i != 0) {
                        throw new ParseException(
                                "DATA solo puede aparecer una vez, al inicio", lt.numeroLinea);
                    }
                    if (tk.length < 2) {
                        throw new ParseException(
                                "DATA requiere al menos un número", lt.numeroLinea);
                    }
                    List<Integer> numeros = new ArrayList<>();
                    for (int j = 1; j < tk.length; j++) {
                        numeros.add(parseEntero(tk[j], lt.numeroLinea));
                    }
                    instrucciones.add(new DataInstr(numeros));
                    break;
                }
                case "FILTER": {
                    if (tk.length != 3) {
                        throw new ParseException(
                                "FILTER requiere un comparador y un número", lt.numeroLinea);
                    }
                    if (!COMPARADORES.contains(tk[1])) {
                        throw new ParseException(
                                "comparador inválido '" + tk[1] + "'", lt.numeroLinea);
                    }
                    int num = parseEntero(tk[2], lt.numeroLinea);
                    instrucciones.add(new FilterInstr(tk[1], num));
                    break;
                }
                case "MAP": {
                    if (tk.length != 3) {
                        throw new ParseException(
                                "MAP requiere un operador y un número", lt.numeroLinea);
                    }
                    if (!ARITMETICOS.contains(tk[1])) {
                        throw new ParseException(
                                "operador aritmético inválido '" + tk[1] + "'", lt.numeroLinea);
                    }
                    int num = parseEntero(tk[2], lt.numeroLinea);
                    instrucciones.add(new MapInstr(tk[1], num));
                    break;
                }
                case "REDUCE": {
                    if (tk.length != 2) {
                        throw new ParseException(
                                "REDUCE requiere SUM, MAX o MIN", lt.numeroLinea);
                    }
                    if (!REDUCE_TIPOS.contains(tk[1])) {
                        throw new ParseException(
                                "tipo de REDUCE inválido '" + tk[1] + "'", lt.numeroLinea);
                    }
                    instrucciones.add(new ReduceInstr(tk[1]));
                    break;
                }
                case "PRINT": {
                    if (tk.length != 1) {
                        throw new ParseException("PRINT no acepta argumentos", lt.numeroLinea);
                    }
                    instrucciones.add(new PrintInstr());
                    vistoPrint = true;
                    break;
                }
                default:
                    throw new ParseException(
                            "instrucción desconocida '" + palabraClave + "'", lt.numeroLinea);
            }
        }

        if (!vistoPrint) {
            throw new ParseException(
                    "el programa debe terminar con PRINT",
                    lineas.get(lineas.size() - 1).numeroLinea);
        }

        return instrucciones;
    }

    private int parseEntero(String texto, int numeroLinea) throws ParseException {
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new ParseException(
                    "se esperaba un número entero, se encontró '" + texto + "'", numeroLinea);
        }
    }
}
