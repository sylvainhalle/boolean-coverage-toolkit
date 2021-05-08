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
package mcdclab.benchmark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.uqac.lif.mcdc.Atom;
import ca.uqac.lif.synthia.Picker;
import ca.uqac.lif.synthia.random.RandomInteger;

public class AtomPicker implements Picker<Atom>
{
	/**
	 * Alphabet for producing variable names
	 */
	protected static final String[] s_alphabet = {"a", "b", "c", "d", "e", "f",
			"g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
			"t", "u", "v", "w", "x", "y", "z"};
	
	/**
	 * The list of atoms
	 */
	protected List<Atom> m_atoms;
	
	/**
	 * The list of atoms that have not yet been picked
	 */
	protected List<Atom> m_availableAtoms;
	
	public AtomPicker(int num_atoms)
	{
		this(getAtoms(num_atoms));
	}
	
	public AtomPicker(Atom ... atoms)
	{
		super();
		m_atoms = new ArrayList<Atom>(atoms.length);
		m_availableAtoms = new ArrayList<Atom>(atoms.length);
		for (Atom a : atoms)
		{
			m_atoms.add(a);
			m_availableAtoms.add(a);
		}
	}
	
	public AtomPicker(Collection<Atom> atoms)
	{
		super();
		m_atoms = new ArrayList<Atom>(atoms.size());
		m_availableAtoms = new ArrayList<Atom>(atoms.size());
		for (Atom a : atoms)
		{
			m_atoms.add(a);
			m_availableAtoms.add(a);
		}
	}
	
	@Override
	public AtomPicker duplicate(boolean with_state)
	{
		AtomPicker ap = new AtomPicker(m_atoms);
		if (with_state)
		{
			ap.m_availableAtoms.clear();
			ap.m_availableAtoms.addAll(m_availableAtoms);
		}
		return ap;
	}

	@Override
	public Atom pick()
	{
		RandomInteger ri = new RandomInteger(0, m_availableAtoms.size());
		int position = ri.pick();
		Atom a = m_availableAtoms.get(position);
		m_availableAtoms.remove(position);
		return a;
	}

	@Override
	public void reset() 
	{
		m_availableAtoms.clear();
		m_availableAtoms.addAll(m_atoms);
	}
	
	protected static Atom[] getAtoms(int num_atoms)
	{
		Atom[] atoms = new Atom[num_atoms];
		for (int i = 0; i < num_atoms; i++)
		{
			atoms[i] = new Atom(s_alphabet[i]);
		}
		return atoms;
	}
}
