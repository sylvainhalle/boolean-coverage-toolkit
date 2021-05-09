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
import java.util.Set;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mcdc.Valuation;
import ca.uqac.lif.synthia.random.RandomBoolean;
import mcdclab.benchmark.ValuationPicker;

/**
 * Generates a test suite by randomly picking tests. This experiment follows the
 * approach of Hu et al. (DSA 2018): it first generates a test suite using the
 * hypergraph technique and gets the number <i>n</i> of test cases. It then
 * randomly picks <i>n</i> test cases and measures coverage.
 */
public class RandomTestGenerationExperiment extends DependentTestGenerationExperiment
{
	/**
	 * The name of the test generation method.
	 */
	public static final transient String NAME = "Random";
	
	/**
	 * The number of times a test suite is randomly generated
	 */
	public static final transient int NUM_RERUNS = 5;
	
	/**
	 * The starting seed for the random pickers.
	 */
	protected int m_seed;
	
	/**
	 * Empty constructor for deserialization.
	 */
	protected RandomTestGenerationExperiment() 
	{
		this(null, 0);
	}
	
	/**
	 * Creates a new experiment instance.
	 * @param reference @param reference The hitting set experiment that is used
	 * as a reference
	 * @param seed The starting seed for the random pickers
	 */
	public RandomTestGenerationExperiment(HittingSetTestGenerationExperiment reference, int seed) 
	{
		super(reference);
		setInput(METHOD, NAME);
		setDescription(reference.getFormula().toString());
		m_seed = seed;
	}
	
	@Override
	public void execute() throws ExperimentException, InterruptedException 
	{
		write(SIZE, 0);
		write(TIME, 0);
		if (m_reference.getStatus() != Status.DONE)
		{
			m_reference.run();
		}
		setProgression(0.5f);
		int target_size = m_reference.readInt(SIZE);
		RandomBoolean bool = new RandomBoolean(m_seed);
		ValuationPicker picker = new ValuationPicker(bool, getFormula().getSortedVariables());
		float best_coverage = 0;
		long start = System.currentTimeMillis();
		for (int run = 0; run < NUM_RERUNS; run++)
		{
			Set<Valuation> suite = new HashSet<Valuation>(target_size);
			for (int i = 0; i < target_size; i++)
			{
				suite.add(picker.pick());
			}
			best_coverage = Math.max(best_coverage, m_reference.getCoverage(suite));
		}
		long end = System.currentTimeMillis();
		write(COVERAGE, best_coverage);
		write(SIZE, target_size);
		write(TIME, end - start);
	}	
}
