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

public class KeepTop implements Truncation
{
	/**
	 * The depth at which the tree should be cut.
	 */
	protected int m_depth;
	
	/**
	 * Creates a new instance of the truncation.
	 * @param depth The depth at which the tree should be cut
	 */
	public KeepTop(int depth)
	{
		super();
		m_depth = depth;
	}
	
	@Override
	public HologramNode applyTo(HologramNode n)
	{
		return cutAt(n, 1);
	}
	
	protected HologramNode cutAt(HologramNode n, int current_depth)
	{
		HologramNode new_n = new HologramNode(n.getLabel(), n.getValue());
		if (current_depth == m_depth)
		{
			return new_n;
		}
		for (HologramNode c : n.m_children)
		{
			new_n.addChild(cutAt(c, current_depth + 1));
		}
		return new_n;
	}
}
