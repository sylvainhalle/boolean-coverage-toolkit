package mcdclab.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.mtnp.table.Table;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;
import ca.uqac.lif.petitpoucet.NodeFunction;

public class MultiComparisonTable extends Table
{
	/**
	 * The experiment parameter name used to group experiments.
	 */
	protected String m_key;
	
	/**
	 * The experiment parameter used to name experiments in the same group.
	 */
	protected String m_categoryKey;
	
	/**
	 * The category value to plot in the "x" axis.
	 */
	protected String m_xCategory;
	
	/**
	 * The experiment parameter whose value is to be put into the table.
	 */
	protected String m_value;
	
	/**
	 * The experiments for each entry.
	 */
	protected Map<String,Map<String,Experiment>> m_entries;
	
	/**
	 * The list of distinct category values that occurred in the experiments
	 * added to the table.
	 */
	protected List<String> m_categories;
	
	public MultiComparisonTable(String key, String category_key, String x_category, String value)
	{
		super();
		m_entries = new HashMap<String,Map<String,Experiment>>();
		m_categories = new ArrayList<String>();
		m_key = key;
		m_categoryKey = category_key;
		m_xCategory = x_category;
		m_value = value;
	}
	
	public MultiComparisonTable add(Experiment e)
	{
		String key = e.readString(m_key);
		if (!m_entries.containsKey(key))
		{
			m_entries.put(key, new HashMap<String,Experiment>());
		}
		String category = e.readString(m_categoryKey);
		if (category.compareTo(m_xCategory) != 0 && !m_categories.contains(category))
		{
			m_categories.add(category);
		}
		Map<String,Experiment> map = m_entries.get(key);
		map.put(category, e);
		return this;
	}
	
	@Override
	public TempTable getDataTable(boolean temporary)
	{
		String[] categories = new String[m_categories.size() + 1];
		categories[0] = m_xCategory;
		for (int i = 0; i < m_categories.size(); i++)
		{
			categories[i+1] = m_categories.get(i);
		}
		TempTable table = new TempTable(getId(), categories);
		table.setId(getId());
		for (Map.Entry<String,Map<String,Experiment>> e : m_entries.entrySet())
		{
			Map<String,Experiment> exps = e.getValue();
			TableEntry te = new TableEntry();
			for (String cat : categories)
			{
				if (exps.containsKey(cat))
				{
					Experiment x_exp = exps.get(cat);
					JsonElement je = x_exp.read(m_value);
					if (je != null)
					{
						te.put(cat, je);
					}
				}
			}
			table.add(te);
		}
		return table;
	}

	@Override
	protected TempTable getDataTable(boolean temporary, String... ordering)
	{
		// Ignore ordering
		return getDataTable(temporary);
	}

	@Override
	public NodeFunction getDependency(int row, int col)
	{
		return getDataTable().getDependency(row, col);
	}
	
	@Override
	public MultiComparisonTable duplicate(boolean with_state)
	{
		throw new UnsupportedOperationException("Cannot duplicate this table");
	}
}
