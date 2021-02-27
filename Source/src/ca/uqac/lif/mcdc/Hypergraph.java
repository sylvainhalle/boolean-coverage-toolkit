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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of a hypergraph. Vertices of the graph are
 * valuations, and hyperedges group together all valuations that produce a
 * given hologram.
 */
public class Hypergraph 
{
	/**
	 * A map associating a hologram to the hyperedge containing all valuations
	 * producing this hologram.
	 */
	protected Map<HologramNode,Hyperedge> m_edges;
	
	/**
	 * Creates a new empty hypergraph.
	 */
	public Hypergraph()
	{
		super();
		m_edges = new HashMap<HologramNode,Hyperedge>();
	}
	
	/**
	 * Adds a valuation to a hyperedge of the graph.
	 * @param n The hologram produced by the valuation
	 * @param id The ID given to this valuation
	 */
	public void addTo(HologramNode n, long id)
	{
		if (m_edges.containsKey(n))
		{
			Hyperedge e = m_edges.get(n);
			e.add(id);
		}
		else
		{
			Hyperedge e = new Hyperedge();
			e.add(id);
			m_edges.put(n, e);
			//System.out.println(n);
		}
	}
	
	/**
	 * Counts hyperedges in the hypergraph.
	 * @return The number of hyperedges
	 */
	public int getEdgeCount()
	{
		return m_edges.size();
	}
	
	/**
	 * Prints the hypergraph in EDN format.
	 * @param ps The {@link PrintStream} to print the hypergraph to 
	 */
	public void printTo(PrintStream ps)
	{
		long edge_cnt = 0;
		boolean first = true;
		ps.println("{");
		for (Map.Entry<HologramNode,Hyperedge> e : m_edges.entrySet())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				ps.println(",");
			}
			ps.print(edge_cnt + " ");
			e.getValue().printTo(ps);
			edge_cnt++;
		}
		ps.println();
		ps.println("}");
	}
	
	/**
	 * Implementation of a hyperedge, i.e. a set of vertices in a hypergraph.
	 * Here vertices are valuations, but they are represented as a
	 * <tt>long</tt>. Valuations are always enumerated in the same order and
	 * hence this number uniquely represents the position of a valuation in
	 * that enumeration.  
	 */
	protected static class Hyperedge
	{
		/**
		 * The set of vertices for this hyperedge.
		 */
		protected Set<Long> m_vertices;
		
		/**
		 * Creates a new empty hyperedge.
		 */
		public Hyperedge()
		{
			super();
			m_vertices = new HashSet<Long>();
		}
		
		/**
		 * Adds a vertex to the hyperedge.
		 * @param i The ID corresponding to the vertex
		 */
		public void add(long i)
		{
			m_vertices.add(i);
		}
		
		/**
		 * Prints the contents of a hyperedge. The printout follows the EDN
		 * format. 
		 * @param ps The {@link PrintStream} to print the edge to
		 */
		public void printTo(PrintStream ps)
		{
			ps.print("#{");
			boolean first = true;
			for (long v_id : m_vertices)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					ps.print(" ");
				}
				ps.print(v_id);
			}
			ps.print("}");
		}
		
		@Override
		public String toString()
		{
			StringBuilder out = new StringBuilder();
			out.append("#{");
			boolean first = true;
			for (long v_id : m_vertices)
			{
				if (first)
				{
					first = false;
				}
				else
				{
					out.append(" ");
				}
				out.append(v_id);
			}
			out.append("}");
			return out.toString();
		}
	}
}
