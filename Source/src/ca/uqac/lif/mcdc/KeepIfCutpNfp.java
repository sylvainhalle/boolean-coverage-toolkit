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

public class KeepIfCutpNfp extends KeepIfMultipleNearFalsePoint
{
	public KeepIfCutpNfp(int clause_nb, String variable, String to_get)
	{
		super(clause_nb, variable, to_get);
	}

	@Override
	public HologramNode applyTo(HologramNode n)
	{
		if (n.m_children.size() <= m_clauseNb)
		{
			// Tree has fewer clauses than the one we look at
			return HologramNode.dummyNode();
		}
		if (isCandidateUTP(n) || isCandidateNFP(n))
		{
			return keepVariables(n);
		}
		return HologramNode.dummyNode();
	}

	/**
	 * Determines if a tree corresponds to a candidate unique true point. This
	 * is the case when:
	 * <ol>
	 * <li>clause i is a unique true point</li>
	 * <li>x determines clause i</li>
	 * <li>clause i is the only clause that x determines</li>
	 * </ol> 
	 * @param n The tree
	 * @return <tt>true</tt> if it corresponds to a candidate UTP,
	 * <tt>false</tt> otherwise
	 */
	protected boolean isCandidateUTP(HologramNode n)
	{
		for (int i = 0; i < n.m_children.size(); i++)
		{
			HologramNode c = n.m_children.get(i);
			if (i != m_clauseNb)
			{
				if (c.getValue() != null && c.getValue() == true)
				{
					// Another clause is true, so no unique true point
					return false;
				}
				if (KeepIfDetermines.determines(c, m_variableName) != null)
				{
					// x determines another clause than i: not a candidate UTP
					return false;
				}
			}
			if (i == m_clauseNb && KeepIfDetermines.determines(c, m_variableName) == null)
			{
				// x does not determine clause i: not a candidate UTP
				return false;
			}
		}
		return true;
	}

	/**
	 * Determines if a tree corresponds to a candidate near false point. This
	 * is the case when negating variable x results in a tree that is a
	 * candidate UTP.
	 * @param n The tree
	 * @return <tt>true</tt> if it corresponds to a candidate NFP,
	 * <tt>false</tt> otherwise
	 */
	protected boolean isCandidateNFP(HologramNode n)
	{
		if (!isNFP(n))
		{
			return false;
		}
		HologramNode negated = HologramNode.negate(n, m_variableName);
		return isCandidateUTP(negated);
	}
	
	protected static HologramNode keepVariables(HologramNode n)
	{
		Map<String,HologramNode> vars = new HashMap<String,HologramNode>();
		fetchVariables(n, vars);
		HologramNode root = new HologramNode(n.getLabel(), n.getValue());
		List<String> o_vars = new ArrayList<String>(vars.keySet().size());
		o_vars.addAll(vars.keySet());
		Collections.sort(o_vars);
		for (String v : o_vars)
		{
			root.addChild(vars.get(v));
		}
		return root;
	}
	
	protected static void fetchVariables(HologramNode n, Map<String,HologramNode> vars)
	{
		if (n.getChildren().isEmpty())
		{
			vars.put(n.getLabel(), n);
		}
		else
		{
			for (HologramNode c : n.getChildren())
			{
				fetchVariables(c, vars);
			}
		}
	}

	/**
	 * Generates a set of transformations corresponding to the CUTPNFP
	 * (CUTPNFP) coverage for a given formula. It actually generates
	 * transformations for a stronger condition (see the paper).
	 * @param phi The formula
	 * @return The set of transformations
	 */
	public static Set<Truncation> generateCUTPNFPCoverage(Operator phi)
	{
		int num_clauses = countClauses(phi);
		Set<String> vars = phi.getVariables();
		Set<Truncation> out_set = new HashSet<Truncation>(num_clauses);
		for (String v1 : vars)
		{
			for (String v2 : vars)
			{
				if (v1.compareTo(v2) == 0)
				{
					continue;
				}
				for (int i = 0; i < num_clauses; i++)
				{
					out_set.add(new KeepIfCutpNfp(i, v1, v2));
				}
			}
		}
		return out_set;
	}
}
