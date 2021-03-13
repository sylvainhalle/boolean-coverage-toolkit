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

public class KeepIfMultipleUniqueTruePoint extends ClauseBasedTruncation
{
	protected String m_variableToGet;
	
	/**
	 * Creates a new instance of the truncation.
	 * @param clause_nb The number of the clause to keep
	 */
	public KeepIfMultipleUniqueTruePoint(int clause_nb, String to_get)
	{
		super(clause_nb);
		m_variableToGet = to_get;
	}

	@Override
	public HologramNode applyTo(HologramNode n)
	{
		if (n.m_children.size() <= m_clauseNb)
		{
			return HologramNode.dummyNode();
		}
		for (int i = 0; i < n.m_children.size(); i++)
		{
			HologramNode c = n.m_children.get(i);
			if (i != m_clauseNb && c.getValue() != null && c.getValue() == true)
			{
				// Another clause is true, so no unique true point
				return null;
			}
		}
		return HologramNode.getLeafForOtherVariable(n, m_clauseNb, m_variableToGet);
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
		Set<String> vars = phi.getVariables();
		for (String v : vars)
		{
			for (int i = 0; i < num_clauses; i++)
			{
				out_set.add(new KeepIfMultipleUniqueTruePoint(i, v));
			}
		}
		return out_set;
	}
	
	@Override
	public String toMathML()
	{
		return "<msubsup><mi>&tau;</mi> <mn>" + m_clauseNb + "</mn> <ms>MNFP</ms></msubsup>";
	}
}
