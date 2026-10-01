import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Analizador lexico muy simple: separa cada linea en tokens por espacios
 * y valida que cada token use unicamente caracteres permitidos por el
 * lenguaje (letras, digitos y los simbolos > < = + - *).
 *
 * Si aparece un caracter no permitido, se lanza ParseException con el
 * numero de linea exacto (error lexico).
 */
public class Lexer {

    /**
     * Lee un archivo de texto y separa cada linea en una lista de tokens validos.
     *
     * @param rutaArchivo la ruta del archivo a leer
     * @return una lista de objetos LineaTokens
     * @throws IOException si ocurre un error de lectura de archivo
     * @throws ParseException si se encuentra un caracter no valido en algun token
     */
    public List<LineaTokens> tokenizar(String rutaArchivo) throws IOException, ParseException {
        List<LineaTokens> lineas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            int numeroLinea = 0;

            while ((linea = br.readLine()) != null) {
                numeroLinea++;
                String contenido = linea.trim();

                if (contenido.isEmpty()) {
                    continue; // se ignoran lineas en blanco
                }

                String[] tokens = contenido.split("\\s+");

                for (String tok : tokens) {
                    if (!tok.matches("[A-Za-z0-9<>=+\\-*]+")) {
                        throw new ParseException(
                                "token léxico inválido '" + tok + "'", numeroLinea);
                    }
                }

                lineas.add(new LineaTokens(numeroLinea, tokens));
            }
        }

        return lineas;
    }
}
