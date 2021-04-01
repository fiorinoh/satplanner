# satplanner
=======
SATPlanner based on PDDL4J and SAT4J.

Compile with:

javac -d classes -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar src/fr/uga/pddl4j/tutorial/satplanner/*.java -Xlint:unchecked

Test with:

java -cp classes:lib/pddl4j-3.8.3.jar:lib/sat4j-sat.jar fr.uga.pddl4j.tutorial.satplanner.SATPlanner -o pddl/logistics.pddl -f pddl/problem.pddl -n 1
