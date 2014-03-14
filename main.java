
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class main {

    private static Scanner scanner;

    // Imperial strikes back!!! 
    // Numberphilen innoittamana käytin taulukon selaukseen perustuvaa tapaa :)
    // Vähän vaihtelua perinteiseen solmu-olioiden läpikäymiseen.
    // Ideana on laskea pyramidia alaspäin kumuloituva summa, etsiä lehtitasolta suurin summa ja backtrackata siitä juureen.
    public static void main(String[] args) {
        
        openFile(args[0]);
        
        // sum_list sisältää kumuloituneet summat ja se riittäisi mikäli meitä kiinnostaa ainoastaan suurin summa.
        // original_list sisältää alkuperäiset luvut polun näyttämistä varten
        ArrayList<Integer> sum_list = new ArrayList();
        ArrayList<Integer> original_list = new ArrayList();
        
        // Luetaan tiedosto vanhaan tyyliin numero kerrallaan, tästä käsittelytavasta johtuen poistin yksinkertaistuksen vuoksi tiedoston alusta seed-tiedon.
        while (scanner.hasNext()) {
            int number = scanner.nextInt();
            original_list.add(number);
            sum_list.add(number);
        }

        long startTime = System.nanoTime();
        int level = calculateCumulativeSums(sum_list);
        System.out.println("Kumulatiivisten summien laskemisessa kesti "+ (System.nanoTime() - startTime) + " ns");
        
        startTime = System.nanoTime();
        int max = searchMaxSumFromLeaf(sum_list, level);
        System.out.println("Suurimman summan etsiminen kesti "+ (System.nanoTime() - startTime) + " ns");
        
        System.out.println("Tykkaysten kokonaissumma on " + max);
        
        startTime = System.nanoTime();
        int[] path_list = pathfinder(sum_list, max, level, original_list);
        System.out.println("Reitin etsiminen suurimmasta summasta juureen kesti "+ (System.nanoTime() - startTime) + " ns");
        
        System.out.println(sum_list);
        
        System.out.println("Oikea rivi on");
        for (int i = 1; i < path_list.length; i++) {
            System.out.println(i + ": " + path_list[i]);
        }
    }

    private static int[] pathfinder(ArrayList<Integer> sum_list, int max, int level, ArrayList<Integer> original_list) {
        // Pointteri polun selaamiseen
        int path_pointer = sum_list.indexOf(max);
        
        // Map tallentaa tason suurimman naapurin indeksin ja bootsrappaa alimman levelin pointteri
        HashMap<Integer, Integer> map = new HashMap();
        map.put(level, path_pointer);
        
        // Polku juuresta suurimman summan saamiseksi ja alimman tason alkion lisääminen
        int[] path_list = new int[level+1];
        path_list[level] = original_list.get(path_pointer);
        
        // Asetetaan rajoitukset levelillä pysymiseksi
        int left_boundary = sum_list.size() - 2 * level;
        int right_boundary = sum_list.size() - level;
        
        // Aloitetaan selaaminen ja koska alin taso on jo lisätty voidaan lähteä liikkeelle toiseksi alimmalta tasolta
        level--;
        while (level > 0) {
            
            // Haetaan polkupointterin alkion vanhemmat
            int parent_left = path_pointer - level - 1;
            int parent_right = path_pointer - level;

            // Tarkistetaan että pysytään oikeassa levelissä ja ehtolauseilla lisätään mappiin suurin vanhempi
            if (parent_left > left_boundary && parent_right < right_boundary) {
                if (sum_list.get(parent_left) > sum_list.get(parent_right)) {
                    map.put(level, parent_left);
                } else {
                    map.put(level, parent_right);
                }
            } else if (parent_left <= left_boundary && parent_right < right_boundary) {
                map.put(level, parent_right);
            } else if (parent_left > left_boundary && parent_right >= right_boundary) {
                map.put(level, parent_left);
            }
            
            // Päivitetään polkupointteri suurimpaan ylemmän tason naapuriin
            path_pointer = map.get(level);
            // Lisätään alkuperäisen listan alkio polkulistaan
            path_list[level] = original_list.get(path_pointer);

            // Päivitetään rajat seuraavan tason mukaisiksi ja siirrytään ylemmälle tasolle
            left_boundary = left_boundary - level + 1;
            right_boundary = right_boundary - level;
            level--;
        }
        return path_list;
    }

    // Tiedoston lukeminen niin yksinkertaisesti kuin voidaan. 
    private static void openFile(String filename) {
        try {
            File file = new File(filename);
            scanner = new Scanner(file);
        } catch (Exception e) {
            System.out.println("File was not found!");
            System.exit(0);
        }
    }

    // Käydään taulukko läpi naapuriehtojen avulla ja lasketaan kumuloituvat summat juuresta lehtiin asti.
    // Taulukko ei ehkä ole yksinkertaisin ratkaisu ja se vaatii paljon pointtereiden päivittelyä.
    // Vanhemmat ja lapset voidaan laskea yksinkertaisesti kullekin alkiolle kun tiedetään millä levelillä ollaan.
    // Tässä tapauksessa suoritetaan alkioiden määrän verran laskutoimituksia.
    private static int calculateCumulativeSums(ArrayList<Integer> sum_list) {
        
        // parent osoittaa vanhempaan eli summa-alkioon
        // level kertoo missä korkeudessa puussa ollaan menossa
        // level_end antaa rajoituksen, että pysytään varmasti oikealla tasolla
        // child osoittaa lapseen eli alkioon, johon lisätään vanhemman summa.
        int parent = 0, level = 1, level_end = 1, child = 1;
        
        // Varsinainen lisäys looppi
        while (child < sum_list.size()) {
            // Mikäli ollaan levelin alussa tai lopussa ei ole kuin yksi reitti alaspäin.
            if (child == level_end || child == level_end + level) {
                sum_list.set(child, sum_list.get(parent) + sum_list.get(child));
            } else {
                // Kun ollaan levelin keskivälillä pitää alkaa tarkistamaan kummasta naapurista saadaan suurempi summa
                if (sum_list.get(parent) + sum_list.get(child) > sum_list.get(parent + 1) + sum_list.get(child)) {
                    sum_list.set(child, sum_list.get(parent) + sum_list.get(child));
                } else {
                    sum_list.set(child, sum_list.get(parent + 1) + sum_list.get(child));
                }
                // Vanhemman pointterin päivitys
                parent++;
            }
            // Lapsen pointterin päivitys
            child++;
            // Kun tason loppu saavutetaan siirrytään seuraavalle tasolle ja päivitetään seuraavan tason pointterit oikein.
            if (child > level_end + level) {
                level++;
                parent++;
                level_end = parent + level;
                child = level_end;
            }
        }
        return level;
    }

    // Etsitään alimman eli lehtitason suurin summa
    // Palautetaan suurin summa
    private static int searchMaxSumFromLeaf(ArrayList<Integer> sum_list, int level) {
        // Haetaan alimman tason lehdet.
        List<Integer> leaf_list = sum_list.subList(sum_list.size() - level, sum_list.size());
        int max = 0;
        for (int i = 0; i < leaf_list.size(); i++) {
            if (leaf_list.get(i) > max) {
                max = leaf_list.get(i);
            }
        }
        return max;
    }
}
