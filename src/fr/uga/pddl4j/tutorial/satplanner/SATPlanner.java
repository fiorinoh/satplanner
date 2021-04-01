package fr.uga.pddl4j.tutorial.satplanner;

import fr.uga.pddl4j.encoding.CodedProblem;
import fr.uga.pddl4j.parser.ErrorManager;
import fr.uga.pddl4j.planners.Planner;
import fr.uga.pddl4j.planners.ProblemFactory;
import fr.uga.pddl4j.planners.statespace.AbstractStateSpacePlanner;
import fr.uga.pddl4j.planners.statespace.StateSpacePlanner;
import fr.uga.pddl4j.util.BitState;
import fr.uga.pddl4j.util.Plan;
import fr.uga.pddl4j.util.SequentialPlan;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;
import org.sat4j.tools.ModelIterator;

/**
 * This class implements a simple SAT planner based on SAT4J.
 *
 * @author H. Fiorino
 * @version 1.0 - 29.03.2021
 */
public final class SATPlanner extends AbstractStateSpacePlanner {

    /*
     * The arguments of the planner.
     */
    private Properties arguments;


    /**
     * Creates a new SAT planner with the default parameters.
     *
     * @param arguments the arguments of the planner.
     */
    public SATPlanner(final Properties arguments) {
        super();
        this.arguments = arguments;
    }

    /**
     * Solves the planning problem and returns the first solution found.
     *
     * @param problem the problem to be solved.
     * @return a solution search or null if it does not exist.
     */
    @Override
    public Plan search(final CodedProblem problem) {
        // The solution plan is sequential
        final Plan plan = new SequentialPlan();
        // We get the initial state from the planning problem
        final BitState init = new BitState(problem.getInit());
        // We get the goal from the planning problem
        final BitState goal = new BitState(problem.getGoal());
        // Nothing to do, goal is already satisfied by the initial state
        if (init.satisfy(problem.getGoal())) {
            return plan;
        }
        // Otherwise, we start the search
        else {

            // SAT solver timeout
            final int timeout = ((int) this.arguments.get(Planner.TIMEOUT));
            // SAT solver max number of var
            final int MAXVAR = 1000000;
            // SAT solver max number of clauses
            final int NBCLAUSES = 500000;

            ISolver solver = SolverFactory.newDefault();
            solver.setTimeout(timeout);
            ModelIterator mi = new ModelIterator(solver);

            // Prepare the solver to accept MAXVAR variables. MANDATORY for MAXSAT solving
            solver.newVar(MAXVAR);
            solver.setExpectedNumberOfClauses(NBCLAUSES);

            // SAT Encoding starts here!
            //final int steps = (int) arguments.get("steps");

            // Feed the solver using Dimacs format, using arrays of int
            for (int i=0; i < NBCLAUSES; i++) {
                // the clause should not contain a 0, only integer (positive or negative)
                // with absolute values less or equal to MAXVAR
                // e.g. int [] clause = {1, -3, 7}; is fine
                // while int [] clause = {1, -3, 7, 0}; is not fine
                int [] clause = {};
                try {
                    solver.addClause(new VecInt(clause)); // adapt Array to IVecInt
                } catch (ContradictionException e){
                    System.out.println("SAT encoding failure!");
                    System.exit(0);
                }
            }

            // We are done. Working now on the IProblem interface
            IProblem ip = solver;
            try {
                if (ip.isSatisfiable()) {
                } else {
                }
            } catch (TimeoutException e){
                System.out.println("Timeout! No solution found!");
                System.exit(0);
            }


            // Finally, we return the solution plan or null otherwise
            return plan;
        }
    }

    /**
     * Print the usage of the SAT planner.
     */
    private static void printUsage() {
        final StringBuilder strb = new StringBuilder();
        strb.append("\nusage of PDDL4J:\n")
                .append("OPTIONS   DESCRIPTIONS\n")
                .append("-o <str>    operator file name\n")
                .append("-f <str>    fact file name\n")
                .append("-t <num>    SAT solver timeout in seconds\n")
                .append("-n <num>    Max number of steps\n")
                .append("-h          print this message\n\n");
        Planner.getLogger().trace(strb.toString());
    }

    /**
     * Parse the command line and return the planner's arguments.
     *
     * @param args the command line.
     * @return the planner arguments or null if an invalid argument is encountered.
     */
    private static Properties parseCommandLine(String[] args) {

        // Get the default arguments from the super class
        final Properties arguments = StateSpacePlanner.getDefaultArguments();

        // Parse the command line and update the default argument value
        for (int i = 0; i < args.length; i += 2) {
            if ("-o".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
                if (!new File(args[i + 1]).exists()) return null;
                arguments.put(Planner.DOMAIN, new File(args[i + 1]));
            } else if ("-f".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
                if (!new File(args[i + 1]).exists()) return null;
                arguments.put(Planner.PROBLEM, new File(args[i + 1]));
            } else if ("-t".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
                final int timeout = Integer.parseInt(args[i + 1]);
                if (timeout < 0) return null;
                arguments.put(Planner.TIMEOUT, timeout);
            } else if ("-n".equalsIgnoreCase(args[i]) && ((i + 1) < args.length)) {
                final int steps = Integer.parseInt(args[i + 1]);
                if (steps > 0)
                    arguments.put("steps", steps);
                else
                    return null;
            } else {
                return null;
            }
        }
        // Return null if the domain or the problem was not specified
        return (arguments.get(Planner.DOMAIN) == null
                || arguments.get(Planner.PROBLEM) == null) ? null : arguments;
    }

    /**
     * The main method of the <code>SATPlanner</code> example. The command line syntax is as
     * follow:
     * <p>
     * <pre>
     * usage of SATPlanner:
     *
     * OPTIONS   DESCRIPTIONS
     *
     * -o <i>str</i>   operator file name
     * -f <i>str</i>   fact file name
     * -t <i>num</i>   specifies the maximum CPU-time in seconds
     * -n <i>num</i>   specifies the maximum number of steps
     * -h              print this message
     *
     * </pre>
     * </p>
     *
     * @param args the arguments of the command line.
     */
    public static void main(String[] args) {
        final Properties arguments = SATPlanner.parseCommandLine(args);
        if (arguments == null) {
            SATPlanner.printUsage();
            System.exit(0);
        }

        final SATPlanner planner = new SATPlanner(arguments);
        final ProblemFactory factory = ProblemFactory.getInstance();

        File domain = (File) arguments.get(Planner.DOMAIN);
        File problem = (File) arguments.get(Planner.PROBLEM);
        ErrorManager errorManager = null;
        try {
            errorManager = factory.parse(domain, problem);
        } catch (IOException e) {
            Planner.getLogger().trace("\nUnexpected error when parsing the PDDL files.");
            System.exit(0);
        }

        if (!errorManager.isEmpty()) {
            errorManager.printAll();
            System.exit(0);
        } else {
            Planner.getLogger().trace("\nParsing domain file: successfully done");
            Planner.getLogger().trace("\nParsing problem file: successfully done\n");
        }

        final CodedProblem pb = factory.encode();
        Planner.getLogger().trace("\nGrounding: successfully done ("
                + pb.getOperators().size() + " ops, "
                + pb.getRelevantFacts().size() + " facts)\n");

        if (!pb.isSolvable()) {
            Planner.getLogger().trace(String.format("Goal can be simplified to FALSE."
                    +  "No search will solve it%n%n"));
            System.exit(0);
        }

        final Plan plan = planner.search(pb);

    }
}