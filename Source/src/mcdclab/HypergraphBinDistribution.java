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

import java.util.ArrayList;
import java.util.List;

import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;

public class HypergraphBinDistribution extends HittingSetExperimentTable
{
	/**
	 * The bins that represent the distribution of values
	 */
	protected List<Bin> m_bins;
	
	/**
	 * Creates a new instance of the table.
	 * @param open Set to <tt>true</tt> to add an open interval at the end of
	 * the distribution
	 * @param marks An increasing sequence of values corresponding to the
	 * borders of each bin.
	 */
	public HypergraphBinDistribution(boolean open, float ... marks)
	{
		super("Bin", "Size");
		m_bins = new ArrayList<Bin>(marks.length);
		for (int i = 0; i < marks.length - 1; i++)
		{
			Bin b = new Bin(marks[i], marks[i+1]);
			m_bins.add(b);
		}
		if (open)
		{
			m_bins.add(new Bin(marks[marks.length - 1], null));
		}
	}
	
	@Override
	public TempTable getDataTable(boolean temp)
	{
		TempTable tt = new TempTable(getId(), "Bin", "Size");
		clearBins();
		for (Experiment e : m_experiments)
		{
			int count = e.readInt(HittingSetTestGenerationExperiment.NUM_EDGES);
			updateBins(count);
		}
		for (int i = 0; i < m_bins.size(); i++)
		{
			Bin b = m_bins.get(i);
			TableEntry te = new TableEntry();
			te.put("Bin", i + ": " + b.toString());
			te.put("Size", b.m_count);
			tt.add(te);
		}
		return tt;
	}
	
	/**
	 * Clears all bins in the table.
	 */
	protected void clearBins()
	{
		for (Bin b : m_bins)
		{
			b.reset();
		}
	}
	
	/**
	 * Updates all bins in the table.
	 */
	protected void updateBins(float x)
	{
		for (Bin b : m_bins)
		{
			b.incrementIf(x);
		}
	}

}
