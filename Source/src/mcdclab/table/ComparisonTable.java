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

import java.util.LinkedList;
import java.util.List;

import ca.uqac.lif.json.JsonNull;
import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.provenance.ExperimentValue;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;
import ca.uqac.lif.petitpoucet.NodeFunction;

/**
 * Table creating (x,y) points from the results of pairs of experiments.
 * @author Sylvain Hallé
 */
public class ComparisonTable extends Table
{
	/**
	 * The list of pairs of experiments
	 */
	protected final List<ExperimentPair> m_pairs;

	/**
	 * The name of the parameter to read in each experiment
	 */
	protected final String m_parameter;

	/**
	 * The name of the first column
	 */
	protected String m_captionX = "x";

	/**
	 * The name of the second column
	 */
	protected String m_captionY = "y";

	/**
	 * Creates a new empty table.
	 * @param parameter The name of the parameter to read in each experiment
	 */
	public ComparisonTable(String parameter)
	{
		super();
		m_parameter = parameter;
		m_pairs = new LinkedList<ExperimentPair>();
	}

	/**
	 * Creates a new empty table.
	 * @param parameter The name of the parameter to read in each experiment
	 * @param caption_x The name of the first column
	 * @param caption_y The name of the second column
	 */
	public ComparisonTable(String parameter, String caption_x, String caption_y)
	{
		super();
		m_parameter = parameter;
		m_captionX = caption_x;
		m_captionY = caption_y;
		m_pairs = new LinkedList<ExperimentPair>();
	}

	/**
	 * Adds a new pair of experiments to the table
	 * @param experiment_x The first experiment of the pair
	 * @param experiment_y The second experiment of the pair
	 * @return This table
	 */
	public ComparisonTable add(Experiment experiment_x, Experiment experiment_y)
	{
		if (experiment_x != null && experiment_y != null)
		{
			m_pairs.add(new ExperimentPair(experiment_x, experiment_y));			
		}
		return this;
	}

	@Override
	protected TempTable getDataTable(boolean temporary, String... ordering) 
	{
		// Ignore ordering
		return getDataTable(temporary);
	}

	@Override
	public TempTable getDataTable(boolean temporary)
	{
		TempTable table = new TempTable(getId(), m_captionX, m_captionY);
		table.setId(getId());
		for (ExperimentPair pair : m_pairs)
		{
			Object x = pair.getExperimentX().read(m_parameter);
			Object y = pair.getExperimentY().read(m_parameter);
			if (x == null || y == null || x instanceof JsonNull || y instanceof JsonNull || discard(pair.getExperimentX()) || discard(pair.getExperimentY()))
			{
				continue;
			}
			TableEntry te = new TableEntry();
			if (x instanceof JsonNumber)
			{
				te.put(m_captionX, ((JsonNumber) x).numberValue());
			}
			else
			{
				te.put(m_captionX, x);
			}
			if (y instanceof JsonNumber)
			{
				te.put(m_captionY, ((JsonNumber) y).numberValue());
			}
			else
			{
				te.put(m_captionY, y);
			}
			te.addDependency(m_captionX, new ExperimentValue(pair.getExperimentX(), m_parameter));
			te.addDependency(m_captionY, new ExperimentValue(pair.getExperimentY(), m_parameter));
			table.add(te);
		}
		return table;
	}
	
	/**
	 * Gets the caption associated to the first column (i.e. x axis) of the
	 * table.
	 * @return The caption
	 */
	public String getCaptionX()
	{
		return m_captionX;
	}
	
	/**
	 * Gets the caption associated to the second columns (i.e. y axis) of the
	 * table.
	 * @return The caption
	 */
	public String getCaptionY()
	{
		return m_captionY;
	}

	/**
	 * Simple structure representing a pair of experiments and a label
	 */
	public static class ExperimentPair
	{
		/**
		 * The first experiment
		 */
		protected final Experiment m_experimentX;

		/**
		 * The second experiment
		 */
		protected final Experiment m_experimentY;

		/**
		 * Creates a new pair of experiments
		 * @param experiment_x The first experiment
		 * @param experiment_y The second experiment
		 */
		public ExperimentPair(Experiment experiment_x, Experiment experiment_y)
		{
			super();
			m_experimentX = experiment_x;
			m_experimentY = experiment_y;
		}

		/**
		 * Gets the first experiment in the pair
		 * @return The experiment
		 */
		public Experiment getExperimentX()
		{
			return m_experimentX;
		}

		/**
		 * Gets the second experiment in the pair
		 * @return The experiment
		 */
		public Experiment getExperimentY()
		{
			return m_experimentY;
		}
	}

	@Override
	public NodeFunction getDependency(int row, int col) 
	{
		return getDataTable().getDependency(row, col);
	}
	
	/**
	 * Determines whether an experiment should be discarded from the table
	 * @param e The experiment
	 * @return <tt>true</tt> if the experiment should be discarded,
	 * <tt>false</tt> otherwise
	 */
	protected boolean discard(Experiment e)
	{
		return e.getStatus() != Experiment.Status.DONE;
	}
	
	@Override
	public ComparisonTable duplicate(boolean with_state)
	{
		throw new UnsupportedOperationException("Cannot duplicate this table");
	}
}
