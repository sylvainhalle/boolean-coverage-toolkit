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
package ca.uqac.lif.mcdc.test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import ca.uqac.lif.mcdc.Atom;
import ca.uqac.lif.mcdc.HologramNode;
import ca.uqac.lif.mcdc.KeepIfDetermines;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Truncation;
import ca.uqac.lif.mcdc.Valuation;

import static ca.uqac.lif.mcdc.Conjunction.And;
import static ca.uqac.lif.mcdc.Disjunction.Or;
import static ca.uqac.lif.mcdc.Negation.Not;

/**
 * Unit tests for operators.
 */
@SuppressWarnings("unused")
public class TruncationTest
{
	protected static final Atom a = new Atom("a");
	protected static final Atom b = new Atom("b");
	protected static final Atom c = new Atom("c");
	protected static final Atom d = new Atom("d");

	@Test
	public void testKeepIfDetermines1()
	{
		Operator op = And(a, b);
		HologramNode n = op.evaluate(Valuation.get(true, true));
		Truncation t = new KeepIfDetermines(a);
		HologramNode t_n = t.applyTo(n);
		assertEquals(true, t_n.getValue());
	}
	
	@Test
	public void testKeepIfDetermines2()
	{
		Operator op = Or(And(a, b), c);
		HologramNode n = op.evaluate(Valuation.get(false, true, false));
		Truncation t = new KeepIfDetermines(a);
		HologramNode t_n = t.applyTo(n);
		assertEquals(false, t_n.getValue());
		assertEquals("a", t_n.getLabel());
	}
	
	@Test
	public void testKeepIfDetermines3()
	{
		Operator op = Or(And(a, b), c);
		HologramNode n = op.evaluate(Valuation.get(false, false, false));
		Truncation t = new KeepIfDetermines(a);
		HologramNode t_n = t.applyTo(n);
		assertEquals(false, t_n.getValue());
		assertEquals("?", t_n.getLabel());
	}
}
