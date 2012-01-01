header {
/*
 * Copyright (c) 2004-2005 Universite de Nantes (LINA)
 * Copyright (c) 2005-2006 France Telecom
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Pierre-Charles David <pcdavid@gmail.com>
 */
package org.objectweb.wildcat.expressions;

import java.util.*;
import org.objectweb.wildcat.Context;
import org.objectweb.wildcat.Path;
import org.objectweb.wildcat.expressions.functions.Function;
}

class ExpressionsLexer extends Lexer;
options { k = 3; defaultErrorHandler=false; }

tokens {
    AND                     = "and";
    OR                      = "or";
    DIV                     = "div";
}

NAME     : LETTER (NAME_CHAR)*;
NUMBER   : (DIGIT)+ (DOT (DIGIT)* )?;
STRING   : DQ_STRING | SQ_STRING;
PLUS     : '+';
MINUS    : '-';
STAR     : '*';
SLASH    : '/';
EQUAL    : "==";
NEQUAL   : "!=";
LESS     : '<';
GREATER  : '>';
LEQ      : "<=";
GEQ      : ">=";
COMMA    : ',';
LPAREN   : '(';
RPAREN   : ')';
DOT      : '.';
DOLLAR   : '$';
SHARP    : '#';

// Whitespace -- ignored
WS	:	( ' ' | '\t' | '\f'
		| (options {generateAmbigWarnings=false;} : "\r\n" | '\r' | '\n' ) { newline(); }
		)+
		{ $setType(Token.SKIP); }
	;

// Single-line comments
SL_COMMENT
	:	"//"
		(~('\n'|'\r'))* ('\n'|'\r'('\n')?)
		{$setType(Token.SKIP); newline();}
	;

protected SQ_STRING: ('\'' (ESC | ~('\\'|'\''))* '\'');
protected DQ_STRING: ('"'  (ESC | ~('\\'|'"' ))* '"' );
protected ESC: '\\' ('n' | 'r' | 't' | '\'' | '"');
protected DIGIT: ('0'..'9');
protected NAME_CHAR: (LETTER | DIGIT | DOLLAR | DOT | MINUS);
protected LETTER :   '\u0041'..'\u005a'
    | '\u005f'
    | '\u0061'..'\u007a'
    | '\u00c0'..'\u00d6'
    | '\u00d8'..'\u00f6'
    | '\u00f8'..'\u00ff'
    | '\u0100'..'\u1fff'
    | '\u3040'..'\u318f'
    | '\u3300'..'\u337f'
    | '\u3400'..'\u3d2d'
    | '\u4e00'..'\u9fff'
    | '\uf900'..'\ufaff'
    ;

class ExpressionsParser extends Parser;
options {
	k = 3;
	importVocab=ExpressionsLexer;
	defaultErrorHandler=false; 
}
{
	private String unquote(String str) {
		str = str.substring(1, str.length() - 1); // removes leadding & ending quotes
		str = str.replaceAll("\\\\'", "'");       // unquotes quotes single-quotes
		str = str.replaceAll("\\\\\"", "\"");     // unquotes quotes double-quotes
		return str;
	}
}

/**
 * Top-level production. Alias for orExpression.
 */
expression returns [ Expression expr ]
{ expr = null; }
    : expr=orExpression
    ;

orExpression returns [ Expression result ]
{
    result = null;
    List clauses = null;
    Expression e1 = null, e2 = null;
}
    :        e1=andExpression { result = e1; }
        ( OR e2=andExpression {
                                  if (clauses == null) {
                                      clauses = new ArrayList();
                                      clauses.add(e1);
                                  }
                                  clauses.add(e2);
                              }
        )*
        {
            if (clauses != null) {
            	java.util.Collections.reverse(clauses);
                result = new OrExpression((Expression[]) clauses.toArray(new Expression[clauses.size()]));
            }
        }
    ;

