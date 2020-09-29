package dm;

import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        SeamCarving s = new SeamCarving();// seam carving

        //vérification de l'extension du fichier
        if(!s.verifExtension(args[0]) || !s.verifExtension(args[1])){
            System.out.println("Le fichier d'entrée ou le fichier sortie ne sont pas des fichier PGM");
            System.exit(0);
        }

        int compteurRepBis = 0; //compteur de répétitions
        int repetitions = Integer.parseInt(args[2]);//nombre de fois que nous voulons appliquer BelmannFord
        int[][] tab = s.readpgm(args[0]); //tab en entrée

        GraphArrayList g = (GraphArrayList) s.tograph(tab); //graphe de l'image en entrée



        /**** Conversion su tableau 2D en ArrayList d'Arraylist ****/

        ArrayList<ArrayList<Integer>> listImage = new ArrayList<>();


        for (int[] ints : tab) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i : ints) {
                list.add(i);
            }
            listImage.add(list);
        }

        /**************************************************************** ***/

        s.Bellman_Ford(g, 0, g.vertices());
        ArrayList<Integer> aSupprimer = s.getListeASupprimer(); //liste des sommets PARENTS du graphe
        int limite = listImage.get(0).size(); //limite de suppression
        int cptPixel = g.vertices() - 1;//compteur de pixel analysé
        boolean end = false;

        while(!end) {
            if (repetitions < limite) {
                while (compteurRepBis < repetitions) {
                    for (int i = listImage.size() - 1; i >= 0; i--) { //on commence à parcourir par la fin
                        for (int j = listImage.get(i).size() - 1; j >= 0; j--) {//on commence à parcourir par la fin
                            --cptPixel;
                            if (aSupprimer.contains(cptPixel)) {
                                listImage.get(i).remove(j);
                                //listImage.get(i).set(j,255); //changement de couleur visualiser le pixel à supprimer
                            }

                        }
                    }

                    int[][] tabIntermediare = new int[listImage.size()][listImage.get(0).size()];
                    for (int l = 0; l < listImage.size(); l++) {
                        for (int k = 0; k < listImage.get(0).size(); k++) {
                            tabIntermediare[l][k] = listImage.get(l).get(k);
                        }
                    }
                    s = new SeamCarving();
                    g = (GraphArrayList) s.tograph(tabIntermediare); //graphe de l'image en entrée
                    /**** Conversion du tableau 2D en ArrayList d'Arraylist ****/
                    listImage = new ArrayList<>();
                    for (int[] ints : tabIntermediare) {
                        ArrayList<Integer> list = new ArrayList<>();
                        for (int i : ints) {
                            list.add(i);
                        }
                        listImage.add(list);
                    }
                    s.Bellman_Ford(g, 0, g.vertices()); //application de Bellman Ford sur le nouveau graphe
                    aSupprimer = s.getListeASupprimer(); //liste des sommets PARENTS du graphe
                    cptPixel = g.vertices() - 1;
                    System.out.println(compteurRepBis);
                    ++compteurRepBis;
                    if(compteurRepBis==repetitions){
                        end=true;
                    }
                }
            } else {
                Scanner sc = new Scanner(System.in);
                System.out.println("Veuillez entrer un nombre de colonnes à supprimer inférieure à la largeur svp :");
                repetitions = sc.nextInt();
            }
        }
        /******************************************************* */
        /************** FORMATION DE LA NOUVELLE IMAGE ********** */

        int[][] tabFinal = new int[listImage.get(0).size()][listImage.size()];
        for (int l = 0; l < listImage.size(); l++) {
            for (int k = 0; k < listImage.get(l).size(); k++) {
                tabFinal[k][l] = listImage.get(l).get(k);
            }
        }
        s.writepgm(tabFinal, args[1]);

        System.out.println("Merci d'avoir utilisé mon logiciel :) ");

    }
}