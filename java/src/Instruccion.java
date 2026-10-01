/**
 * Clase abstracta base de toda la jerarquia de instrucciones en MiniLang.
 * Aqui se apoya el polimorfismo pedido en la Parte B: cada subclase
 * sabe convertirse a si misma en representacion intermedia (IR).
 */
public abstract class Instruccion {
    public abstract String toIR();
}
