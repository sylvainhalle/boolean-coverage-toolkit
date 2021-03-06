<?php
$letters = array("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q");

function renumber($s)
{
  $var_names = array();
  $s = preg_replace("/v([\\s\\)])/", "v0$1", $s);
  $s = preg_replace("/c([\\s\\)])/", "c0$1", $s);
  $s = preg_replace("/l([\\s\\)])/", "l0$1", $s);
  preg_match_all("/Bv\\d*/ms", $s, $matches, PREG_SET_ORDER);
  foreach ($matches as $match)
  {
  	  $predicate = $match[0];
  	  add_to($var_names, $predicate);
  }
  preg_match_all("/[A-Za-z0-9]+ [=><\\/]+ [A-Za-z0-9]+/ms", $s, $matches, PREG_SET_ORDER);
  foreach ($matches as $match)
  {
  	  $predicate = $match[0];
  	  add_to($var_names, $predicate);
  }
  foreach ($var_names as $condition => $var)
  {
  	  $s = str_replace($condition, $var, $s);
  }
  echo $s;
  //print_r($var_names);
}

function add_to(&$array, $string)
{
  global $letters;
  foreach ($array as $condition => $var)
  {
  	  if ($string === $condition)
  	  {
  	  	  return;
  	  }
  }
  $index = count($array);
  $new_var = $letters[$index];
  $array[$string] = $new_var;
}

$expr = <<<EOD
((Ev = El) and (Ev2 = El2)) or ((Ev = El3) and (Ev2 = El4)) or ((Ev = El5) and (Ev2 = El6)) or ((Ev = El7) and (Ev2 = El8)) or ((Ev = El9) and (Ev2 = El10)) or ((Ev = El11) and (Ev2 = El12)) or (Ev3 = Ev2)
EOD;

renumber($expr);

?>