import java.util.ArrayList;
import java.util.List;

public abstract class Solucion {

    protected List<Integer> solu;

    public Solucion(){
        solu = new ArrayList<Integer>();
    }

    //evaluar funcion objetivo => calcula el valor de la funcion objetivo dada una solucion x=[a,b,c]
    public abstract double evalSol(Instancia instancia);

    //copiar el objeto y devolverlo (para evitar referencias (del attrib solu) a un solo arraylist entre distintas copias)
    public abstract Solucion copy();
}
