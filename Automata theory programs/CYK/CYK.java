import java.io.File;
import java.util.*;

// https://en.wikipedia.org/wiki/CYK_algorithm

/**
 * S -> AB|CD|a|b
 * A -> DE|a
 * B -> b
 * C -> DE|b
 * D -> c
 * E -> AB
 * Only words generated: a, b, ab, bc, cabb, cabc
 */

class CYK {
    private static boolean cyk(ArrayList<String> grammar, String word) {
        int n = word.length();
        // If I want to have non-terminals with more than one symbol,
        // I use String
        Set<Character> nonterminals = new HashSet<>();
        // Rules are in the form: key -> list of direct non-terminals where it goes
        // For example if I have S -> AB|CD
        // In the map I will have: AB, [S] and CD, [S], where AB and CD are the keys
        // I simulate the backpoint triples vector with this hashmap
        Map<String, List<Character>> rules = new HashMap<>();

        for (String rule : grammar) {
            String[] production = rule.split("->");
            char nonterminal = production[0].trim().charAt(0);
            nonterminals.add(nonterminal);
            // Split by the or symbol
            String[] rightSide = production[1].trim().split("\\|");

            for (String s : rightSide) {
                s = s.trim(); // Remove leading and trailing whitespaces
                // Create a new list if necessary
                rules.putIfAbsent(s, new ArrayList<>());
                // Add the non-terminal
                rules.get(s).add(nonterminal);
            }
        }

        int r = nonterminals.size();
        // dp[i][j][k] - True if the substring from i to j (inclusive) can be generated by the non-terminal k
        boolean[][][] dp = new boolean[n][n][r];
        Map<Character, Integer> nonterminalIndex = new HashMap<>();
        int index = 0;

        // Index from 0 starting with the start symbol
        for (char nh : nonterminals) {
            nonterminalIndex.put(nh, index++);
        }

        // This is the base case, where I set the elements on the main diagonal according to the non-terminal
        // If through the grammar we reach the letter processed in the current step, then set to true
        // During traversal, go back through the grammar from the non-terminal that reaches the letter
        for (int i = 0; i < n; i++) {
            String terminal = String.valueOf(word.charAt(i));
            if (rules.containsKey(terminal)) {
                for (char nonterminal : rules.get(terminal)) {
                    dp[i][i][nonterminalIndex.get(nonterminal)] = true;
                }
            }
        }

        // Association with PODM (https://www.infoarena.ro/problema/podm)
        // Start with substrings of length 2
        for (int length = 2; length <= n; length++) {
            // Check over subintervals [i, j], where i and j will be indices in the vector
            for (int i = 0; i <= n - length; i++) {
                int j = i + length - 1;
                // Check each character in the subinterval
                for (int k = i; k < j; k++) {
                    // Check each production
                    for (String production : rules.keySet()) {
                        if (production.length() == 2) {
                            char firstNonterminal = production.charAt(0);
                            char secondNonterminal = production.charAt(1);
                            // Traverse non-terminals from which we reach the found non-terminal
                            for (char nonterminal : rules.get(production)) {
                                if (dp[i][k][nonterminalIndex.get(firstNonterminal)] && dp[k + 1][j][nonterminalIndex.get(secondNonterminal)]) {
                                    dp[i][j][nonterminalIndex.get(nonterminal)] = true;
                                }
                            }
                        }
                    }
                }
            }
        }

        // The answer is in the top right corner (mixed dynamic programming)
        return dp[0][n - 1][0];
    }

    public static void main(String[] args) {
        try {
            File file = new File("src/gramatica3.txt");
            Scanner scan = new Scanner(file);
            ArrayList<String> grammar = new ArrayList<>();
            while(scan.hasNextLine()) {
                String s = scan.nextLine();
                grammar.add(s);
            }
            scan.close();
            String word = "uupzupiuupp";
            System.out.println(cyk(grammar, word));
        } catch(Exception e) {
            e.printStackTrace(System.err);
        }
    }
}


/*
S -> AD|BF|CD|EF
A -> CS
B -> ES
C -> (
D -> )
E -> [
F -> ]

S -> AT|BT|CF|DF|0|1|GE|HF|IF
T -> CF|DF|0|1|GE|HF|IF
F -> 0|1|GE|HF|IF
A -> TH
B -> TI
C -> FJ
D -> FK
E -> )
G -> LS
H -> +
I -> -
J -> *
K -> /
L -> (

S->ER|0|1|AX
E->AX
R->BR|SX
N->0|1
X->+|-|*|/
A->NS
B->SX

S->XY->BA->CA
X->BA->CA
Y->ED->FD
A->b
B->CX
C->a
D->d
E->FY
F->c
 */