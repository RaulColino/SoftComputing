import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        //Windows example path
        final File folder = new File("C:\\Users\\YourUser\\Desktop\\SoftComputing\\instancias");

        try {
            //fase de creacion de datos para cada algoritmo
            PrintWriter printWriterAleatorio = new PrintWriter("results_algoritmo_constructivo_aleatorio.csv");
            PrintWriter printWriterGRASP = new PrintWriter("results_algoritmo_metaheuristico_grasp.csv");
            PrintWriter printWriterVNS = new PrintWriter("results_algoritmo_metaheuristico_vns.csv");

            printWriterAleatorio.println("numero_instancia, valor_FO, tiempo_de_ejecucion_(s)");
            printWriterGRASP.println("valor_FO, tiempo_de_ejecucion_(s)");
            printWriterVNS.println("valor_FO, tiempo_de_ejecucion_(s)");

            int nInstancia = 1;
            for (final File ficheroInstancia : folder.listFiles()) {

                //if (nInstancia>2) break; //esta linea nos permite probar con las primeras x instancias
                if (ficheroInstancia.isDirectory()) {
                    System.out.println("Error el archivo leido es un directorio, no un archivo de texto!");
                } else {
                    aplicarAlgoritmoConstructivoAleatorio(ficheroInstancia, printWriterAleatorio, nInstancia, 59);
                    aplicarAlgoritmoMetaheuristicoGRASP(ficheroInstancia, printWriterGRASP, nInstancia, 0.2, 58);
                    aplicarAlgoritmoMetaheuristicoBVNS(ficheroInstancia, printWriterVNS, nInstancia,8, 58);
                    nInstancia++;
                }
            }

            printWriterAleatorio.close();
            printWriterGRASP.close();
            printWriterVNS.close();


            //fase de creacion del csv final con todos los algoritmos
            PrintWriter pw = new PrintWriter("results.csv");
            pw.println("Instancia, F.O. Aleatorio, Time Aleatorio (s), F.O. GRASP, Time GRASP (s), F.O. VNS, Time VNS (s)"); //cabecera del csv

            Scanner scAleatorio = new Scanner(new File("results_algoritmo_constructivo_aleatorio.csv"));
            Scanner scGRASP = new Scanner(new File("results_algoritmo_metaheuristico_grasp.csv"));
            Scanner scVNS = new Scanner(new File("results_algoritmo_metaheuristico_vns.csv"));

            String firstLineA = scAleatorio.nextLine(); //la cabecera (primera linea) del fichero de cada algoritmo no hay que incluirla
            //System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+firstLineA);
            String firstLineG = scGRASP.nextLine();
            //System.out.println("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb"+firstLineG);
            String firstLineV = scVNS.nextLine();
            //System.out.println("ccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc"+firstLineV);

            while (scAleatorio.hasNextLine()) { //reunimos todos los datos de todos lo ficheros en uno solo
                String lineAleatorio = scAleatorio.nextLine();
                pw.print(lineAleatorio);
                String lineGRASP = scGRASP.nextLine();
                pw.print(", "+lineGRASP);
                String lineVNS = scVNS.nextLine();
                pw.println(", "+lineVNS);
            }

            pw.close();

        } catch(Exception e) {
            e.getStackTrace();
        }
    }

    //Algoritmo aleatorio baseline
    private static void aplicarAlgoritmoConstructivoAleatorio(File ficheroInstancia, PrintWriter pw, int nInstancia, long tiempoMaxDeEjecucionEnSegundos) {
        System.out.println("--aplicarAlgoritmoConstructivoAleatorio--");
        Instancia instancia = getInstanciaFromFile(ficheroInstancia);
        long tStart = System.nanoTime();

        //algoritmo
        Solucion bestSol = new SolucionMDP();
        while (System.nanoTime() - tStart < TimeUnit.SECONDS.toNanos(tiempoMaxDeEjecucionEnSegundos)) { //ejec durante 60 seg

            Solucion nuevaSol = new SolucionMDP();
            Constructivo.constructivoAleatorio(instancia, nuevaSol); //nueva sol randomizada
            if (nuevaSol.evalSol(instancia) > bestSol.evalSol(instancia)) { //si la nueva solucion es mejor la guardamos
                bestSol = nuevaSol;
            }
        }

        long tEnd = System.nanoTime();
        long elapsedTimeSecs = TimeUnit.NANOSECONDS.toSeconds(tEnd - tStart);

        pw.println(nInstancia+","+bestSol.evalSol(instancia)+","+elapsedTimeSecs);

        System.out.println("Instancia "+nInstancia);
        System.out.println("Elapsed time: "+elapsedTimeSecs+" s");
        System.out.println("Mejor sol encontrada: "+bestSol.solu);
        System.out.println("Valor FO: "+bestSol.evalSol(instancia));
    }

    //GRASP (algoritmo metaheuristico trayectorial basado en busqueda multi-arranque)
    private static void aplicarAlgoritmoMetaheuristicoGRASP(File ficheroInstancia, PrintWriter pw, int nInstancia, double alpha, long tiempoMaxDeEjecucionEnSegundos) {
        System.out.println("--aplicarAlgoritmoMetaheuristicoGrasp--");
        Instancia instancia = getInstanciaFromFile(ficheroInstancia);
        long tStart = System.nanoTime();

        //algoritmo
        Solucion bestSol = new SolucionMDP();
        while (System.nanoTime() - tStart < TimeUnit.SECONDS.toNanos(tiempoMaxDeEjecucionEnSegundos)) { //(seg to nanos) ejec durante tiempoMaxDeEjecucionEnSegundos seg

            //construccion aleatorizada miope
            Solucion nuevaSol = new SolucionMDP();
            Constructivo.constructivoAleatorizadoMiope(instancia, nuevaSol, alpha); //nueva sol randomizada //nuevaSol pasada por referencia y se modifica en el metodo

            //mejorar solucion
            MetodosDeMejora.busquedaLocalConSwapYFirstImprovement(nuevaSol, instancia, tStart, tiempoMaxDeEjecucionEnSegundos);

            //actualizar solucion
            if (nuevaSol.evalSol(instancia) > bestSol.evalSol(instancia)) { //si la nueva solucion es mejor la guardamos
                bestSol = nuevaSol;
            }
        }

        long tEnd = System.nanoTime();
        long elapsedTimeSecs = TimeUnit.NANOSECONDS.toSeconds(tEnd - tStart);

        pw.println(bestSol.evalSol(instancia)+","+elapsedTimeSecs);

        System.out.println("Instancia "+nInstancia);
        System.out.println("Elapsed time: "+elapsedTimeSecs+" s");
        System.out.println("Mejor sol encontrada: "+bestSol.solu);
        System.out.println("Valor FO: "+bestSol.evalSol(instancia));
    }


    //Variable Neighborhood Change (algoritmo metaheuristico trayectorial basado en busqueda local por entornos)
    private static void aplicarAlgoritmoMetaheuristicoBVNS(File ficheroInstancia, PrintWriter pw, int nInstancia, int kmax, long tiempoMaxDeEjecucionEnSegundos) {

        System.out.println("--aplicarAlgoritmoMetaheuristicoVNS--");
        Instancia instancia = getInstanciaFromFile(ficheroInstancia);
        long tStart = System.nanoTime();

        Solucion sol = new SolucionMDP();
        Constructivo.constructivoAleatorio(instancia, sol);
        int k;

        //el algoritmo VNS se para cuando k llega a kmax y se acaba el tiempo
        do {
            k = 1;
            do {
                //solNueva = shake(sol,k);
                //System.out.println("k: "+k);
                Solucion solNueva = sol.copy();
                //System.out.println("solNuevaAntesShake: "+solNueva.solu+" FO: "+solNueva.evalSol(instancia));
                Constructivo.shake(solNueva, k, instancia);
                //System.out.println("solNuevaDespuesShake:"+solNueva.solu+" FO: "+solNueva.evalSol(instancia));

                //solNueva' = localSearch(solNueva);
                MetodosDeMejora.busquedaLocalConSwapYFirstImprovement(solNueva, instancia, tStart, tiempoMaxDeEjecucionEnSegundos);
                //System.out.println("solNuevaDespuesDeLocalSearch:"+solNueva.solu+" FO: "+solNueva.evalSol(instancia));

                //k = neighborhoodChange(sol,solNueva) => if(solNueva mejorque sol){sol = solNueva; k=1;}else{k++}
                k = neighborhoodChange(sol, solNueva, k, instancia); //if(k=1)entonces hemos mejorado->sol = solNueva;
                if(k==1) { //si solNueva mejora a sol entonces k=1 despues de neighborhoodChange()
                    sol = solNueva; //System.out.println("sol mejorada por solNueva, sol actualizada a: "+sol.solu+", FO: "+sol.evalSol(instancia));
                }
                //System.out.println("sol es a: "+sol.solu+", FO: "+sol.evalSol(instancia)+"k ha sido asignado con un nuevo valor:"+k);

            } while (k < kmax);
        } while (System.nanoTime() - tStart < TimeUnit.SECONDS.toNanos(tiempoMaxDeEjecucionEnSegundos));

        long tEnd = System.nanoTime();
        long elapsedTimeSecs = TimeUnit.NANOSECONDS.toSeconds(tEnd - tStart);

        pw.println(sol.evalSol(instancia)+","+elapsedTimeSecs);

        System.out.println("Instancia "+nInstancia);
        System.out.println("Elapsed time: "+elapsedTimeSecs+" s");
        System.out.println("Mejor sol encontrada: "+sol.solu);
        System.out.println("Valor FO: "+sol.evalSol(instancia));
    }

    private static int neighborhoodChange(Solucion sol, Solucion solNueva, int k, Instancia instancia) {
        if(solNueva.evalSol(instancia) > sol.evalSol(instancia)){
            //Si se ha producido una mejora, se actualiza la mejor solución hasta el momento...
            //sol = solNueva; esto no funciona y no se por que. Mejor actualizar la sol fuera de esta funcion.
            //System.out.println("sol mejorada por solNueva, sol antigua: "+sol.solu+", FO: "+sol.evalSol(instancia));
            //...y se vuelve a la primera vecindad.
            return 1;
        }else{
            //aumentamos el tamaño de la vecindad (aumentamos tamaño de busqueda para encontrar nuevas sol)
            //System.out.println("sol NO mejorada por solNueva");
            return k+1;
        }
    }


    //Devuelve un objeto Instancia a partir de los datos de un archivo con los datos de la instancia
    private static Instancia getInstanciaFromFile(File file) {
        try {
            Scanner sc = new Scanner(file);

            //leer numero de nodos requeridos para la solucion y numero de nodos totales
            String firstLine = sc.nextLine();
            String [] firstLineValues = firstLine.split(" ");
            int nNodes = Integer.parseInt(firstLineValues[0]), nNodesReq = Integer.parseInt(firstLineValues[1]);
            System.out.println("numero de nodos totales: "+nNodes+", nodos a elegir: "+nNodesReq);

            //generar matriz de distancias
            double[][] distanceMatrix = new double[nNodes][nNodes];
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String [] splitted = line.split(" ");
                int n1 = Integer.parseInt(splitted[0]); int n2 = Integer.parseInt(splitted[1]); double dist = Double.parseDouble(splitted[2]);
                //System.out.println(file.getName()+" : nodo "+n1+" - nodo "+n2+" , distancia: "+dist);
                distanceMatrix[n1][n2] = dist;
                distanceMatrix[n2][n1] = dist;
            }

            return new Instancia(nNodesReq, nNodes, distanceMatrix);

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}


