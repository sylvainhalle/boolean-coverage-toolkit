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
package mcdclab.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;
import ca.uqac.lif.petitpoucet.NodeFunction;
import mcdclab.experiment.HittingSetTestGenerationExperiment;

public class HypergraphMultiBinDistribution extends Table
{
	/**
	 * The bins that represent the distribution of values
	 */
	protected Map<String,List<Bin>> m_bins;
	
	/**
	 * The experiments
	 */
	protected Map<String,Set<Experiment>> m_experiments;
	
	/**
	 * A list of reference bins
	 */
	List<Bin> m_referenceBins;
	
	/**
	 * An ordered list of categories
	 */
	List<String> m_categories;
	
	/**
	 * Creates a new instance of the table.
	 * @param open Set to <tt>true</tt> to add an open interval at the end of
	 * the distribution
	 * @param marks An increasing sequence of values corresponding to the
	 * borders of each bin.
	 */
	public HypergraphMultiBinDistribution(boolean open, float ... marks)
	{
		super();
		m_referenceBins = new ArrayList<Bin>(marks.length);
		m_categories = new ArrayList<String>();
		for (int i = 0; i < marks.length - 1; i++)
		{
			Bin b = new Bin(marks[i], marks[i+1]);
			m_referenceBins.add(b);
		}
		if (open)
		{
			m_referenceBins.add(new Bin(marks[marks.length - 1], null));
		}
		m_experiments = new HashMap<String,Set<Experiment>>();
		m_bins = new HashMap<String,List<Bin>>();
	}
	
	public void add(Experiment e)
	{
		if (e.getClass() != HittingSetTestGenerationExperiment.class)
		{
			return;
		}
		String category = e.readString(HittingSetTestGenerationExperiment.CRITERION);
		if (!m_categories.contains(category))
		{
			m_categories.add(category);
			m_experiments.put(category, new HashSet<Experiment>());
			List<Bin> new_bins = new ArrayList<Bin>(m_referenceBins.size());
			for (Bin b : m_referenceBins)
			{
				new_bins.add(b.duplicate());
			}
			m_bins.put(category, new_bins);
		}
		Set<Experiment> exps = m_experiments.get(category);
		exps.add(e);
	}
	
	protected void clearBins()
	{
		for (List<Bin> bins : m_bins.values())
		{
			for (Bin b : bins)
			{
				b.reset();
			}
		}
	}
	
	protected void updateBins(String category, float x)
	{
		List<Bin> bins = m_bins.get(category);
		for (Bin b : bins)
		{
			b.incrementIf(x);
		}
	}
	
	@Override
	public TempTable getDataTable(boolean temp)
	{
		String[] columns = new String[m_categories.size() + 1];
		columns[0] = "Bin";
		for (int i = 0; i < m_categories.size(); i++)
		{
			columns[i+1] = m_categories.get(i);
		}
		TempTable tt = new TempTable(getId(), columns);
		clearBins();
		for (Map.Entry<String,Set<Experiment>> entry : m_experiments.entrySet())
		{
			String category = entry.getKey();
			List<Bin> bins = m_bins.get(category);
			for (Experiment e : entry.getValue())
			{
				int count = e.readInt(HittingSetTestGenerationExperiment.NUM_EDGES);
				for (Bin b : bins)
				{
					b.incrementIf(count);
				}
			}
		}
		for (int i = 0; i < m_referenceBins.size(); i++)
		{
			TableEntry te = new TableEntry();
			te.put("Bin", i + ": " + m_referenceBins.get(i).toString());
			for (String category : m_categories)
			{
				List<Bin> bins = m_bins.get(category);
				Bin b = bins.get(i);
				te.put(category, b.m_count);
				tt.add(te);
			}
		}
		return tt;
	}

	@Override
	protected TempTable getDataTable(boolean temp, String... arg1)
	{
		// Ignore column order
		return getDataTable(temp);
	}

	@Override
	public NodeFunction getDependency(int arg0, int arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}	
}
