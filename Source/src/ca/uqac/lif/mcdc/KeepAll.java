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

/**
 * The identity transformation.
 */
public class KeepAll implements Truncation
{
	/**
	 * A single publicly visible instance of the class.
	 */
	public static final KeepAll instance = new KeepAll();
	
	/**
	 * Creates a new instance of the transformation.
	 */
	protected KeepAll()
	{
		super();
	}
	
	@Override
	public HologramNode applyTo(HologramNode n)
	{
		return n;
	}

	@Override
	public String toMathML()
	{
		return "<msub><mi>&tau;</mi><mi>I</mi></msub>";
	}
}
