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
        HashSet<String> states = new HashSet<>(List.of(list.get(1).split(" ")));

        // int nrLitere = Integer.parseInt(list.get(2));
        HashSet<String> letters = new HashSet<>(List.of(list.get(3).split(" ")));

        String initialState = list.get(4);

        // int nrStariFinale = Integer.parseInt(list.get(5));
        HashSet<String> finalStates = new HashSet<>(List.of(list.get(6).split(" ")));

        int nrTransitions = Integer.parseInt(list.get(7));
        int currentLine = 8;

        // I will store the elements in the following form:
        // (currentState, letter) -> nextState
        HashMap<List<String>, String> transitionFunction = new HashMap<>();

        for (int i = 0; i < nrTransitions; i++) {
            String[] transition = list.get(currentLine++).split(" ");
            List<String> temp = new ArrayList<>();
            temp.add(transition[0]);
            temp.add(transition[1]);
            transitionFunction.put(temp, transition[2]);
        }

        Formatter f = new Formatter("src/output.dat");

        int nrWords = Integer.parseInt(list.get(currentLine++));
        for (int t = 0; t < nrWords; ++t) {
            String word = list.get(currentLine++);

            String currentState = initialState;
            boolean valid = true;
            for (Character c : word.toCharArray()) {
                List<String> temp = new ArrayList<>();
                temp.add(currentState);
                temp.add(String.valueOf(c));
                if (!transitionFunction.containsKey(temp) || !letters.contains(String.valueOf(c))) {
                    valid = false;
                    break;
                } else {
                    currentState = transitionFunction.get(temp);
                    if (!states.contains(currentState)) {
                        valid = false;
                        break;
                    }
                }
            }
            f.format(valid && finalStates.contains(currentState) ? "DA\n" : "NU\n");
        }
        f.close();
    }
}