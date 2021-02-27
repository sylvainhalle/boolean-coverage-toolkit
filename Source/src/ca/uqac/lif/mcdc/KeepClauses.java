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

public class KeepClauses implements Truncation
{
	public static final KeepClauses instance = new KeepClauses();
	
	protected KeepClauses()
	{
		super();
	}
	
	@Override
	public HologramNode applyTo(HologramNode n)
	{
		if (n.getLabel().compareTo(Disjunction.SYMBOL) != 0)
		{
			// Not a formula in DNF, do nothing
			return n;
		}
		HologramNode root = new HologramNode(n.getLabel());
		root.setValue(n.getValue());
		for (HologramNode c : n.m_children)
		{
			HologramNode new_c = new HologramNode(c.getLabel());
			new_c.setValue(c.getValue());
			root.addChild(new_c);
		}
		return root;
	}
}
