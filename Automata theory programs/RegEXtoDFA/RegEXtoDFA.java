import java.util.*;
import java.util.regex.Pattern;

//((((b*((b|((ba)*|(c|c*)))((c*|(ab))*(b**a))))|(((((c|a)a)(a|d)*)*((d*|c*)b*)*)|b*))|(((b(a|b))**|b)((((d|c)*((dc)|a*))c**)|(((a|d)|(dd))b)*))*)c)*


class Main2 {

    private final static Character lambda = '$';
    private final static String letters = "[a-zA-Z]";

    // FollowPos(i) - positions that can be reached from the node with position i in the AST
    // Determine followPos while generating the AST
    // Use the letter array to associate each leafIndex with a letter
    private static int leaves = 0;
    private static final ArrayList<HashSet<Integer>> followPos = new ArrayList<>();
    private static final ArrayList<Character> letter = new ArrayList<>();
    private static final HashMap<ArrayList<Integer>, Integer> markedStates = new HashMap<>();
    private static Integer currentPosition; // Current position in expression traversal

    // Endmarker - #
    // Any state reached by a transition with EndMarker
    // Will be considered a final state

    public static void main(String[] args) {
        System.out.print("Regex: ");
        Scanner scan = new Scanner(System.in);
        String regexExpr = scan.next();
        scan.close();
        convertToDfa(regexExpr);
    }

    // Algorithm steps:
    // 1) Construct the AST from the augmented expression, denoted T
    // 2) Construct nullable, firstPos, lastPos, and followPos for T
    // 3) Final step, construct the DFA states DStates and Dtran - transitions
    // The states of D are the positions in T
    // Use a map to maintain the state of a state - markedStates
    // A state will be marked before generating transitions to other states
    // The initial state is firstPos(T.root), and the final states are those that contain the endmarker's position in the end

    private static void convertToDfa(String regexExpr) {
        String regex = augmentedRegex(regexExpr);
        currentPosition = 0;
        leaves = 0;
        followPos.clear();
        letter.clear();
        markedStates.clear();
        // Build the syntax tree for the given regex
        AST root = evalExpression(regex);
        // Now generate DStates and DTran
        // For this, simulate a BFS over the generated AST
        // Traverse all DFA states
        // The queue will be initialized with the firstPos of T
        Queue<ArrayList<Integer>> Q = new LinkedList<>();
        Q.add(new ArrayList<>());
        for (Integer position : root.firstPos) {
            Q.peek().add(position);
        }
        // Arrange states based on the letter and leafIndex
        Q.peek().sort(new CustomComparator());
        markedStates.clear();
        int numberOfStates = 0, startState = 1;
        HashSet<String> states = new HashSet<>();
        HashSet<String> finalStates = new HashSet<>();
        HashMap<List<String>, String> transitions = new HashMap<>();
        markedStates.put(Q.peek(), ++numberOfStates);
        states.add("" + numberOfStates);
        // ~Subset construction:
        // For the current state, generate a next state based on followPos
        // and choose the subset with positions having the same letters
        while (!Q.isEmpty()) {
            ArrayList<Integer> currentState = Q.poll();
            int i = 0;
            while (i < currentState.size()) {
                int j = i;
                ArrayList<Integer> next = new ArrayList<>();
                while (j < currentState.size() &&
                      letter.get(currentState.get(i)) == letter.get(currentState.get(j))) {
                    try {
                        next.addAll(followPos.get(currentState.get(j)));
                    } catch (Exception e) {
                        // Nothing
                    }
                    ++j;
                }
                // Ignore states generated by the end marker
                if (letter.get(currentState.get(i)) == '#') {
                    i = j;
                    continue;
                }
                // Remove duplicates
                HashSet<Integer> uniqueFilter = new HashSet<>(next);
                next = new ArrayList<>(uniqueFilter);
                // Sort again by letter, then by leafIndex
                next.sort(new CustomComparator());
                // Before making the transition, mark the state
                if (!markedStates.containsKey(next)) {
                    markedStates.put(next, ++numberOfStates);
                    states.add(String.valueOf(numberOfStates));
                    Q.add(next);
                }
                // Add transitions in the form (currentState, letter) -> nextState
                List<String> temp = new LinkedList<>();
                temp.add(String.valueOf(markedStates.get(currentState)));
                temp.add(String.valueOf(letter.get(currentState.get(i))));
                transitions.put(temp, String.valueOf(markedStates.get(next)));
                i = j;
            }
        }
        // States reached by the endMarker become final states
        for (ArrayList<Integer> key : markedStates.keySet()) {
            for (Integer state : key) {
                if (letter.get(state) == '#') {
                    finalStates.add(String.valueOf(markedStates.get(key)));
                    break;
                }
            }
        }
        printDfa(states, finalStates, String.valueOf(startState), transitions);
    }

