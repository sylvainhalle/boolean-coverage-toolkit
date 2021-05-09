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

import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TableTransformation;
import ca.uqac.lif.mtnp.table.TempTable;

/**
 * Creates a table by filtering out the lines from another table.
 */
public class FilterLines implements TableTransformation
{
	/**
	 * The condition used to filter lines.
	 */
	protected transient FilterCondition m_condition;
	
	/**
	 * Creates a new filter.
	 * @param condition The condition used to filter lines
	 */
	public FilterLines(FilterCondition condition)
	{
		super();
		m_condition = condition;
	}
	
	@Override
	public TempTable transform(TempTable ... tables) 
	{
		TempTable tt = new TempTable(tables[0].getId(), tables[0].getColumnNames());
		for (TableEntry te : tables[0].getEntries())
		{
			if (m_condition.include(te))
			{
				tt.add(te);
			}
		}
		return tt;
	}
	
	/**
	 * A condition used to filter lines.
	 */
	public interface FilterCondition
	{
		/**
		 * Determines if a line should be included in the output result.
		 * @param entry The entry corresponding to a line in a table
		 * @return <tt>true</tt> if the line should be included, <tt>false</tt>
		 * otherwise
		 */
		public boolean include(TableEntry entry);
	}
}
