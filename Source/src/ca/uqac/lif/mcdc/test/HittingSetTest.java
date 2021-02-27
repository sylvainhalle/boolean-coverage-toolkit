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

import java.io.IOException;

import org.junit.Test;

import ca.uqac.lif.mcdc.HittingSetRunner;
import ca.uqac.lif.mcdc.Hypergraph;
import ca.uqac.lif.mcdc.HypergraphGenerator;
import ca.uqac.lif.mcdc.KeepAll;
import ca.uqac.lif.mcdc.KeepClauses;
import ca.uqac.lif.mcdc.KeepIfDetermines;
import ca.uqac.lif.mcdc.KeepVariable;
import mcdclab.TCASBenchmark;

import static ca.uqac.lif.mcdc.Conjunction.And;
import static ca.uqac.lif.mcdc.Disjunction.Or;
import static ca.uqac.lif.mcdc.Negation.Not;

/**
 * Unit tests for hitting set.
 */
public class HittingSetTest 
{
	@Test
	public void test1() throws IOException
	{
		Hypergraph h = HypergraphGenerator.getGraph(Or("a", "b"), KeepAll.instance);
		int n = HittingSetRunner.runHittingSet(h).size();
		assertEquals(4, n);
	}
	
	@Test
	public void test2() throws IOException
	{
		Hypergraph h = HypergraphGenerator.getGraph(Or(And("a", "b"), And("c", "d")), KeepClauses.instance);
		int n = HittingSetRunner.runHittingSet(h).size();
		assertEquals(4, n);
	}
	
	@Test
	public void test3() throws IOException
	{
		Hypergraph h = HypergraphGenerator.getGraph(TCASBenchmark.getFormula(3),
				new KeepIfDetermines("a"), new KeepIfDetermines("b"), new KeepIfDetermines("c"), 
				new KeepIfDetermines("d"), new KeepIfDetermines("e"), new KeepIfDetermines("f"), 
				new KeepIfDetermines("g"));
		int n = HittingSetRunner.runHittingSet(h).size();
		assertTrue(n >= 6);
	}
}
