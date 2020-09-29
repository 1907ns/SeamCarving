package dm;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
public class SeamCarving
{
    private ArrayList<Integer> listeASupprimer;

    public SeamCarving () {
        this.listeASupprimer=new ArrayList<>();

    }

    /**
     * Fonction permettant de lire un ficchier PGM et de le retranscrire dans un tableau d'entiers
     * @param fn nom du fichier à lire
     * @return tableau d'entiers 2D représentant les pixels du fichier pgm
     */
   public static int[][] readpgm(String fn)
	 {		
        try {
            //InputStream f = ClassLoader.getSystemClassLoader().getResourceAsStream(fn);
            InputStream f = new FileInputStream(fn); //InputStream modifié pour procéder à la lecture du fichier avec le .jar
            BufferedReader d = new BufferedReader(new InputStreamReader(f));
            String magic = d.readLine();
            String line = d.readLine();
		   while (line.startsWith("#")) {
			  line = d.readLine();
		   }
		   Scanner s = new Scanner(line);
		   int width = s.nextInt();
		   int height = s.nextInt();		   
		   line = d.readLine();
		   s = new Scanner(line);
		   int maxVal = s.nextInt();
		   int[][] im = new int[height][width];
		   s = new Scanner(d);
		   int count = 0;
		   while (count < height*width) {
			  im[count / width][count % width] = s.nextInt();
			  count++;
		   }
		   return im;
        }
		
        catch(Throwable t) {
            t.printStackTrace(System.err) ;
            return null;
        }
    }

    /**
     * Fonction permettant d'écrire un fichier PGM à partir d'un tableau d'entiers
     * @param image tableau d'entiers représentant le fichir à écrire
     * @param filename nom du fichier
     * @throws IOException
     */
    public void writepgm(int [][] image, String filename) throws IOException{
       try{

           File file = new File(filename);
           PrintWriter flot= new PrintWriter(new BufferedWriter(new FileWriter(file)));
           flot.println("P2");
           flot.println("# "+filename);
           flot.println(image.length + " " + image[0].length);
           flot.println(255);
           for(int i=0; i<image[0].length; i++){
               for(int j =0; j<image.length; j++){
                   flot.println(image[j][i]);
               }
           }
           flot.close();

       } catch (IOException e) {
           e.printStackTrace();
       }

    }

    /**
     * Fonction permettant d'obtenir le tableau d'intérêts de l'image
     * @param image tableau de départ
     * @return tableau d'intérêts de l'image
     */
    public  int [][] interest(int[][] image){
       int [][] tmp = new int[image.length][image[0].length];
       for(int i = 0; i<image.length;i++){
           for(int j = 0; j<image[0].length; j++){
               if(j==0){
                   tmp[i][j]=Math.abs(image[i][j]-image[i][j+1]);
               }else if (j==image[0].length-1){
                   tmp[i][j]=Math.abs(image[i][j-1]-image[i][j]);
               } else {
                   int moy =image[i][j+1]+image[i][j-1];
                   tmp[i][j]=Math.abs(image[i][j]-(moy/2));
               }
           }
       }
        return tmp;
    }

    /**
     * Fonction permettant d'obtenir un graphe à partir d'un tableau
     * @param itr tableau à transformer en graphe
     * @return le graph du tableau en paramètres
     */
    public Graph tograph(int[][] itr){
            GraphArrayList g = new GraphArrayList(itr.length * itr[0].length + 2);
            int cpt = 1;
            int[][] tabInterest = this.interest(itr); //transformation du tableau initial en tableau d'intérêts
            int cptLine = 0;
            for(int i = 0; i<itr.length; i++){
                for(int j= 0; j<itr[0].length; j++){
                    if(cptLine==itr.length-1){
                        g.addEdge(new Edge(cpt,g.vertices()-1,tabInterest[i][j]));
                    }else if (cptLine==0){
                        g.addEdge(new Edge(0,cpt,0));
                        if(j==0){
                            g.addEdge(new Edge(cpt,cpt+itr[0].length,tabInterest[i][j]));
                            g.addEdge(new Edge(cpt,cpt+itr[0].length+1,tabInterest[i][j]));
                        }else if(j==itr[0].length-1){
                            g.addEdge(new Edge(cpt,cpt+itr[0].length,tabInterest[i][j]));
                            g.addEdge(new Edge(cpt,cpt+itr[0].length-1,tabInterest[i][j]));
                        }else{
                            g.addEdge(new Edge(cpt,cpt+itr[0].length,tabInterest[i][j]));
                            g.addEdge(new Edge(cpt,cpt+itr[0].length+1,tabInterest[i][j]));
                            g.addEdge(new Edge(cpt,cpt+itr[0].length-1,tabInterest[i][j]));
                        }

                    }else {
                        if(j==0){
                            g.addEdge(new Edge(cpt,cpt+itr[0].length,tabInterest[i][j]));
                            g.addEdge(new Edge(cpt,cpt+itr[0].length+1,tabInterest[i][j]));
                        }else if(j==itr[0].length-1){
                            g.addEdge(new Edge(cpt,cpt+itr[0].length,tabInterest[i][j]));
                            g.addEdge(new Edge(cpt,cpt+itr[0].length-1,tabInterest[i][j]));
                        }else{
                            g.addEdge(new Edge(cpt,cpt+itr[0].length,tabInterest[i][j]));
                            g.addEdge(new Edge(cpt,cpt+itr[0].length+1,tabInterest[i][j]));
                            g.addEdge(new Edge(cpt,cpt+itr[0].length-1,tabInterest[i][j]));
                        }

                    }
                    cpt++;
                }
                cptLine++;
            }
            return g;

    }



