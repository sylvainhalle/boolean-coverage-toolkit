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
import java.util.List;
import java.util.Set;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mcdc.HittingSetRunner;
import ca.uqac.lif.mcdc.Hypergraph;
import ca.uqac.lif.mcdc.HypergraphGenerator;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Truncation;
import clojure.lang.PersistentHashSet;

public class CriterionFusionExperiment extends FormulaBasedExperiment
{
	/**
	 * The name of parameter "criteria".
	 */
	public static final transient String CRITERIA = "Criteria";
	
	/**
	 * The name of parameter "global size".
	 */
	public static final transient String SIZE_GLOBAL = "Size global";
	
	/**
	 * The name of parameter "merged size".
	 */
	public static final transient String SIZE_MERGED = "Size merged";
	
	/**
	 * The name of parameter "global time".
	 */
	public static final transient String TIME_GLOBAL = "Time global";
	
	/**
	 * The name of parameter "merged time".
	 */
	public static final transient String TIME_MERGED = "Time merged";
	
	/**
	 * The names of the criteria being processed separately
	 */
	protected transient List<String> m_criteriaNames;
	
	/**
	 * The set of sets of truncations to get coverage for.
	 */
	protected transient Set<Set<Truncation>> m_truncationSets;
	
	/**
	 * Creates a new experiment.
	 * @param formula The formula to generate coverage for
	 * @param formula_name A name given to the formula
	 * @param criteria_names The names of the criteria being processed
	 * separately
	 * @param truncation_sets The set of sets of truncations to get coverage
	 * for
	 */
	public CriterionFusionExperiment(Operator formula, String formula_name, List<String> criteria_names, Set<Set<Truncation>> truncation_sets)
	{
		super(formula, formula_name);
		describe(CRITERIA, "The coverage criteria to apply");
		describe(TIME_GLOBAL, "The time (in ms) taken to generate a test suite for all criteria at once");
		describe(SIZE_GLOBAL, "The size of the generated test suite for all criteria at once");
		describe(TIME_MERGED, "The time (in ms) taken to generate a test suite for all criteria separately");
		describe(SIZE_MERGED, "The size of the generated test suite for all separate criteria merged");
		String criteria = "";
		for (String s : criteria_names)
		{
			criteria += s + " ";
		}
		setInput(CRITERIA, criteria);
		m_criteriaNames = criteria_names;
		m_truncationSets = truncation_sets;
	}

	@Override
	public void execute() throws ExperimentException, InterruptedException 
	{
		long start = 0, end = 0;
		write(TIME_GLOBAL, 0);
		write(SIZE_GLOBAL, 0);
		write(TIME_MERGED, 0);
		write(SIZE_MERGED, 0);
		// Step 1: generate a test suite for all transformations at once
		Set<Truncation> all_truncations = new HashSet<Truncation>();
		for (Set<Truncation> st : m_truncationSets)
		{
			all_truncations.addAll(st);
		}
		start = System.currentTimeMillis();
		Hypergraph g_all = HypergraphGenerator.getGraph(m_formula, all_truncations);
		PersistentHashSet hs_all = HittingSetRunner.runHittingSet(g_all);
		end = System.currentTimeMillis();
		write(TIME_GLOBAL, end - start);
		write(SIZE_GLOBAL, hs_all.size());
		
		// Step 2: generate a test suite for each set of transformations and merge
		start = System.currentTimeMillis();
		Set<Long> suite = new HashSet<Long>();
		for (Set<Truncation> st : m_truncationSets)
		{
			Hypergraph g = HypergraphGenerator.getGraph(m_formula, st);
			PersistentHashSet hs = HittingSetRunner.runHittingSet(g);
			for (Object o : hs)
			{
				suite.add((long) o);
			}
		}
		end = System.currentTimeMillis();
		write(TIME_MERGED, end - start);
		write(SIZE_MERGED, suite.size());
	}

}
