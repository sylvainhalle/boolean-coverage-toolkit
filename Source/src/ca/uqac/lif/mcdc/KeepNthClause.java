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

import java.util.HashSet;
import java.util.Set;

public class KeepNthClause extends ClauseBasedTruncation
{
	/**
	 * Creates a new instance of the truncation.
	 * @param clause_nb The number of the clause to keep
	 */
	public KeepNthClause(int clause_nb)
	{
		super(clause_nb);
	}
	
	@Override
	public HologramNode applyTo(HologramNode n) 
	{
		HologramNode new_n = new HologramNode(n.getLabel(), n.getValue());
		for (int i = 0; i < n.m_children.size(); i++)
		{
			if (i == m_clauseNb)
			{
				HologramNode c = n.m_children.get(i);
				HologramNode new_c = new HologramNode(c.getLabel(), c.getValue());
				new_n.addChild(new_c);
			}
			else
			{
				HologramNode new_c = new HologramNode("?", null);
				new_n.addChild(new_c);
			}
		}
		return new_n;
	}
	
	/**
	 * Generates a set of transformations corresponding to clause coverage for
	 * a given formula.
	 * @param phi The formula
	 * @return The set of transformations
	 */
	public static Set<Truncation> generateClauseCoverage(Operator phi)
	{
		int num_clauses = countClauses(phi);
		Set<Truncation> out_set = new HashSet<Truncation>(num_clauses);
		for (int i = 0; i < num_clauses; i++)
		{
			out_set.add(new KeepNthClause(i));
		}
		return out_set;
	}
}
