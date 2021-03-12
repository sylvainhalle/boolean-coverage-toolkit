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
	
	/**
	 * Gets the children of this node
	 * @return The list of children
	 */
	public List<HologramNode> getChildren()
	{
		return m_children;
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
	
	/**
	 * Determines if a hologram node or one of its descendants has a certain
	 * label.
	 * @param name The label to look for
	 * @return <tt>true</tt> if this label appears somewhere, <tt>false</tt>
	 * otherwise
	 */
	public boolean hasLabel(String name)
	{
		if (m_label.compareTo(name) == 0)
		{
			return true;
		}
		for (HologramNode child : m_children)
		{
			if (child.hasLabel(name))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Gets the Boolean value of the leaf with given label.
	 * @param v The label
	 * @return The Boolean value
	 */
	public Boolean getLeafValue(String v)
	{
		if (m_label.compareTo(v) == 0)
		{
			return m_value;
		}
		for (HologramNode child : m_children)
		{
			Boolean b = child.getLeafValue(v);
			if (b != null)
			{
				return b;
			}
		}
		return null;
	}
	
	/**
	 * Creates a new instance of dummy node.
	 * @return The node
	 */
	public static HologramNode dummyNode()
	{
		return new HologramNode(HologramNode.DUMMY_SYMBOL, null);
	}
	
	/**
	 * Creates a tree from another one by negating the value of a variable
	 * and percolating the changes.
	 * @param n The root of the original tree
	 * @param v The name of the variable
	 * @return A new copy of the tree with the modifications
	 */
	public static HologramNode negate(HologramNode n, String v)
	{
		HologramNode new_n = new HologramNode(n.getLabel(), n.getValue());
		String cur_label = n.getLabel();
		if (cur_label.compareTo(v) == 0)
		{
			new_n.setValue(!n.getValue());
		}
		else if (cur_label.compareTo(Negation.SYMBOL) == 0)
		{
			HologramNode new_child = negate(n.m_children.get(0), v);
			new_n.addChild(new_child);
			if (new_child.getValue() == null)
			{
				new_n.setValue(null);
			}
			else
			{
				new_n.setValue(!new_child.getValue());
			}
		}
		else if (cur_label.compareTo(Conjunction.SYMBOL) == 0)
		{
			boolean value = true;
			for (HologramNode child : n.m_children)
			{
				HologramNode new_child = negate(child, v);
				if (new_child.getValue() != null && new_child.getValue() == false)
				{
					value = false;
				}
				new_n.addChild(new_child);
			}
			new_n.setValue(value);
		}
		else if (cur_label.compareTo(Disjunction.SYMBOL) == 0)
		{
			boolean value = false;
			for (HologramNode child : n.m_children)
			{
				HologramNode new_child = negate(child, v);
				if (new_child.getValue() != null && new_child.getValue() == true)
				{
					value = true;
				}
				new_n.addChild(new_child);
			}
			new_n.setValue(value);
		}
		return new_n;
	}
	
	/**
	 * Creates a tree made of a 
	 * @param n
	 * @param child_nb
	 * @param v
	 * @return
	 */
	public static HologramNode getLeafForOtherVariable(HologramNode n, int child_nb, String v)
	{
		if (n.getChildren().get(child_nb).hasLabel(v))
		{
			return HologramNode.dummyNode();
		}
		HologramNode new_n = new HologramNode(n.getLabel(), n.getValue());
		for (int i = 0; i < n.getChildren().size(); i++)
		{
			if (i != child_nb)
			{
				new_n.addChild(HologramNode.dummyNode());
			}
			else
			{
				HologramNode child = n.getChildren().get(i);
				HologramNode new_child = new HologramNode(child.getLabel(), null);
				HologramNode new_under = new HologramNode(v, n.getLeafValue(v));
				new_child.addChild(new_under);
				new_n.addChild(new_child);
			}
		}
		return new_n;
	}
}
