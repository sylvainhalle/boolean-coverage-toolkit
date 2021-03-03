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

import java.util.Set;

public class Atom extends Operator
{
	protected String m_name;
	
	public Atom(String name)
	{
		super();
		m_name = name;
	}
	
	@Override
	public HologramNode evaluate(Valuation v)
	{
		HologramNode n = new HologramNode(m_name);
		n.setValue(v.get(m_name));
		return n;
	}

	@Override
	public Atom duplicate(boolean with_state) 
	{
		return new Atom(m_name);
	}
	
	@Override
	protected void getVariables(Set<String> vars)
	{
		vars.add(m_name);
	}
	
	@Override
	public int getSize()
	{
		return 1;
	}

	@Override
	protected void toString(StringBuilder out)
	{
		out.append(m_name);
	}

	@Override
	public int getDepth()
	{
		return 1;
	}
}
