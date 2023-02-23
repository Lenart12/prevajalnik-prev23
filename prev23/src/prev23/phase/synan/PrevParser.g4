parser grammar PrevParser;

@header {

	package prev23.phase.synan;
	
	import java.util.*;
	
	import prev23.common.report.*;
	import prev23.phase.lexan.*;
	
}

options{
    tokenVocab=PrevLexer;
}


source
  : EOF
  ;

