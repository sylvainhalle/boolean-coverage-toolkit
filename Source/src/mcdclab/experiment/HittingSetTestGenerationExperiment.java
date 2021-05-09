/*
    Graph-based MC/DC testing
    Copyright (C) 2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mcdclab.experiment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mcdc.CategoryCoverage;
import ca.uqac.lif.mcdc.HittingSetRunner;
import ca.uqac.lif.mcdc.HologramNode;
import ca.uqac.lif.mcdc.Hypergraph;
import ca.uqac.lif.mcdc.HypergraphGenerator;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Truncation;
import ca.uqac.lif.mcdc.Valuation;
import clojure.lang.PersistentHashSet;

/**
 * Generates a test suite using tree transformations and the reduction to the
 * hypergraph vertex covering problem. This experiment also has the property
 * that, once it has executed, it can be asked to compute the coverage ratio
 * of test suites produced by other experiments for the same criterion
 * (with {@link #getCoverage(Set)}).
 * 
 * @see DependentTestGenerationExperiment
 */
public class HittingSetTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * The name of the test generation method.
	 */
	public static final transient String NAME = "Hypergraph";
	
	/**
	 * The name of parameter "Number of edges".
	 */
	public static final transient String NUM_EDGES = "Number of edges";
	
	/**
	 * The name of parameter "Generation time".
	 */
	public static final transient String TIME_GENERATION = "Generation time";
	
	/**
	 * The name of parameter "Solving time".
	 */
	public static final transient String TIME_SOLVING = "Solving time";
	
	/**
	 * The truncations used to generate equivalence classes.
	 */
	protected transient Truncation[] m_truncations;
	
	/**
	 * The test suite produced as a result of running this experiment. Contrary
	 * to other types of experiments, here the test suite is kept so that it can
	 * be reused in eventual calls to {@link #getCoverage(Set)}.
	 */
	protected Set<Valuation> m_testSuite;
	
	static
	{
		warmUp();
	}
	
	/**
	 * Private empty constructor, added only to allow deserialization.
	 */
	private HittingSetTestGenerationExperiment()
	{
		super(null, null);
	}
	
	/**
	 * Creates a new experiment.
	 * @param formula The formula to generate coverage for
	 * @param formula_name A name given to the formula
	 * @param truncations  The truncations used to generate equivalence classes
	 */
	public HittingSetTestGenerationExperiment(Operator formula, String formula_name, Truncation ... truncations)
	{
		super(formula, formula_name);
		describe(TIME_GENERATION, "The time (in ms) required to generate the hypergraph");
		describe(TIME_SOLVING, "The time (in ms) required to find ahypergraph vertex covering");
		setInput(METHOD, NAME);
		m_truncations = truncations;
		m_testSuite = new HashSet<Valuation>();
	}
	
	/**
	 * Creates a new experiment.
	 * @param formula The formula to generate coverage for
	 * @param formula_name A name given to the formula
	 * @param truncations  The truncations used to generate equivalence classes
	 */
	public HittingSetTestGenerationExperiment(Operator formula, String formula_name, Set<Truncation> truncations)
	{
		super(formula, formula_name);
		setInput(METHOD, NAME);
		m_truncations = new Truncation[truncations.size()];
		int i = 0;
		for (Truncation t : truncations)
		{
			getTruncations()[i++] = t;
		}
		m_testSuite = new HashSet<Valuation>();
	}

	@Override
	public void execute() throws ExperimentException, InterruptedException 
	{
		long start = System.currentTimeMillis();
		write(SIZE, 0);
		write(TIME, 0);
		HypergraphGenerator generator = new HypergraphGenerator();
		Hypergraph h = generator.getGraph(getFormula(), getTruncations());
		long end_generation = System.currentTimeMillis();
		write(NUM_EDGES, h.getEdgeCount());
		setProgression(0.5f);
		PersistentHashSet phs = HittingSetRunner.runHittingSet(h);
		long end = System.currentTimeMillis();
		write(TIME, end - start);
		Iterator<?> it = phs.iterator();
		while (it.hasNext())
		{
			long vertex = (Long) it.next();
			Valuation v = generator.getValuation(vertex);
			if (v == null)
			{
				throw new ExperimentException("A valuation index corresponds to no valuation");
			}
			m_testSuite.add(v);
		}
		write(SIZE, phs.size());
		write(TIME_GENERATION, end_generation - start);
		write(TIME_SOLVING, end - end_generation);
		write(COVERAGE, 1);
	}
	
	/**
	 * Gets the category coverage ratio of a test suite for the same coverage
	 * criterion as the one considered in this experiment.
	 * @param test_suite The test suite
	 * @return The coverage ratio
	 */
	public float getCoverage(Set<Valuation> test_suite)
	{
		CategoryCoverage cov = new CategoryCoverage(getTruncations());
		return cov.getCoverage(getFormula(), test_suite, m_testSuite);
	}
	
	/**
	 * Gets the set of equivalence classes induced by the tree transformations
	 * and the formula in this experiment. Note that this will return a non-empty
	 * set only if the experiment has finished running and produced a test suite.
	 * @return The set of equivalence classes
	 */
	public Set<HologramNode> getTrees()
	{
		Set<HologramNode> trees = new HashSet<HologramNode>(m_testSuite.size());
		for (Valuation v : m_testSuite)
		{
			HologramNode original = getFormula().evaluate(v);
			for (Truncation t : getTruncations())
			{
				HologramNode transformed = t.applyTo(original);
				trees.add(transformed);
			}
		}
		return trees;
	}
	
	@Override
	public void tellId(int id)
	{
		writeDescription();
	}
	
	/**
	 * Writes the description of the experiment.
	 */
	protected void writeDescription()
	{
		StringBuilder out = new StringBuilder();
		out.append("<p>Generates a test suite using a reduction to hypergraph vertex covering. The formula used is:</p>\n");
		out.append("<blockquote>\n").append(getFormula().toString()).append("\n</blockquote>\n");
		out.append("<p>The set of ").append(getTruncations().length).append(" tree transformations is: ");
		for (int i = 0; i < getTruncations().length; i++)
		{
			if (i > 0)
			{
				out.append(", ");
			}
			out.append("<math>").append(getTruncations()[i].toMathML()).append("</math> ");
		}
		out.append("</p>\n\n<p><a href=\"/holograms/").append(getId()).append("\">See the equivalence classes</a> induced by this\n");
		out.append("set of tree transformations (this may take a long time)</p>\n");
		setDescription(out.toString());
	}
	
	/**
	 * Runs a dummy hitting set problem. This forces the JVM to load the
	 * Clojure classes before running the first experiment, which factors out
	 * this initial loading time. Otherwise, the first hitting set experiment
	 * always shows as an outlier in terms of running time, regardless of
	 * which one it is.
	 */
	protected static void warmUp()
	{
		Hypergraph h = new Hypergraph();
		h.addTo(new HologramNode("?", false), 0);
		HittingSetRunner.runHittingSet(h);
	}

	public Truncation[] getTruncations() {
		return m_truncations;
	}
}