    // Check if the symbol is either a letter or lambda
    private static boolean isAccepted(Character c) {
        return Pattern.matches(letters, "" + c) || c == lambda;
    }

    // Augmented regex expression
    // Insert concatenation operator "." where possible
    // And add the endMarker # at the end
    private static String augmentedRegex(String regexExpr) {
        if (regexExpr.isEmpty()) {
            return "";
        }
        StringBuilder ans = new StringBuilder("" + regexExpr.charAt(0));
        for (int i = 1; i < regexExpr.length(); ++i) {
            if ((isAccepted(regexExpr.charAt(i - 1)) && isAccepted(regexExpr.charAt(i))) ||
               (isAccepted(regexExpr.charAt(i - 1)) && regexExpr.charAt(i) == '(') ||
               (regexExpr.charAt(i - 1) == ')' && isAccepted(regexExpr.charAt(i))) ||
               (regexExpr.charAt(i - 1) == '*' && isAccepted(regexExpr.charAt(i))) ||
               (regexExpr.charAt(i - 1) == ')' && regexExpr.charAt(i) == '(') ||
               (regexExpr.charAt(i - 1) == '*' && regexExpr.charAt(i) == '(')) {
                ans.append(".");
            }
            ans.append(regexExpr.charAt(i));
        }
        ans = new StringBuilder("(" + ans + ").#");
        return ans.toString();
    }

    // Adaptation based on https://mariusbancila.ro/blog/2009/02/06/evaluate-expressions-%E2%80%93-part-4-evaluate-the-abstract-syntax-tree/
    // Adapted to regex operations
    // Operation order: |, ., *
    // First evaluate an expression, then concatenation, then kleene star
    private static AST evalExpression(String regexExpr) {
        AST root = new AST();

        AST son = evalTerm(regexExpr);
        root.firstPos = son.firstPos;
        root.lastPos = son.lastPos;
        root.sons.add(son);
        root.nullable = son.nullable;

        // Evaluate the next term and check if it reached a new subexpression
        while (currentPosition < regexExpr.length() && regexExpr.charAt(currentPosition) == '|') {
            ++currentPosition;
            son = evalTerm(regexExpr);
            root.sons.add(son);
            root.nullable = (root.nullable || son.nullable);
            root.firstPos.addAll(son.firstPos);
            root.lastPos.addAll(son.lastPos);
        }

        root.resetAST();
        return root;
    }

    // To generate followPos, we have 2 rules:
    // 1) If a node "." in the AST at position i, and position i is in lastPos(c1),
    // then all positions in firstPos(c2) will also be in followPos(i)
    // 2) If a node N "*" at position i, and i is in lastPos(N),
    // then all elements in firstPos(N) will be in followPos(i);
    private static AST evalTerm(String regexExpr) {
        AST root = new AST();

        AST son = evalStar(regexExpr);
        root.firstPos = son.firstPos;
        root.lastPos = son.lastPos;
        root.sons.add(son);
        root.nullable = son.nullable;

        while (currentPosition < regexExpr.length() && regexExpr.charAt(currentPosition) == '.') {
            ++currentPosition;
            son = evalStar(regexExpr);
            root.sons.add(son);

            // Apply rule 1
            for (Integer first : root.lastPos) {
                for (Integer second : son.firstPos) {
                    if (followPos.get(first) != null) {
                        followPos.get(first).add(second);
                    } else {
                        HashSet<Integer> temp = new HashSet<>();
                        temp.add(second);
                        followPos.set(first, temp);
                    }
                }
            }

            if (root.nullable) {
                root.firstPos.addAll(son.firstPos);
            }
            if (son.nullable) {
                root.lastPos.addAll(son.lastPos);
            } else {
                root.lastPos = son.lastPos;
            }

            root.nullable = (root.nullable && son.nullable);
        }

        root.resetAST();
        return root;
    }

