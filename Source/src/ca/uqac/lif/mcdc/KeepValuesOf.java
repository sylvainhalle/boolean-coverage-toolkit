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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KeepValuesOf implements Truncation
{
	/**
	 * The list of variables to keep in the resulting tree.
	 */
	protected List<String> m_variables;
	
	/**
	 * Creates a new instance of this truncation.
	 * @param variables The list of variables to keep in the resulting tree
	 */
	public KeepValuesOf(List<String> variables)
	{
		super();
		m_variables = variables;
	}
	
	/**
	 * Creates a new instance of this truncation.
	 * @param variables The array of variables to keep in the resulting tree
	 */
	public KeepValuesOf(String ... variables)
	{
		super();
		m_variables = new ArrayList<String>(variables.length);
		for (String v : variables)
		{
			m_variables.add(v);
		}
	}

	@Override
	public HologramNode applyTo(HologramNode n) 
	{
		HologramNode new_n = new HologramNode("?", null);
		Map<String,HologramNode> vars = new HashMap<String,HologramNode>();
		getVariables(n, vars);
		for (String v : m_variables)
		{
			new_n.addChild(vars.get(v));
		}
		return new_n;
	}
	
	/**
	 * Fetches the leaf nodes of a tree that are among the variables to be
	 * kept.
	 * @param n The current hologram node
	 * @param vars A map linking variable names to their corresponding hologram
	 * node
	 */
	protected void getVariables(HologramNode n, Map<String,HologramNode> vars)
	{
		if (n.m_children.isEmpty())
		{
			String label = n.getLabel();
			if (m_variables.contains(label) && !vars.containsKey(label))
			{
				vars.put(label, n);
			}
		}
		else
		{
			for (HologramNode c : n.m_children)
			{
				getVariables(c, vars);
			}
		}
	}
	
	/**
	 * Generates a set of truncations that keep all combinations of <i>t</i>
	 * variables. This corresponds to the so-called "t-way" coverage
	 * criterion applied to a set of Boolean variables.
	 * @param t The number of variables to keep in each truncation
	 * @param variables The complete list of variables 
	 * @return A set of truncations
	 */
	public static Set<Truncation> generateTWay(int t, Operator op)
	{
		String[] variables = op.getSortedVariables();
		Set<Truncation> out_set = new HashSet<Truncation>();
		ValuationIterator it = new ValuationIterator(variables);
		while (it.hasNext())
		{
			Valuation v = it.next();
			if (v.countTrue() != t)
			{
				continue;
			}
			List<String> a_variables = new ArrayList<String>(t);
			for (String x : variables)
			{
				if (v.get(x) != null && v.get(x) == true)
				{
					a_variables.add(x);
				}
			}
			out_set.add(new KeepValuesOf(a_variables));
		}
		return out_set;
	}
	
	@Override
	public String toString()
	{
		return "τ" + m_variables;
	}
}
