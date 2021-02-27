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

import org.junit.Test;

import ca.uqac.lif.mcdc.Hypergraph;
import ca.uqac.lif.mcdc.HypergraphGenerator;
import ca.uqac.lif.mcdc.KeepAll;
import ca.uqac.lif.mcdc.KeepClauses;
import ca.uqac.lif.mcdc.KeepVariable;

import static ca.uqac.lif.mcdc.Conjunction.And;
import static ca.uqac.lif.mcdc.Disjunction.Or;
import static ca.uqac.lif.mcdc.Negation.Not;

/**
 * Unit tests for hypergraph generation.
 */
public class HypergraphTest
{
	@Test
	public void test1()
	{
		Hypergraph h = HypergraphGenerator.getGraph(Or("a", "b"), KeepAll.instance);
		assertEquals(4, h.getEdgeCount());
	}
	
	@Test
	public void test2()
	{
		Hypergraph h = HypergraphGenerator.getGraph(Or("a", And("b", Not("c"))), KeepAll.instance);
		assertEquals(8, h.getEdgeCount());
	}
	
	@Test
	public void test3()
	{
		Hypergraph h = HypergraphGenerator.getGraph(Or("a", "b"), new KeepVariable("a"));
		assertEquals(2, h.getEdgeCount());
	}
	
	@Test
	public void test4()
	{
		Hypergraph h = HypergraphGenerator.getGraph(Or(And("a", "b"), And("c", "d")), KeepClauses.instance);
		assertEquals(4, h.getEdgeCount());
	}
}
