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

import ca.uqac.lif.mcdc.Operator;
import mcdclab.benchmark.TCASBenchmark;

import static ca.uqac.lif.mcdc.Conjunction.And;
import static ca.uqac.lif.mcdc.Disjunction.Or;
import static ca.uqac.lif.mcdc.Negation.Not;

/**
 * Unit tests for operators.
 */
@SuppressWarnings("unused")
public class OperatorTest
{
	@Test
	public void testVariables1()
	{
		TCASBenchmark benchmark = new TCASBenchmark();
		Operator op = benchmark.getFormula(18);
		Set<String> vars = op.getVariables();
		assertEquals(13, vars.size());
	}
}
