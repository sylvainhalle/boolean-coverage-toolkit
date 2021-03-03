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

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.mcdc.Operator;

/**
 * Stores Boolean formulas by associating a name to each of them.
 */
public class OperatorProvider
{
	/**
	 * The association between names and formulas
	 */
	protected transient Map<String,Operator> m_formulas;
	
	/**
	 * Creates a new empty operator provider.
	 */
	public OperatorProvider()
	{
		super();
		m_formulas = new HashMap<String,Operator>();
	}
	
	/**
	 * Adds an association between a formula and a name.
	 * @param name The name of the formula
	 * @param op The formula
	 */
	public void add(String name, Operator op)
	{
		m_formulas.put(name, op);
	}
	
	/**
	 * Gets the formula with given name. 
	 * @param name The name
	 * @return The formula with this name, or <tt>null</tt> if no formula
	 * with such a name exists
	 */
	public Operator getFormula(String name)
	{
		if (m_formulas.containsKey(name))
		{
			return m_formulas.get(name);
		}
		return null;
	}
	
	/**
	 * Gets the names of all formulas
	 * @return An array of formula names
	 */
	public String[] getNames()
	{
		String[] names = new String[m_formulas.size()];
		int i = 0;
		for (String name : m_formulas.keySet())
		{
			names[i++] = name;
		}
		return names;
	}
	
	/**
	 * Counts the number of formulas contained in this provider.
	 * @return The number of formulas
	 */
	public int countFormulas()
	{
		return m_formulas.size();
	}
	
	/**
	 * Computes the maximum nesting depth of all formulas contained in this
	 * provider.
	 * @return The maximum depth
	 */
	public int getMaxDepth()
	{
		int max_depth = 0;
		for (Operator op : m_formulas.values())
		{
			max_depth = Math.max(max_depth, op.getDepth());
		}
		return max_depth;
	}
	
	/**
	 * Computes the maximum number of variables of all formulas contained in this
	 * provider.
	 * @return The maximum number of variables
	 */
	public int getMaxVariables()
	{
		int max_nv = 0;
		for (Operator op : m_formulas.values())
		{
			max_nv = Math.max(max_nv, op.getVariables().size());
		}
		return max_nv;
	}
	
	/**
	 * Computes the maximum number of connectives of all formulas contained in
	 * this provider.
	 * @return The maximum number of connectives
	 */
	public int getMaxSize()
	{
		int max_nv = 0;
		for (Operator op : m_formulas.values())
		{
			max_nv = Math.max(max_nv, op.getSize());
		}
		return max_nv;
	}
}