    /**
     * Algorithme de Bellman Ford
     * @param g graphe à analyser
     * @param s sommet de départ
     * @param t sommet d'arrivée
     */
    public void Bellman_Ford(Graph g, int s, int t){
        g=(GraphArrayList)g;
        int v = ((GraphArrayList) g).vertices();
        int dist[] = new int[v];
        int distBis[] = new int[v];
        int tabParent[] = new int[v];
        boolean end = false;

        for(int i=0;i<g.vertices();i++){
            tabParent[i]=i;
        }
        //Etape 1, initialisation à l'infini
        for(int i =0; i<v;++i){
            dist[i]= Integer.MAX_VALUE;
        }
        dist[s]=0;
        int u = 0;
        int w = 0;
        int cost = 0;

        int cpt = 1;

        //Etape 2
        while(cpt < v  && !end) {
            for(Edge edge : ((GraphArrayList) g).edges()){

                u = edge.getFrom();
                w = edge.getTo();
                cost = edge.cost;
                if (dist[u] != Integer.MAX_VALUE && dist[u] + cost < dist[w]) {
                    dist[w] = dist[u] + cost;
                    tabParent[w]=u;
                    //end=true;//1 parent par ligne, donc dès que l'on le trouve -> on arrête de chercher
                }

            }
            end=compareTab(dist,distBis);
            copieTab(dist,distBis);
            ++cpt;
        }

        /*** AUCUN PIXEL AVEC UNE VALEUR NEGATIVE? DONC PAS DE CIRCUIT DE POIDS NEGATIF
         * Pas besoin de vérifier cela
         */
        //Etape 3
        /*for(Edge edge : ((GraphArrayList) g).edges()){
             u = edge.getFrom();
             w = edge.getTo();
             cost = edge.cost;
            if (dist[u] != Integer.MAX_VALUE && dist[u] + cost < 0) {
                System.out.println("Le graphe contient un cycle de poids négatif");
                break;
            }
        }*/

        int sommet = g.vertices()-1;

        //récolte des sommets à supprimer
        while(tabParent[sommet]!=0){
            sommet= tabParent[sommet];
            this.listeASupprimer.add(sommet);
        }
    }


    /**
     *  Getter de la liste des pixels à supprimer (identifiés via un leurs positions)
     * @return la liste des pixels à supprimer (identifiés via un leurs positions)
     */
    public ArrayList<Integer> getListeASupprimer() {
        return listeASupprimer;
    }


    /**
     * Fonction qui vérifie si un fichier est un fichier pgm
     * @param nomFichier nom du fichier dont l'extension est à vérifier
     * @return true si le fichier est un fichier pgm, false sinon
     */
    public boolean verifExtension(String nomFichier){
        String extension = nomFichier.substring(nomFichier.length()-4);
        if(extension.equals(".pgm") && nomFichier.length()>4){
            return true;
        }
        return false;
    }

    public boolean compareTab(int tab[], int tabDeux[]){
        for(int i =0;i<tab.length;i++){
            if(tab[i]!=tabDeux[i]){
                return true;
            }
        }
        return false;
    }

    public void copieTab(int tab[], int tabDeux[]){
        for(int i =0;i<tab.length;i++){
           tabDeux[i]=tab[i];
        }

    }

}

