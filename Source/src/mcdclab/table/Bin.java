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
package mcdclab.table;

public class Bin
{
	/**
	 * The minimum value for this bin
	 */
	protected Float m_min;
	
	/**
	 * The maximum value for this bin
	 */
	protected Float m_max;
	
	/**
	 * The number of elements in this bin
	 */
	protected int m_count;
	
	/**
	 * Creates a new bin.
	 * @param min The minimum value for this bin Can be null to designate
	 * -infinity.
	 * @param max The minimum value for this bin. Can be null to designate
	 * +infinity.
	 */
	public Bin(float min, Float max)
	{
		super();
		m_min = min;
		m_max = max;
		m_count = 0;
	}
	
	/**
	 * Increments the count on this bin only if the provided value
	 * lies within the bin's range.
	 * @param x The value
	 */
	public void incrementIf(float x)
	{
		if (x >= m_min && x < m_max)
		{
			m_count++;
		}
	}
	
	/**
	 * Resets the count on this bin.
	 */
	public void reset()
	{
		m_count = 0;
	}
	
	/**
	 * Creates a copy of this bin.
	 * @return A new bin with the same bounds
	 */
	public Bin duplicate()
	{
		return new Bin(m_min, m_max);
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		//out.append("[");
		if (m_min == null)
		{
			out.append("-\u221e");
		}
		else
		{
			out.append(m_min.intValue());
		}
		out.append("-");
		if (m_max == null)
		{
			out.append("\u221e");
		}
		else
		{
			out.append(m_max.intValue());
		}
		//out.append(")");
		return out.toString();
	}
}