import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MetodosDeMejora {

    // Busqueda Local Con Swap Y FirstImprovement:
    // sol = [5,6,3] otros nodos: other = {1,2,4}
    // foreach(elem from sol)=> foreach(otherElem from other) => swap(elem, otherElem) if(mejora la sol),la guardo, rompo el bucle y vuelvo a empezar el bucle pero con la nueva sol
    public static void busquedaLocalConSwapYFirstImprovement(Solucion solucion, Instancia instancia, long tStart, long tiempoMaxDeEjecucionEnSegundos) {

        double maxDistancia = solucion.evalSol(instancia);
        int nodoIndex = 0;
        boolean heVistoTodasLasSolucionesPosiblesConSwap1x1 = false;

        Random rnd = new Random(); //accedemos y probamos con swaps cada elemento de la sol en posiciones aleatorias
        List<Integer> SolNodePosToCheckInRandomOrder = rnd.ints(0, instancia.nNodosAElegir).distinct().limit(instancia.nNodosAElegir).boxed().collect(Collectors.toList()); //array de pos aleatorias del array sol.solu
        //System.out.println("positions of sol to modify in rnd order: "+SolNodePosToCheckInRandomOrder);

        while (!heVistoTodasLasSolucionesPosiblesConSwap1x1) {
            //System.out.println("solucion: "+solucion.solu+ " , idx: "+SolNodePosToCheckInRandomOrder.get(nodoIndex)+" ,  nodoIndex: "+nodoIndex);

            int idx = SolNodePosToCheckInRandomOrder.get(nodoIndex);
            //int nodoOrig = solucion.solu.get(idx);

            if (TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()-tStart) > tiempoMaxDeEjecucionEnSegundos) {
                //System.out.println("acabamos porque se acabo el tiempo!!!. Tiempo transcurrido: "+TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()-tStart));
                return;
            }

            for (int i = 0; i < instancia.nNodosTotales; i++) {

                if (!solucion.solu.contains(i)) { //si el nodo elegido de valor i no es un nodo de la solucion, entonces lo podemos coger...

                    int nodoEliminado = solucion.solu.remove(idx);
                    solucion.solu.add(idx, i);
                    if (solucion.evalSol(instancia) > maxDistancia) { //first improvement
                        //System.out.println("encontrada una sol mejor!: "+solucion.solu + " cambiando el elemento de la pos "+SolNodePosToCheckInRandomOrder.get(nodoIndex));
                        nodoIndex = -1; //si vemos uno mejor cambia la sol (empezamos de nuevo)
                        maxDistancia = solucion.evalSol(instancia);
                        break; //si he encontrado una sol mejor hago break y empiezo de nuevo a recorrer desde el primer nodo de la sol hasta el ultimo
                    }else{
                        solucion.solu.remove(idx);
                        solucion.solu.add(idx, nodoEliminado);
                    }
                }
            }
            //cuando termina el for sobre el primer nodo (nodoIndex = 0) ya he encontrado el mejor y no puedo mejorar el nodo de pos 0, asi que ahora paso a mejorar el segundo y asi sucesivamente
            nodoIndex++;
            if (nodoIndex >= instancia.nNodosAElegir) {
                //System.out.println("Busqueda local terminada!!!!!!!!!!!!!!!!!!!!!!!!");
                heVistoTodasLasSolucionesPosiblesConSwap1x1 = true;
            }
        }
    }
}