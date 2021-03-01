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

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mcdc.CategoryCoverage;
import ca.uqac.lif.mcdc.HittingSetRunner;
import ca.uqac.lif.mcdc.Hypergraph;
import ca.uqac.lif.mcdc.HypergraphGenerator;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Truncation;
import ca.uqac.lif.mcdc.Valuation;
import ca.uqac.lif.synthia.random.RandomBoolean;

/**
 * Generates a test suite by randomly picking tests. This experiment follows the
 * approach of Hu et al. (DSA 2018): it first generates a test suite using the
 * hypergraph technique and gets the number <i>n</i> of test cases. It then
 * randomly picks <i>n</i> test cases and measures coverage.
 */
public class RandomTestGenerationExperiment extends HittingSetTestGenerationExperiment
{
	/**
	 * The name of the test generation method.
	 */
	public static final transient String NAME = "Random";
	
	public RandomTestGenerationExperiment(Operator formula, String formula_name, Set<Truncation> truncations) 
	{
		super(formula, formula_name, truncations);
		setInput(METHOD, NAME);
	}
	
	public RandomTestGenerationExperiment(Operator formula, String formula_name, Truncation ... truncations) 
	{
		super(formula, formula_name, truncations);
		setInput(METHOD, NAME);
	}
	
	@Override
	public void execute() throws ExperimentException, InterruptedException 
	{
		
		write(SIZE, 0);
		write(TIME, 0);
		Hypergraph h = HypergraphGenerator.getGraph(m_formula, m_truncations);
		int target_size = 0;
		target_size = HittingSetRunner.runHittingSet(h).size();
		RandomBoolean bool = new RandomBoolean();
		ValuationPicker picker = new ValuationPicker(bool, m_formula.getSortedVariables());
		Set<Valuation> suite = new HashSet<Valuation>(target_size);
		long start = System.currentTimeMillis();
		for (int i = 0; i < target_size; i++)
		{
			suite.add(picker.pick());
		}
		long end = System.currentTimeMillis();
		CategoryCoverage cov = new CategoryCoverage(m_truncations);
		write(COVERAGE, cov.getCoverage(m_formula, suite));
		write(SIZE, target_size);
		write(TIME, end - start);
	}
	
}
