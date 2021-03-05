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

public class KeepIfCutpNfpTest
{
	protected static final Atom a = new Atom("a");
	protected static final Atom b = new Atom("b");
	protected static final Atom c = new Atom("c");
	protected static final Atom d = new Atom("d");
	
	@Test
	public void test1()
	{
		Operator op = Or(And("a", "b"), And("c", "d"));
		HologramNode n = op.evaluate(Valuation.get(true, false, false, true));
		KeepIfCutpNfp t = new KeepIfCutpNfp(0, "a", "c");
		HologramNode n_t = t.applyTo(n);
	}
}
