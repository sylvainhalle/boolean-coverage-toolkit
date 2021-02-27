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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeepIfMultipleUniqueTruePoint extends ClauseBasedTruncation
{
	/**
	 * Creates a new instance of the truncation.
	 * @param clause_nb The number of the clause to keep
	 */
	public KeepIfMultipleUniqueTruePoint(int clause_nb)
	{
		super(clause_nb);
	}

	@Override
	public HologramNode applyTo(HologramNode n)
	{
		if (n.m_children.size() <= m_clauseNb)
		{
			return new HologramNode(HologramNode.DUMMY_SYMBOL, null);
		}
		for (int i = 0; i < n.m_children.size(); i++)
		{
			HologramNode c = n.m_children.get(i);
			if (i != m_clauseNb && c.getValue() != null && c.getValue() == true)
			{
				// Another clause is true, so no unique true point
				return new HologramNode(HologramNode.DUMMY_SYMBOL, null);
			}
		}
		List<String> sorted_vars = getOtherVariables(n); 
		HologramNode new_n = new HologramNode(n.getLabel(), n.getValue());
		Map<String,HologramNode> leaves = new HashMap<String,HologramNode>(sorted_vars.size());
		for (int i = 0; i < n.m_children.size(); i++)
		{
			if (i == m_clauseNb)
			{
				continue;
			}
			HologramNode c = n.m_children.get(i);
			fetchOtherVariables(c, sorted_vars, leaves);
		}
		for (String v : sorted_vars)
		{
			new_n.addChild(leaves.get(v));
		}
		return new_n;
	}
	
	protected List<String> getOtherVariables(HologramNode n)
	{
		Set<String> all_vars = n.getAtoms();
		Set<String> vars = n.m_children.get(m_clauseNb).getAtoms();
		List<String> sorted_vars = new ArrayList<String>();
		for (String v : all_vars)
		{
			if (!vars.contains(v))
			{
				sorted_vars.add(v);
			}
		}
		Collections.sort(sorted_vars);
		return sorted_vars;
	}
	
	protected void fetchOtherVariables(HologramNode n, List<String> other_variables, Map<String,HologramNode> leaves)
	{
		String label = n.getLabel();
		if (n.m_children.isEmpty())
		{
			if (other_variables.contains(label) && !leaves.containsKey(label))
			{
				leaves.put(label, n);
			}
		}
		else
		{
			for (HologramNode c : n.m_children)
			{
				fetchOtherVariables(c, other_variables, leaves);
			}
		}
	}
	
	/**
	 * Generates a set of transformations corresponding to multiple unique true
	 * point (MUTP) coverage for a given formula.
	 * @param phi The formula
	 * @return The set of transformations
	 */
	public static Set<Truncation> generateMUTPCoverage(Operator phi)
	{
		int num_clauses = countClauses(phi);
		Set<Truncation> out_set = new HashSet<Truncation>(num_clauses);
		for (int i = 0; i < num_clauses; i++)
		{
			out_set.add(new KeepIfMultipleUniqueTruePoint(i));
		}
		return out_set;
	}
}
