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

public class KeepIfUniquelyDetermines extends VariableBasedTruncation
{
	/**
	 * Creates a new instance of the truncation.
	 * @param name The name of the variable to keep
	 */
	public KeepIfUniquelyDetermines(String name)
	{
		super(name);
	}
	
	/**
	 * Creates a new instance of the truncation.
	 * @param a The atom to keep
	 */
	public KeepIfUniquelyDetermines(Atom a)
	{
		this(a.m_name);
	}

	@Override
	public HologramNode applyTo(HologramNode n)
	{
		HologramNode new_n = determines(n, m_name);
		if (new_n == null)
		{
			new_n = new HologramNode("?");
			new_n.setValue(n.getValue());
		}
		return new_n;
	}
	
	public static HologramNode determines(HologramNode n, String var_name)
	{
		if (n.getLabel().compareTo(Conjunction.SYMBOL) == 0)
		{
			if (n.getValue() == true)
			{
				for (HologramNode c : n.m_children)
				{
					HologramNode new_c = determines(c, var_name);
					if (new_c != null)
					{
						return new_c;
					}
				}
				return null;
			}
			else
			{
				for (HologramNode c : n.m_children)
				{
					HologramNode new_c = determines(c, var_name);
					if (new_c == null)
					{
						return null;
					}
				}
				return new HologramNode(var_name);
			}
		}
		else if (n.getLabel().compareTo(Disjunction.SYMBOL) == 0)
		{
			if (n.getValue() == false)
			{
				for (HologramNode c : n.m_children)
				{
					HologramNode new_c = determines(c, var_name);
					if (new_c != null)
					{
						return new_c;
					}
				}
				return null;
			}
			else
			{
				for (HologramNode c : n.m_children)
				{
					HologramNode new_c = determines(c, var_name);
					if (new_c == null)
					{
						return null;
					}
				}
				return new HologramNode(var_name);
			}
		}
		else if (n.getLabel().compareTo(Negation.SYMBOL) == 0)
		{
			return determines(n.m_children.get(0), var_name);
		}
		else if (n.getLabel().compareTo(var_name) == 0)
		{
			return n;
		}
		return null;
	}
	
	@Override
	public String toString()
	{
		return m_name;
	}
}
