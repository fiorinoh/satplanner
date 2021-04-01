package fr.uga.pddl4j.tutorial.satplanner;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.util.BitOp;
import fr.uga.pddl4j.util.BitState;
import fr.uga.pddl4j.util.BitVector;

import java.util.List;

/**
 * This class implements a planning problem/domain encoding into DIMACS
 *
 * @author H. Fiorino
 * @version 1.0 - 30.03.2021
 */
public final class SATEncoding {
    /*
     * A SAT problem in dimacs format is a list of int list a.k.a clauses
     */
    private List dimacs;

    /*
     * Current number of steps of the SAT encoding
     */
    private int steps;

    /**
     *
     * @param
     */
    public SATEncoding(final CodedProblem problem, final int steps) {
        super();
        this.steps = steps;
        // We get the initial state from the planning problem
        final BitState init = new BitState(problem.getInit());

        // Encoding of init
        // Each fact is a unit clause

        // We get the goal from the planning problem
        final BitState goal = new BitState(problem.getGoal());

        // We get the operators of the problem
        for (int i = 0; i < problem.getOperators().size(); i++) {
            final BitOp a = problem.getOperators().get(i);
            final BitVector precond = a.getPreconditions().getPositive();
            final BitVector positive = a.getUnconditionalEffects().getPositive();
            final BitVector negative = a.getUnconditionalEffects().getNegative();
        }
    }

    /*
     * SAT encoding for next step
     */
    public List next() {
        return null;
    }

    private static int pair(int a, int b) {
        return 0;
    }

    private static int[] unpair(int c) {
        return null;
    }
}