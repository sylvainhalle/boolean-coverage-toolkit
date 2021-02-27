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
import java.util.List;

/**
 * A tuple of numerical values.
 */
public class NumericTuple
{
	/**
	 * The list of values
	 */
	protected List<Integer> m_elements;
	
	/**
	 * Creates a new empty tuple.
	 */
	public NumericTuple()
	{
		super();
		m_elements = new ArrayList<Integer>();
	}

	/**
	 * Adds an element to the tuple.
	 * @param n The element to add
	 */
	public void add(int n)
	{
		m_elements.add(n);
	}

	@Override
	public int hashCode()
	{
		int cnt = 0;
		for (int i = 0; i < m_elements.size(); i++)
		{
			cnt += (i + 1) * m_elements.get(i);
		}
		return cnt;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof NumericTuple))
		{
			return false;
		}
		NumericTuple ht = (NumericTuple) o;
		if (ht.m_elements.size() != m_elements.size())
		{
			return false;
		}
		for (int i = 0; i < m_elements.size(); i++)
		{
			if (ht.m_elements.get(i) != m_elements.get(i)) 
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString()
	{
		return m_elements.toString();
	}
}
