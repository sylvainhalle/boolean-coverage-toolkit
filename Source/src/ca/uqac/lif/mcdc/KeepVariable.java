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

public class KeepVariable extends VariableBasedTruncation
{
	/**
	 * Creates a new instance of the truncation.
	 * @param name The name of the variable to keep
	 */
	public KeepVariable(String name)
	{
		super(name);
	}

	@Override
	public HologramNode applyTo(HologramNode n)
	{
		HologramNode v = findVariable(n);
		if (v == null)
		{
			return new HologramNode("?");
		}
		return v;
	}

	protected HologramNode findVariable(HologramNode n)
	{
		if (m_name.compareTo(n.getLabel()) == 0)
		{
			return n;
		}
		for (HologramNode c : n.m_children)
		{
			HologramNode v = findVariable(c);
			if (v != null)
			{
				return v;
			}
		}
		return null;
	}
}
