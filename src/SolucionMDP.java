import java.util.ArrayList;
import java.util.List;

public class SolucionMDP extends Solucion {

    //Constructor
    public SolucionMDP() {
        super.solu = new ArrayList<Integer>();
    }

    //Valor de la FO en MDP: sum de distancias entre nodos de la solucion
    @Override
    public double evalSol(Instancia instancia) {
        if(this.solu.isEmpty()) {/*System.out.println("solucion vacia!!!!!!");*/ return -1.0;}
        double valorFOcalculado = 0;
        for (int i = 0; i < super.solu.size()-1; i++) {
            for (int j = i+1; j < super.solu.size(); j++) {
                valorFOcalculado += instancia.matrizDistancias[super.solu.get(i)][super.solu.get(j)];
            }
        }
        return valorFOcalculado;
    }

    //Realiza un copia de la solucion en un espacio nuevo de memoria
    @Override
    public Solucion copy() {
        Solucion copiaDeLaSol = new SolucionMDP();
        copiaDeLaSol.solu = new ArrayList<>(this.solu);
        return copiaDeLaSol;
    }
}