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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An assignment of truth values to a set of variables.
 */
public class Valuation
{
	protected static final String[] s_alphabet = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"};
	
	protected Map<String,Boolean> m_values;

	public Valuation()
	{
		super();
		m_values = new HashMap<String,Boolean>();
	}
	
	public Valuation(Valuation v)
	{
		this();
		m_values.putAll(v.m_values);
	}
	
	public static Valuation get(boolean ... values)
	{
		Valuation v = new Valuation();
		for (int i = 0; i < values.length; i++)
		{
			v.set(s_alphabet[i], values[i]);
		}
		return v;
	}
	
	public void put(String[] names, boolean[] values)
	{
		for (int i = 0; i < names.length; i++)
		{
			m_values.put(names[i], values[i]);
		}
	}
	
	public void set(String name, boolean value)
	{
		m_values.put(name, value);
	}
	
	public Set<String> getVariables()
	{
		return m_values.keySet();
	}
	
	public Boolean get(String name)
	{
		if (m_values.containsKey(name))
		{
			return m_values.get(name);
		}
		return null;
	}
	
	/**
	 * Counts the number of variables set to <tt>true</tt> in the current
	 * valuation.
	 * @return The number of true variables
	 */
	public int countTrue()
	{
		int cnt = 0;
		for (Map.Entry<String,Boolean> e : m_values.entrySet())
		{
			if (e.getValue() == true)
			{
				cnt++;
			}
		}
		return cnt;
	}
}
