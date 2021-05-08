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

import ca.uqac.lif.mcdc.Atom;
import ca.uqac.lif.mcdc.Operator;

public abstract class FormulaBenchmark
{
	protected static final Atom a = new Atom("a");
	protected static final Atom b = new Atom("b");
	protected static final Atom c = new Atom("c");
	protected static final Atom d = new Atom("d");
	protected static final Atom e = new Atom("e");
	protected static final Atom f = new Atom("f");
	protected static final Atom g = new Atom("g");
	protected static final Atom h = new Atom("h");
	protected static final Atom i = new Atom("i");
	protected static final Atom j = new Atom("j");
	protected static final Atom k = new Atom("k");
	protected static final Atom l = new Atom("l");
	protected static final Atom m = new Atom("m");
	protected static final Atom n = new Atom("n");
	
	public abstract Operator getFormula(int id);
}
