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
package mcdclab;

import java.util.Map;

import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.NumberHelper;
import ca.uqac.lif.labpal.Experiment.Status;
import ca.uqac.lif.labpal.macro.Macro;
import ca.uqac.lif.labpal.macro.MacroMap;
import ca.uqac.lif.labpal.macro.MacroNode;
import ca.uqac.lif.petitpoucet.NodeFunction;

import static mcdclab.HittingSetTestGenerationExperiment.NUM_EDGES;
import static mcdclab.HittingSetTestGenerationExperiment.TIME;
import static mcdclab.HittingSetTestGenerationExperiment.TIME_GENERATION;
import static mcdclab.HittingSetTestGenerationExperiment.TIME_SOLVING;

/**
 * Fetches values about the execution time of the hypergraph experiments.
 */
public class HypergraphStats extends MacroMap
{
	/**
	 * Creates a new instance of the macro.
	 * @param lab The lab this macro is associated with
	 */
	public HypergraphStats(Laboratory lab)
	{
		super(lab);
		add("maxTimeHypergraphTotal", "The maximum time (in seconds) to generate a test suite with the hypergraph method");
		add("maxTimeHypergraphGeneration", "The maximum time (in seconds) to generate a hypergraph across all experiments");
		add("maxTimeHypergraphSolving", "The maximum time (in seconds) to find a hypergraph cover across all experiments");
		add("maxHypergraphSize", "The maximum number of hyperedges across all hypergraphs");
	}

	@Override
	public void computeValues(Map<String,JsonElement> map)
	{
		long max_total = 0, max_generation = 0, max_solving = 0, max_size = 0;
		for (Experiment e : m_lab.getExperiments())
		{
			if (e.getStatus() != Status.DONE || e.getClass() != HittingSetTestGenerationExperiment.class)
			{
				continue;
			}
			max_total = Math.max(max_total, e.readInt(TIME));
			max_generation = Math.max(max_generation, e.readInt(TIME_GENERATION));
			max_solving = Math.max(max_solving, e.readInt(TIME_SOLVING));
			max_size = Math.max(max_solving, e.readInt(NUM_EDGES));
		}
		map.put("maxTimeHypergraphTotal", new JsonNumber(NumberHelper.roundToSignificantFigures((float) max_total / 1000f, 3)));
		map.put("maxTimeHypergraphGeneration", new JsonNumber(NumberHelper.roundToSignificantFigures((float) max_generation / 1000f, 3)));
		map.put("maxTimeHypergraphSolving", new JsonNumber(NumberHelper.roundToSignificantFigures((float) max_solving / 1000f, 3)));
		map.put("maxTimeHypergraphSize", new JsonNumber(max_size));
	}

	@Override
	public NodeFunction getDependency()
	{
		return new MaxTimeHypergraphNode(this);
	}
	
	protected class MaxTimeHypergraphNode extends MacroNode
	{
		public MaxTimeHypergraphNode(Macro m)
		{
			super(m);
		}
	}
}