    private static AST evalStar(String regexExpr) {
        AST root = new AST();

        if (regexExpr.charAt(currentPosition) == '(') {
            ++currentPosition;
            AST son = evalExpression(regexExpr);
            root.firstPos = son.firstPos;
            root.lastPos = son.lastPos;
            root.sons.add(son);
            root.nullable = son.nullable;
            root.resetAST();
        } else {
            // If it is the lambda leaf
            if (regexExpr.charAt(currentPosition) == lambda) {
                root.nullable = true;
            // If it is a character other than letters and endMarker
            } else if (!isAccepted(regexExpr.charAt(currentPosition)) &&
                    regexExpr.charAt(currentPosition) != '#') {
                root.nullable = true;
                --currentPosition;
            // If it is a leaf of a character
            } else {
                ++leaves;
                followPos.ensureCapacity(leaves + 1);
                while (followPos.size() < leaves + 1) {
                    followPos.add(null);
                }
                letter.ensureCapacity(leaves + 1);
                while (letter.size() < leaves + 1) {
                    letter.add(null);
                }
                root.nullable = false;
                root.firstPos = new HashSet<>();
                root.firstPos.add(leaves);
                root.lastPos = new HashSet<>();
                root.lastPos.add(leaves);
                root.leafIndex = leaves;
                letter.set(leaves, regexExpr.charAt(currentPosition));
            }
        }

        if (++currentPosition < regexExpr.length() && regexExpr.charAt(currentPosition) == '*') {
            root.nullable = true;
            // Apply rule 2
            for (Integer first : root.lastPos) {
                for (Integer second : root.firstPos) {
                    if (followPos.get(first) != null) {
                        followPos.get(first).add(second);
                    } else {
                        HashSet<Integer> temp = new HashSet<>();
                        temp.add(second);
                        followPos.set(first, temp);
                    }
                }
            }
        }

        while (currentPosition < regexExpr.length() && regexExpr.charAt(currentPosition) == '*') {
            ++currentPosition;
        }

        return root;
    }

    private static void printDfa(Set<String> states, Set<String> finalStates, String startState, HashMap<List<String>, String> transitions) {
        // Number of states and states
        System.out.print(states.size() + "\n");
        for (String node : states) {
            System.out.print(node + " ");
        }
        System.out.print("\n");
        // Letters in the alphabet
        Set<String> letters = new HashSet<>();
        for (List<String> key : transitions.keySet()) {
            letters.add(key.get(1));
        }
        System.out.print(letters.size() + "\n");
        for (String letter : letters) {
            System.out.print(letter + " ");
        }
        // Initial state
        System.out.print("\n" + startState + "\n");
        // Final states
        System.out.print(finalStates.size() + "\n");
        for (String finalState : finalStates) {
            System.out.print(finalState + " ");
        }
        // Transitions
        System.out.print("\n" + transitions.size() + "\n");
        for (List<String> key : transitions.keySet()) {
            System.out.print(key.get(0) + " " + key.get(1) + " " + transitions.get(key) + "\n");
        }
    }

    // https://en.wikipedia.org/wiki/Abstract_syntax_tree
    static class AST {
        public int leafIndex;
        // nullable(n) - True if the language of the expression in the subtree with root accepts lambda
        public boolean nullable;
        // To compute the expression of the subtree
        public ArrayList<AST> sons;
        // firstPos(n) - Positions in the subtree of n that correspond to the first letter
        // of at least one word in the language
        // lastPos(n) - Similarly, but must correspond to the last letter
        public Set<Integer> firstPos, lastPos;
        public AST() {
            sons = new ArrayList<>();
            leafIndex = 0;
            nullable = false;
            firstPos = new HashSet<>();
            lastPos = new HashSet<>();
        }
        /** Clear all children of the current node */
        public void resetAST() {
            sons.clear();
        }
    }

    static class CustomComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            int a = (Integer) o1;
            int b = (Integer) o2;
            if (letter.get(a) == letter.get(b)) {
                return Integer.compare(a, b);
            }
            return Integer.compare(letter.get(a), letter.get(b));
        }
    }
}