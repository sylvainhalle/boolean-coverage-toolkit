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

import java.util.HashMap;
import java.util.Map;

public class ObjectIdentifier<T> 
{
	/**
	 * A map between hologram nodes and unique IDs
	 */
	protected Map<T,Integer> m_objectIds;
	
	/**
	 * A counter for holograms
	 */
	protected int m_idCounter;
	
	public ObjectIdentifier()
	{
		super();
		m_objectIds = new HashMap<T,Integer>();
		m_idCounter = 0;
	}
	
	/**
	 * Returns the number of distinct objects identified by this instance of
	 * identifier.
	 * @return The number of objects
	 */
	protected int countDistinctObjects()
	{
		return m_objectIds.size();
	}
	
	/**
	 * Gets the unique ID associated to a given hologram node. If the node
	 * has been seen before, the ID associated to this node is returned.
	 * Otherwise, the ID counter is incremented and this node is associated
	 * to the current counter value.
	 * @param n The hologram node
	 * @return The unique ID
	 */
	protected int getObjectId(T n)
	{
		if (m_objectIds.containsKey(n)) 
		{
			return m_objectIds.get(n);
		}
		m_idCounter++;
		m_objectIds.put(n, m_idCounter);
		return m_idCounter;
	}
}
