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

/**
 * Renders a hologram into a Graphviz file.
 */
public class HologramDotRenderer implements HologramRenderer
{
	@Override
	public void render(HologramNode n, PrintStream ps)
	{
		ps.println("digraph G {");
		ps.println("nodesep=0.125");
		ps.println("ranksep=0.25;");
		ps.println("node [shape=\"rectangle\",height=0.3,width=0.3,fixedsize=\"true\",style=\"filled\"];");
		ps.println("edge [arrowhead=\"none\"];");
		renderRecursive(n, ps, -1, new NodeCounter());
		ps.println("}");
	}

	protected void renderRecursive(HologramNode n, PrintStream ps, long parent, NodeCounter c)
	{
		long id = c.get();
		ps.print(id + " [label=<" + n.getLabel() + ">,fillcolor=");
		if (n.getValue() == null)
		{
			ps.print("\"grey\"");
		}
		else if (n.getValue() == true)
		{
			ps.print("\"green\"");
		}
		else if (n.getValue() == false)
		{
			ps.print("\"red\"");
		}
		ps.println("];");
		if (parent >= 0)
		{
			ps.println(parent + " -> " + id + ";");
		}
		c.increment();
		for (HologramNode child : n.m_children)
		{
			renderRecursive(child, ps, id, c);
		}
	}

	protected class NodeCounter
	{
		protected long m_count = 0;

		public long get()
		{
			return m_count;
		}

		public long increment()
		{
			return ++m_count;
		}
	}
}
