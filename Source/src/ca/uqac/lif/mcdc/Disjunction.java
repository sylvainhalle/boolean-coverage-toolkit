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
import java.util.Set;

public class Disjunction extends Operator
{
	/**
	 * The symbol for disjunction.
	 */
	public static final String SYMBOL = "\u2228";
	
	protected List<Operator> m_operands;
	
	public static Disjunction Or(Object ... ops)
	{
		Disjunction d = new Disjunction();
		for (Object o : ops)
		{
			if (o instanceof Operator)
			{
				d.m_operands.add((Operator) o);
			}
			if (o instanceof String)
			{
				d.m_operands.add(new Atom((String) o));
			}
		}
		return d;
	}
	
	public Disjunction(Operator ... operands)
	{
		super();
		m_operands = new ArrayList<Operator>(operands.length);
		for (Operator op : operands)
		{
			m_operands.add(op);
		}
	}
	
	@Override
	public HologramNode evaluate(Valuation v)
	{
		boolean b = false;
		HologramNode n = new HologramNode(SYMBOL);
		for (Operator op : m_operands)
		{
			HologramNode c = op.evaluate(v);
			n.addChild(c);
			if (c.getValue() == true)
			{
				b = true;
			}
		}
		n.setValue(b);
		return n;
	}

	@Override
	public Disjunction duplicate(boolean with_state) 
	{
		Disjunction a = new Disjunction();
		for (Operator op : m_operands)
		{
			a.m_operands.add(op.duplicate(with_state));
		}
		return a;
	}
	
	@Override
	protected void getVariables(Set<String> vars)
	{
		for (Operator op : m_operands)
		{
			op.getVariables(vars);
		}
	}
	
	@Override
	public int getSize()
	{
		int total = 1;
		for (Operator op : m_operands)
		{
			total += op.getSize();
		}
		return total;
	}
	
	@Override
	protected void toString(StringBuilder out) 
	{
		out.append("(");
		for (int i = 0; i < m_operands.size(); i++)
		{
			if (i > 0)
			{
				out.append(SYMBOL);
			}
			m_operands.get(i).toString(out);
		}
		out.append(")");
	}
	
	@Override
	public int getDepth()
	{
		int max = 0;
		for (Operator op : m_operands)
		{
			max = Math.max(max, op.getDepth());
		}
		return max + 1;
	}
}
