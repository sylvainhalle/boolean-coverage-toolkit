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

public class Negation extends Operator
{
	/**
	 * The symbol for negation.
	 */
	public static final String SYMBOL = "\u00ac";
	
	protected Operator m_operand;
	
	public static Negation Not(Object o)
	{
		if (o instanceof String)
		{
			return new Negation(new Atom((String) o));
		}
		if (o instanceof Operator)
		{
			return new Negation((Operator) o);
		}
		return null;
	}
	
	public Negation(Operator operand)
	{
		super();
		m_operand = operand;
	}
	
	@Override
	public HologramNode evaluate(Valuation v) 
	{
		HologramNode n = new HologramNode(SYMBOL);
		HologramNode c = m_operand.evaluate(v);
		n.addChild(c);
		n.setValue(!c.getValue());
		return n;
	}

	@Override
	public Negation duplicate(boolean with_state) 
	{
		return new Negation(m_operand.duplicate(with_state));
	}
	
	@Override
	protected void getVariables(Set<String> vars)
	{
		m_operand.getVariables(vars);
	}
	
	@Override
	public int getSize()
	{
		return 1 + m_operand.getSize();
	}
	
	@Override
	protected void toString(StringBuilder out) 
	{
		if (m_operand instanceof Atom)
		{
			out.append(SYMBOL);
			m_operand.toString(out);
		}
		else
		{
			out.append(SYMBOL).append("(");
			m_operand.toString(out);
			out.append(")");
		}
	}

	@Override
	public int getDepth()
	{
		return 1 + m_operand.getDepth();
	}
}
