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
package ca.uqac.lif.mcdc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CategoryCoverage extends TruncationMetric
{
	protected Truncation[] m_truncations;
	
	public CategoryCoverage(Truncation ... truncations)
	{
		super();
		m_truncations = truncations;
	}
	
	/**
	 * Computes the category coverage ratio of a test suite.
	 * 
	 * @param formula The formula
	 * @param test_suite The test suite to compute coverage
	 * @return The coverage ratio
	 */
	public float getCoverage(Operator formula, Set<Valuation> test_suite)
	{
		ObjectIdentifier<HologramNode> identifier_all = new ObjectIdentifier<HologramNode>();
		ObjectIdentifier<HologramNode> identifier_suite = new ObjectIdentifier<HologramNode>();
		ValuationIterator it = new ValuationIterator(formula.getSortedVariables());
		while (it.hasNext())
		{
			Valuation v = it.next();
			HologramNode original = formula.evaluate(v);
			for (Truncation t : m_truncations)
			{
				HologramNode n = t.applyTo(original);
				identifier_all.seenBefore(n);
				if (test_suite.contains(v)) 
				{
					identifier_suite.seenBefore(n);
				}
			}
		}
		return (float) identifier_suite.countDistinctObjects() / (float) identifier_all.countDistinctObjects();
	}
	
	/**
	 * Computes the category coverage ratio of a test suite, based on a
	 * reference test suite that is assumed to have full coverage. This method
	 * is typically much faster than {@link #getCoverage(Operator, Set)}.
	 * 
	 * @param formula The formula
	 * @param test_suite The test suite to compute coverage
	 * @param reference_test_suite The reference coverage
	 * @return The coverage ratio
	 */
	public float getCoverage(Operator formula, Set<Valuation> test_suite, Set<Valuation> reference_test_suite)
	{
		ObjectIdentifier<HologramNode> identifier_all = new ObjectIdentifier<HologramNode>();
		ObjectIdentifier<HologramNode> identifier_suite = new ObjectIdentifier<HologramNode>();
		for (Valuation v : reference_test_suite)
		{
			HologramNode original = formula.evaluate(v);
			for (Truncation t : m_truncations)
			{
				HologramNode n = t.applyTo(original);
				identifier_all.seenBefore(n);
			}
		}
		for (Valuation v : test_suite)
		{
			HologramNode original = formula.evaluate(v);
			for (Truncation t : m_truncations)
			{
				HologramNode n = t.applyTo(original);
				identifier_suite.seenBefore(n);
			}
		}
		return (float) identifier_suite.countDistinctObjects() / (float) identifier_all.countDistinctObjects();
	}
	
	/**
	 * Computes the distribution of all valuations across equivalence classes
	 * induced by the set of tree transformations. A distribution is an
	 * associative map between a tree and an integer, representing the number
	 * of valuations that produce the given tree.
	 * @param formula The formula on which to apply the transformations
	 * @return A set of distributions, one for each tree transformation
	 */
	public List<Map<HologramNode,Integer>> getCategoryDistribution(Operator formula)
	{
		String[] variables = formula.getSortedVariables();
		List<Map<HologramNode,Integer>> distros = new ArrayList<Map<HologramNode,Integer>>(m_truncations.length);
		for (Truncation t : m_truncations)
		{
			ObjectCounter<HologramNode> id = new ObjectCounter<HologramNode>();
			ValuationIterator it = new ValuationIterator(variables);
			while (it.hasNext())
			{
				Valuation v = it.next();
				HologramNode n = t.applyTo(formula.evaluate(v));
				if (n != null)
				id.seenBefore(n);
			}
			distros.add(id.m_objectIds);
		}
		return distros;
	}
}
