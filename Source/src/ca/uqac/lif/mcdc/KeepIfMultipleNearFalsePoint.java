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

public class KeepIfMultipleNearFalsePoint extends ClauseBasedTruncation
{
	/**
	 * The variable to focus on
	 */
	protected String m_variableName;
	
	/**
	 * Creates a new instance of the truncation.
	 * @param clause_nb The number of the clause to keep
	 * @param variable The variable to focus on
	 */
	public KeepIfMultipleNearFalsePoint(int clause_nb, String variable)
	{
		super(clause_nb);
		m_variableName = variable;
	}

	@Override
	public HologramNode applyTo(HologramNode n)
	{
		if (n.m_children.size() <= m_clauseNb)
		{
			// Tree has fewer clauses than the one we look at
			return HologramNode.dummyNode();
		}
		for (int i = 0; i < n.m_children.size(); i++)
		{
			HologramNode c = n.m_children.get(i);
			if (i != m_clauseNb && c.getValue() != null && c.getValue() == false)
			{
				// Another clause is false, so no near false point
				return HologramNode.dummyNode();
			}
		}
		// clause_nb is the only false clause.
		HologramNode clause = n.getChildren().get(m_clauseNb);
		boolean seen_var = false;
		int num_false = 0;
		for (HologramNode child : clause.getChildren())
		{
			if (child.hasLabel(m_variableName))
			{
				seen_var = true;
			}
			if (child.getValue() != null && child.getValue() == false)
			{
				num_false++;
			}
		}
		if (!seen_var || num_false != 1)
		{
			// Either the variable is not in the clause, or it contains
			// more than one false term, so no near false point
			return HologramNode.dummyNode();
		}
		return getLeavesForOtherVariables(n);
	}
	
	/**
	 * Generates a set of transformations corresponding to multiple unique true
	 * point (MUTP) coverage for a given formula.
	 * @param phi The formula
	 * @return The set of transformations
	 */
	public static Set<Truncation> generateMNFPCoverage(Operator phi)
	{
		int num_clauses = countClauses(phi);
		Set<String> vars = phi.getVariables();
		Set<Truncation> out_set = new HashSet<Truncation>(num_clauses);
		for (String v : vars)
		{
			for (int i = 0; i < num_clauses; i++)
			{
				out_set.add(new KeepIfMultipleNearFalsePoint(i, v));
			}
		}
		return out_set;
	}
}
