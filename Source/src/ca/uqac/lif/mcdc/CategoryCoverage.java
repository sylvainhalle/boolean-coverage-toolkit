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
}
