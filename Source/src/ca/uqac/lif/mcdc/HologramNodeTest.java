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

import org.junit.Test;

import ca.uqac.lif.mcdc.Atom;
import ca.uqac.lif.mcdc.HologramNode;
import ca.uqac.lif.mcdc.KeepIfCutpNfp;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Valuation;

import static ca.uqac.lif.mcdc.Conjunction.And;
import static ca.uqac.lif.mcdc.Disjunction.Or;
import static ca.uqac.lif.mcdc.Negation.Not;
import static org.junit.Assert.*;

public class HologramNodeTest
{
	protected static final Atom a = new Atom("a");
	protected static final Atom b = new Atom("b");
	protected static final Atom c = new Atom("c");
	protected static final Atom d = new Atom("d");
	protected static final Atom e = new Atom("e");
	protected static final Atom f = new Atom("f");
	
	@Test
	public void test1()
	{
		Operator op = Or(And(a, b, c), And(d, e, f), And(b, c, e));
		HologramNode n = op.evaluate(Valuation.get(true, true, true, true, true, true));
		HologramNode t_n = HologramNode.getLeafForOtherVariable(n, 1, "e");
		assertEquals(t_n.getLabel(), HologramNode.DUMMY_SYMBOL);
	}
	
	@Test
	public void test2()
	{
		Operator op = Or(And(a, b, c), And(d, e, f), And(b, c, e));
		HologramNode n = op.evaluate(Valuation.get(true, true, true, true, true, true));
		HologramNode t_n = HologramNode.getLeafForOtherVariable(n, 1, "c");
		assertEquals(t_n.getLabel(), Disjunction.SYMBOL);
		assertEquals(3, t_n.getChildren().size());
		HologramNode c;
		c = t_n.getChildren().get(0);
		assertEquals(c.getLabel(), HologramNode.DUMMY_SYMBOL);
		c = t_n.getChildren().get(1);
		assertEquals(c.getLabel(), Conjunction.SYMBOL);
		c = t_n.getChildren().get(2);
		assertEquals(c.getLabel(), HologramNode.DUMMY_SYMBOL);
	}
	
}
