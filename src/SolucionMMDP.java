import java.util.ArrayList;

public class SolucionMMDP extends Solucion {

    //Constructor
    public SolucionMMDP() {
        super.solu = new ArrayList<Integer>();
    }

    //Valor de la FO en MDP: la distancia minima entre 2 nodos de la solucion
    @Override
    public double evalSol(Instancia instancia) {
        return 0;
    }

    //Realiza un copia de la solucion en un espacio nuevo de memoria
    @Override
    public Solucion copy() {
        return null;
    }
}


/*
 * Quiero coger la distancia minima de la solucion,
 * y esa distancia minima maximizarla lo maximo posible.
 * Para ello busco varias soluciones y de ellas me quedo con la que
 * tenga mayor distancia minima.
 *
 * */