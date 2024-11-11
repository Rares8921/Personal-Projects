import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

class Main {

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
        // HashSet<String> states = new HashSet<>(List.of(list.get(1).split(" ")));

        // int nrLitere = Integer.parseInt(list.get(2));
        // HashSet<String> letters = new HashSet<>(List.of(list.get(3).split(" ")));

        String initialState = list.get(4);

        // int nrStariFinale = Integer.parseInt(list.get(5));
        HashSet<String> finalStates = new HashSet<>(List.of(list.get(6).split(" ")));

        int nrTransitions = Integer.parseInt(list.get(7));
        int currentLine = 8;

        // Elements will be stored in the form:
        // (currentState, letter) -> List(nextState)
        HashMap<List<String>, List<String>> transitionFunction = new HashMap<>();

        for (int i = 0; i < nrTransitions; i++) {
            String[] transition = list.get(currentLine++).split(" ");

            List<String> temp = new ArrayList<>();
            temp.add(transition[0]);
            temp.add(transition[1]);

            List<String> nextStates = transitionFunction.getOrDefault(temp, new ArrayList<>());
            nextStates.add(transition[2]);
            transitionFunction.put(temp, nextStates);
        }

        Formatter f = new Formatter("src/output.dat");

        int nrWords = Integer.parseInt(list.get(currentLine++));
        for (int t = 0; t < nrWords; ++t) {
            String word = list.get(currentLine++);

            // Simulate L-NFA traversal
            // Store nodes where each path can reach
            // Finally check if at least one of these is a final state
            // In addition to simulating an NFA,
            // For each state, continue with a lambda transition without moving to the next character
            // If this state was specified in the input

            HashSet<String> currentStates = new HashSet<>(lambdaTransition(initialState, transitionFunction));

            for (Character c : word.toCharArray()) {
                HashSet<String> nextStates = new HashSet<>();
                for (String currentState : currentStates) {
                    List<String> temp = new ArrayList<>();
                    temp.add(currentState);
                    temp.add(String.valueOf(c));
                    if (transitionFunction.containsKey(temp)) {
                        nextStates.addAll(transitionFunction.get(temp));
                        // Check for each state if it contains a lambda transition
                        // And traverse the entire path of lambda transitions afterward
                        // To leave only the letters from the word to be checked
                        // Like in the simulation of an NFA
                        for (String nextState : transitionFunction.get(temp)) {
                            nextStates.addAll(lambdaTransition(nextState, transitionFunction));
                        }
                    }
                }
                currentStates = nextStates;
            }

            // Check if at least one final state is reached through a path
            boolean validFinalState = false;
            for (String currentState : currentStates) {
                if (finalStates.contains(currentState)) {
                    validFinalState = true;
                    break;
                }
            }
            f.format(validFinalState ? "DA\n" : "NU\n");
        }
        f.close();
    }

    public static HashSet<String> lambdaTransition(String state, HashMap<List<String>, List<String>> transitionFunction) {
        // lambdaTransition - all states reachable using a lambda transition
        HashSet<String> lambdaTransition = new HashSet<>();
        lambdaTransition.add(state);
        // The following search will check states that can be reached
        // From the starting state by traversing all possible lambda transitions
        // To make the checks above resemble an NFA simulator
        LinkedList<String> currentStateTransition = new LinkedList<>();
        currentStateTransition.add(state);
        while (!currentStateTransition.isEmpty()) {
            // Get the current state
            String currentState = currentStateTransition.poll();
            // Create the pair for checking
            List<String> transition = new ArrayList<>();
            transition.add(currentState);
            transition.add(".");
            // If this pair is found
            if (transitionFunction.containsKey(transition)) {
                // Now continue the search, check if the path continues with a lambda transition
                for (String nextState : transitionFunction.get(transition)) {
                    // Check not to create a cycle
                    if (!lambdaTransition.contains(nextState)) {
                        lambdaTransition.add(nextState);
                        currentStateTransition.add(nextState);
                    }
                }
            }
        }
        return lambdaTransition;
    }
}