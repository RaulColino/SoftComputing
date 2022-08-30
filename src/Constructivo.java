import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Constructivo {


    //EL CONSTRUCTIVO ALEATORIO ES IGUAL EN MDP Y MMDP PERO CAMBIA EL TIPO DE INSTANCIA HEREDADA DE SOLUCION
    //construye una solucion (MDP o MMDP) de forma aleatoria
    public static void constructivoAleatorio(Instancia instancia, Solucion sol) {
        sol.solu = devolverSolucionRandomizada(instancia);
    }

    //construye una solucion con multiarranque voraz (tendriamos que hacer el constructivo del grasp aqui)
    public static void constructivoAleatorizadoMiope(Instancia instancia, Solucion sol, double alpha) {
        int px; //elem de la solucion
        List<Integer> listaDeCandidatos;
        List<Integer> listaDeCandidatosRestringidaRCL;
        List<Integer> costes; //costes de cada candidato

        //elegimos nodo al azar y lo añadimos a la solucion
        int verticeAleatorio = devolverNodoRandom(instancia);
        sol.solu.add(verticeAleatorio);
        //System.out.println("verticeAleatorio:"+verticeAleatorio);

        //el resto de los nodos los metemos en la lista de candidatos
        listaDeCandidatos =  IntStream.range(0,instancia.nNodosTotales).boxed().collect(Collectors.toList());
        listaDeCandidatos.remove(verticeAleatorio);
        //System.out.println("GRASP:listaDeCandidatos: "+listaDeCandidatos);

        while( sol.solu.size() < instancia.nNodosAElegir ){ //mientras no tengamos los nodos requeridos...
            double distMinYMaxAOtroNodo[]  = getDistMinYMaxAOtroNodo(verticeAleatorio, instancia, listaDeCandidatos); //[dist min, dist max]
            double distMinAOtroNodo =  distMinYMaxAOtroNodo[0]; // distancia minima entre verticeAleatorio y otro
            double distMaxAOtroNodo = distMinYMaxAOtroNodo[1]; // distancia maxima entre verticeAleatorio y otro
            double valorDistMinAceptable = distMaxAOtroNodo - alpha*(distMaxAOtroNodo-distMinAOtroNodo);
            //listaDeCandidatosRestringidaRCL = getNodosQueSuperanElValorDeDistanciaMinimaAceptable(valorDistMinAceptable, listaDeCandidatos);
            //int nuevoNodoCandidato = getNodoRandomFromListaCandidatos(listaDeCandidatosRestringidaRCL);
            int nuevoNodoCandidato = getNuevoNodoCandidatoRandomQueCumpleConValorDistMinAceptable(valorDistMinAceptable,listaDeCandidatos, instancia, verticeAleatorio);
            sol.solu.add(nuevoNodoCandidato);
            listaDeCandidatos.remove(listaDeCandidatos.indexOf(nuevoNodoCandidato));
        }
    }

    //fase constructiva del VNS
    public static void shake(Solucion solNueva, int k, Instancia instancia) {
        Random random = new Random();

        for (int i = 0; i < k; i++) {
            int nodoNuevoAleatorio;
            do {
                nodoNuevoAleatorio = random.nextInt(instancia.nNodosTotales);
            } while(solNueva.solu.contains(nodoNuevoAleatorio));
            int nodeSolIndexToSwap = random.nextInt(solNueva.solu.size());
            solNueva.solu.remove(nodeSolIndexToSwap);
            solNueva.solu.add(nodeSolIndexToSwap, nodoNuevoAleatorio);
        }
    }


    private static List<Integer> devolverSolucionRandomizada(Instancia instancia) {
        Random random = new Random();
        // generate nNodesReq random nodes
        List<Integer> myRandomNodesChosen = random.ints(0, instancia.nNodosTotales)
                .distinct().limit(instancia.nNodosAElegir).boxed().collect(Collectors.toList());
        return myRandomNodesChosen;
    }


    private static Integer devolverNodoRandom(Instancia instancia) {
        Random random = new Random();
        Integer nodoAleatorio = random.nextInt(instancia.nNodosTotales);
        return nodoAleatorio;
    }


    private static double[] getDistMinYMaxAOtroNodo(int verticeAleatorio, Instancia instancia, List<Integer> listaDeCandidatos) {
        double distMinYMaxAOtroNodo[] = {Double.MAX_VALUE, -1};
        for (Integer nodoI: listaDeCandidatos) {
            distMinYMaxAOtroNodo[0] = Math.min(instancia.matrizDistancias[verticeAleatorio][nodoI], distMinYMaxAOtroNodo[0]);
            distMinYMaxAOtroNodo[1] = Math.max(instancia.matrizDistancias[verticeAleatorio][nodoI], distMinYMaxAOtroNodo[1]);
        }
        return distMinYMaxAOtroNodo;
    }

    private static double getDistAlNodoDeDistMaxA(int verticeAleatorio) {
        return 0;
    }

    private static int getNuevoNodoCandidatoRandomQueCumpleConValorDistMinAceptable(double valorDistMinAceptable, List<Integer> listaDeCandidatos, Instancia instancia, int verticeAleatorioAnterior) {
        //elijo un nodo aleatorio de la lista de candidatos...
        Random random = new Random();
        int pos = random.nextInt(listaDeCandidatos.size());
        //System.out.println("pos: "+pos);
        Integer nuevoNodoAleatorio;
        while (true) { //(este bucle no puede ser infinito porque en la listaDeCandidatos siempre hay un nodo que tiene valorDistMinAceptable igual o mayor)
            nuevoNodoAleatorio = listaDeCandidatos.get(pos);
            //supera o iguala valorDistMinAceptable?...
            if (instancia.matrizDistancias[nuevoNodoAleatorio][verticeAleatorioAnterior] >= valorDistMinAceptable) {
                //System.out.println("Encontrado nodo con valorDistMinAceptable=" + valorDistMinAceptable + " igual o mayor: nodo " + nuevoNodoAleatorio + ", valorDist= " + instancia.matrizDistancias[nuevoNodoAleatorio][verticeAleatorioAnterior]);
                return nuevoNodoAleatorio; //si: ...lo devuelvo
            }else{
                pos = (pos+1) % listaDeCandidatos.size(); //no: ...elijo el siguiente nodo y lo vuelvo a evaluar
            }
        }
    }
}