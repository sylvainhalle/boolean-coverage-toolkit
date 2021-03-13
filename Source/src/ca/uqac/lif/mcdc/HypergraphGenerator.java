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
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class HypergraphGenerator
{
	/**
	 * A list mapping valuations to integers (their position in the list)
	 */
	protected transient List<Valuation> m_indices;
	
	public HypergraphGenerator()
	{
		super();
		m_indices = new ArrayList<Valuation>();
	}
	
	public Hypergraph getGraph(Operator phi, Set<Truncation> truncations)
	{
		Truncation[] a_truncations = new Truncation[truncations.size()];
		int i = 0;
		for (Truncation t : truncations)
		{
			a_truncations[i++] = t;
		}
		return getGraph(phi, a_truncations);
	}
	
	public Hypergraph getGraph(Operator phi, Truncation ... truncations)
	{
		Hypergraph h = new Hypergraph();
		String[] vars = getSortedVariables(phi);
		long val_nb = 0;
		ValuationIterator it = new ValuationIterator(vars);
		while (it.hasNext())
		{
			Valuation v = it.next();
			m_indices.add(v);
			HologramNode n = phi.evaluate(v);
			for (Truncation t : truncations)
			{
				HologramNode truncated = t.applyTo(n);
				if (n != null)
				{
					h.addTo(truncated, val_nb);
				}
			}
			val_nb++;
		}
		return h;
	}
	
	public Valuation getValuation(long index)
	{
		return m_indices.get((int) index);
	}
	
	/**
	 * Gets the sorted list of all variables occurring in a formula.
	 * @param phi The formula
	 * @return An array where all variable names are sorted in ascending
	 * order.
	 */
	protected static String[] getSortedVariables(Operator phi)
	{
		Set<String> vars = phi.getVariables();
		List<String> s_vars = new ArrayList<String>(vars.size());
		s_vars.addAll(vars);
		Collections.sort(s_vars);
		String[] sorted = new String[s_vars.size()];
		for (int i = 0; i < sorted.length; i++)
		{
			sorted[i] = s_vars.get(i);
		}
		return sorted;
	}
}
