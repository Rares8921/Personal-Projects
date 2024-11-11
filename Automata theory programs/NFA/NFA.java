import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        // Reading
        String fileName = "src/input.dat";
        List<String> list = new ArrayList<>();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            list = br.lines().collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }

        // Processing
        // int nrStari = Integer.parseInt(list.get(0));
        // HashSet<String> stari = new HashSet<>(List.of(list.get(1).split(" ")));

        // int nrLitere = Integer.parseInt(list.get(2));
        // HashSet<String> litere = new HashSet<>(List.of(list.get(3).split(" ")));

        String stareInitiala = list.get(4);

        // int nrStariFinale = Integer.parseInt(list.get(5));
        HashSet<String> stariFinale = new HashSet<>(List.of(list.get(6).split(" ")));

        int nrTranzitii = Integer.parseInt(list.get(7));
        int linieCurenta = 8;

        // Elements will be stored in the form:
        // (currentState, letter) -> List(nextStates)
        HashMap<List<String>, List<String>> functieDeTranzitie = new HashMap<>();

        for(int i = 0; i < nrTranzitii; i++) {
            String[] tranzitie = list.get(linieCurenta++).split(" ");
            List<String> temp = new ArrayList<>();
            temp.add(tranzitie[0]);
            temp.add(tranzitie[1]);
            List<String> stariUrmatoare = functieDeTranzitie.getOrDefault(temp, new ArrayList<>());
            stariUrmatoare.add(tranzitie[2]);
            functieDeTranzitie.put(temp, stariUrmatoare);
        }

        Formatter f = new Formatter("src/output.dat");

        int nrCuvinte = Integer.parseInt(list.get(linieCurenta++));
        for(int t = 0; t < nrCuvinte; ++t) {
            String cuvant = list.get(linieCurenta++);

            // Simulate NFA
            HashSet<String> stariCurente = new HashSet<>();
            stariCurente.add(stareInitiala);

            for(Character c : cuvant.toCharArray()) {
                HashSet<String> stariUrmatoare = new HashSet<>();
                for(String stareCurenta : stariCurente) {
                    List<String> temp = new ArrayList<>();
                    temp.add(stareCurenta);
                    temp.add(String.valueOf(c));
                    if(functieDeTranzitie.containsKey(temp)) {
                        stariUrmatoare.addAll(functieDeTranzitie.get(temp));
                    }
                }
                stariCurente = stariUrmatoare;
            }

            // Check if any final state is reached
            boolean valid = false;
            for(String stareCurenta : stariCurente) {
                if(stariFinale.contains(stareCurenta)) {
                    valid = true;
                    break;
                }
            }
            f.format(valid ? "DA\n" : "NU\n");
        }
        f.close();
    }
}