andExpression returns [ Expression result ]
{
    result = null;
    List clauses = null;
    Expression e1 = null, e2 = null;
}
    :         e1=compExpression { result = e1; }
        ( AND e2=compExpression {
                                    if (clauses == null) {
                                        clauses = new ArrayList();
                                        clauses.add(e1);
                                    }
                                    clauses.add(e2);
                                }
        )*
        {
            if (clauses != null) {
            	java.util.Collections.reverse(clauses);
                result = new AndExpression((Expression[]) clauses.toArray(new Expression[clauses.size()]));
            }
        }
    ;

compExpression returns [ Expression result ]
{
	result = null;
	Expression right = null;
}
    : result=plusExpression
        ( EQUAL   right=plusExpression
          { result = new CallExpression("equals", result, right); }
        | NEQUAL  right=plusExpression
          { result = new CallExpression("not", new CallExpression("equals", result, right)); }
        | LESS    right=plusExpression
          { result = new CallExpression("less", result, right); }
        | GREATER right=plusExpression
          { result = new CallExpression("not", new OrExpression(new CallExpression("less", result, right),
          	                                                    new CallExpression("equals", result, right))); }
        | LEQ     right=plusExpression
          { result = new OrExpression(new CallExpression("less", result, right),
          	                          new CallExpression("equals", result, right)); }
        | GEQ     right=plusExpression
          { result = new CallExpression("not", new CallExpression("less", result, right)); }
        )*
    ;

plusExpression returns [ Expression result ]
{
	result = null;
	Expression right = null;
}
    : result=multiplyExpression
        ( PLUS  right=multiplyExpression
          { result = new CallExpression("add", result, right); }
        | MINUS right=multiplyExpression
          { result = new CallExpression("substract", result, right); }
        )*
    ;

multiplyExpression returns [ Expression result ]
{
	result = null;
	Expression right = null;
}
    : result=unaryExpression
        ( STAR right=unaryExpression
          { result = new CallExpression("multiply", result, right); }
        | DIV  right=unaryExpression
          { result = new CallExpression("divide", result, right); }
        )*
    ;

unaryExpression returns [ Expression result ]
{ result = null; }
    : result=atom
    | MINUS result=atom { result = new CallExpression("substract", new ConstantExpression(0), result); }
    ;

atom returns [ Expression result ]
{ result = null; }
    : result=literal
    | result=variable
    | result=paren
    | result=function
    | result=ctxpath
    ;

// Literal numbers and strings
literal returns [ Expression result ]
{ result = null; }
    : n:NUMBER { result = new ConstantExpression(Double.valueOf(n.getText())); }
    | s:STRING { result = new ConstantExpression(unquote(s.getText())); }
    ;

/**
 * $varName => (variable "varName")
 */
variable returns [ Expression result ]
{ result = null; }
    : DOLLAR n:NAME
     { result = new VariableExpression(n.getText()); }
    ;

/**
 * ( e ) => e
 */
paren returns [ Expression result ]
{ result = null; }
    : LPAREN result=expression RPAREN
    ;
    
/**
 * f(a1, a2 ...) => (call "f" a1 a2 ...)
 */
function returns [ Expression result ]
{
	result = null;
	List args = null;
}
    : func:NAME LPAREN (args=arguments)? RPAREN
        {
            result = new CallExpression(func.getText(), (Expression[]) args.toArray(new Expression[args.size()]));
        }
    ;

arguments returns [ List args ]
{
	args = new ArrayList();
	Expression e1 = null, e2 = null;
}
    : e1=expression { args.add(e1); } (COMMA e2=expression { args.add(e2); })*;

/**
 * /steps/to/resource#attribute
 */
ctxpath returns [ Expression result ]
{
	result = null;
	StringBuilder str = new StringBuilder();
}
    :
     ( SLASH step:NAME { str.append("/").append(step.getText()); } )*
       SHARP attr:NAME { str.append("#").append(attr.getText()); }
       {
       	   Path path = Context.createPath(str.toString());
           result = new CallExpression("attribute", new ConstantExpression(path), new VariableExpression("_context"));
       }
    ;
