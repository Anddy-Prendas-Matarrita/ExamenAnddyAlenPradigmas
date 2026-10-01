import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Etapa 1 del pipeline: JAVA (lexer + parser + OOP).
 * Uso: java Main [programa.mini] [programa.ir]
 */
public class Main {
    /**
     * Metodo principal que ejecuta la Etapa 1 del pipeline.
     * Lee un archivo MiniLang, lo tokeniza, lo parsea y si es valido
     * lo exporta a la representacion intermedia (IR).
     *
     * @param args argumentos de linea de comandos (ruta de entrada y ruta de salida)
     */
    public static void main(String[] args) {
        String entrada = args.length > 0 ? args[0] : "programa.mini";
        String salida = args.length > 1 ? args[1] : "programa.ir";

        System.out.println("=== Etapa 1: Java (lexer + parser + OOP) ===");
        System.out.println("Leyendo: " + entrada);

        try {
            Lexer lexer = new Lexer();
            List<LineaTokens> tokens = lexer.tokenizar(entrada);

            Parser parser = new Parser();
            List<Instruccion> instrucciones = parser.parsear(tokens);

            try (PrintWriter pw = new PrintWriter(new FileWriter(salida))) {
                for (Instruccion instr : instrucciones) {
                    pw.println(instr.toIR()); // llamada polimorfica
                }
            }

            System.out.println("Programa válido. Se generó: " + salida);
            for (Instruccion instr : instrucciones) {
                System.out.println("  " + instr.toIR());
            }

        } catch (ParseException pe) {
            System.err.println("ERROR DE COMPILACIÓN: " + pe.getMessage());
            System.err.println("No se generó " + salida + " porque el programa no es válido.");
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("ERROR DE E/S: " + ioe.getMessage());
            System.exit(2);
        }
    }
}
