/**
 * Representa la instruccion PRINT, que marca el final de las operaciones
 * e indica que se debe imprimir el resultado final.
 */
public class PrintInstr extends Instruccion {
    @Override
    public String toIR() {
        return "PRINT";
    }
}
