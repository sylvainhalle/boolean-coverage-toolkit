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

/**
 * An object that keeps track of an incrementing count.
 */
public class Counter
{
	protected int m_count;
	
	public Counter()
	{
		super();
		m_count = 0;
	}
	
	public void reset()
	{
		m_count = 0;
	}
	
	public void addTo(int x)
	{
		m_count += x;
	}
	
	public int get()
	{
		return m_count;
	}
}
