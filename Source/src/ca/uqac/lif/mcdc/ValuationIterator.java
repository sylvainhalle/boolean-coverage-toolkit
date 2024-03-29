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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Enumerates all valuations over a given domain.
 * @author Sylvain Hallé, Rania Taleb
 */
public class ValuationIterator implements Iterator<Valuation>
{
  /**
   * The array of variable names
   */
  protected String[] m_variables;
  
  /**
   * The array of values given to each variable
   */
  protected boolean[] m_vector;
  
  /**
   * Whether the iterator is done enumerating valuations
   */
  protected boolean m_done;
  
  /**
   * Whether the array of values should be updated to the next
   * valuation in the enumeration
   */
  protected boolean m_update;
  
  /**
   * Creates a new valuation iterator.
   * @param domain The set of variables. The iterator will enumerate all
   * combinations of valuations for these variables. 
   */
  public ValuationIterator(Set<String> domain)
  {
    super();
    m_variables = new String[domain.size()];
    m_vector = new boolean[m_variables.length];
    int i = 0;
    for (String s : domain)
    {
      m_variables[i++] = s;
    }
    for (i = 0; i < m_variables.length; i++)
    {
      m_vector[i] = false;
    }
    m_done = false;
    m_update = false;
  }
  
  /**
   * Creates a new valuation iterator.
   * @param domain The list of variables. The iterator will enumerate all
   * combinations of valuations for these variables. 
   */
  public ValuationIterator(String ... domain)
  {
    super();
    m_variables = domain;
    m_vector = new boolean[m_variables.length];
    int i = 0;
    for (String s : domain)
    {
      m_variables[i++] = s;
    }
    for (i = 0; i < m_variables.length; i++)
    {
      m_vector[i] = false;
    }
    m_done = false;
    m_update = false;
  }
  
  @Override
  public boolean hasNext()
  {
    if (m_update)
    {
      for (int i = 0; i < m_vector.length; i++)
      {
        if (m_vector[i] == false)
        {
          m_vector[i] = true;
          break;
        }
        else
        {
          m_vector[i] = false;
          if (i == m_vector.length - 1)
          {
            m_done = true;
          }
        }
      }
      m_update = false;
    }
    return !m_done;
  }

  @Override
  public Valuation next() throws NoSuchElementException
  {
    if (m_done)
    {
      throw new NoSuchElementException("No new valuation to enumerate");
    }
    Valuation v = new Valuation();
    for (int i = 0; i < m_vector.length; i++)
    {
      v.set(m_variables[i], m_vector[i]);
    }
    m_update = true;
    return v;
  }
  
  @Override
  public void remove()
  {
  	  throw new UnsupportedOperationException("");
  }
}
