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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Operator 
{
	public abstract HologramNode evaluate(Valuation v);
	
	public abstract Operator duplicate(boolean with_state);
	
	public abstract int getSize();
	
	/**
	 * Gets the set of all variables occurring in this operator.
	 * @return The set of all variable names
	 */
	public final Set<String> getVariables()
	{
		Set<String> vars = new HashSet<String>();
		getVariables(vars);
		return vars;
	}
	
	/**
	 * Gets the sorted array of all variables occurring in this operator.
	 * @return The array of all variable names
	 */
	public final String[] getSortedVariables()
	{
		Set<String> vars = getVariables();
		List<String> s_vars = new ArrayList<String>(vars.size());
		s_vars.addAll(vars);
		Collections.sort(s_vars);
		String[] a_vars = new String[s_vars.size()];
		for (int i = 0; i < a_vars.length; i++)
		{
			a_vars[i] = s_vars.get(i);
		}
		return a_vars;
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		toString(out);
		return out.toString();
	}
	
	protected abstract void getVariables(Set<String> vars);
	
	protected abstract void toString(StringBuilder out);
}
