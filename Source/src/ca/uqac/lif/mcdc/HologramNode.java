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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HologramNode
{
	/**
	 * The symbol representing the "dummy" node
	 */
	public static final String DUMMY_SYMBOL = "?";
	
	protected List<HologramNode> m_children;
	
	protected Boolean m_value = null;
	
	protected String m_label;
	
	public HologramNode(String label, Boolean value)
	{
		super();
		m_label = label;
		m_children = new ArrayList<HologramNode>();
		m_value = value;
	}
	
	public HologramNode(String label)
	{
		this(label, null);
	}
	
	public String getLabel()
	{
		return m_label;
	}
	
	public void setValue(Boolean b)
	{
		m_value = b;
	}
	
	public Boolean getValue()
	{
		return m_value;
	}
	
	public void addChild(HologramNode n)
	{
		m_children.add(n);
	}
	
	public HologramNode duplicate(boolean with_state)
	{
		HologramNode n = new HologramNode(m_label);
		for (HologramNode c : m_children)
		{
			n.addChild(c.duplicate(with_state));
		}
		if (with_state)
		{
			n.m_value = m_value;
		}
		return n;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof HologramNode))
		{
			return false;
		}
		HologramNode n = (HologramNode) o;
		if (m_value != n.m_value)
		{
			return false;
		}
		if (m_children.size() != n.m_children.size())
		{
			return false;
		}
		if (m_label.compareTo(n.m_label) != 0)
		{
			return false;
		}
		for (int i = 0; i < m_children.size(); i++)
		{
			HologramNode n1 = m_children.get(i);
			HologramNode n2 = n.m_children.get(i);
			if (n1 == null)
			{
				System.out.println("");
			}
			if (!m_children.get(i).equals(n.m_children.get(i)))
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return m_label.hashCode() * m_children.size();
	}
	
	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		toString(out, "");
		return out.toString();
	}
	
	protected void toString(StringBuilder out, String indent)
	{
		out.append(indent).append(m_label).append(",");
		if (m_value == true)
		{
			out.append("T");
		}
		else if (m_value == false)
		{
			out.append("F");
		}
		else
		{
			out.append("?");
		}
		String new_indent = indent + " ";
		for (HologramNode c : m_children)
		{
			c.toString(out, new_indent);
			out.append("\n");
		}
	}
	
	/**
	 * Get all the atoms present in this tree.
	 * @return The set of variable names occurring in the tree
	 */
	public Set<String> getAtoms()
	{
		Set<String> set = new HashSet<String>();
		getAtoms(set);
		return set;
	}
	
	protected void getAtoms(Set<String> atoms)
	{
		if (m_children.isEmpty())
		{
			atoms.add(m_label);
		}
		else
		{
			for (HologramNode n : m_children)
			{
				n.getAtoms(atoms);
			}
		}
	}
}
