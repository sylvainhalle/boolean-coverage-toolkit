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
package mcdclab;

import java.util.Set;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mcdc.HittingSetRunner;
import ca.uqac.lif.mcdc.HologramNode;
import ca.uqac.lif.mcdc.Hypergraph;
import ca.uqac.lif.mcdc.HypergraphGenerator;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Truncation;

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
	 * The truncations used to generate equivalence classes.
	 */
	protected transient Truncation[] m_truncations;
	
	static
	{
		warmUp();
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
		setInput(METHOD, NAME);
		m_truncations = truncations;
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
			m_truncations[i++] = t;
		}
	}

	@Override
	public void execute() throws ExperimentException, InterruptedException 
	{
		long start = System.currentTimeMillis();
		write(SIZE, 0);
		write(TIME, 0);
		Hypergraph h = HypergraphGenerator.getGraph(m_formula, m_truncations);
		write(NUM_EDGES, h.getEdgeCount());
		setProgression(0.5f);
		int size = 0;
		size = HittingSetRunner.runHittingSet(h).size();
		long end = System.currentTimeMillis();
		write(SIZE, size);
		write(TIME, end - start);
		write(COVERAGE, 1);
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
}
