//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";



package parser;



//#line 3 "gramaticat.y"

  import lexicalAnalyzer.LexicalAnalyzer;
  import structures.Element;
  import structures.Terceto;
  import structures.TercetoAsignacion;
  import structures.TercetoComparador;
  import structures.TercetoConversion;
  import structures.TercetoLabel;
  import structures.TercetoDivision;
  import structures.TercetoPrint;
  import matrix.AssignMatrix;
  import matrix.DivisionMatrix;
  import matrix.OperationMatrix;
  import matrix.TercetoMatrix;
  import structures.TercetoMultiplicacion;
  import structures.TercetoDecremento;
  import structures.TercetoResta;
  import structures.TercetoSuma;
  import structures.Token;
  import structures.TokenMatrix;
  import interfaz.UI2;
  import java.util.ArrayList;
  import java.util.Vector;
  import java.util.Stack;
  import structures.TercetoBFalse;
  import structures.TercetoBInconditional;
  import structures.TercetoSimple;
  import structures.TercetoBase;
  import java.util.HashMap;
  import structures.TercetoReferencia;
  import structures.AuxGenerator;


  
//#line 52 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short COMENTARIO=257;
public final static short IDENTIFICADOR=258;
public final static short PALABRARESERVADA=259;
public final static short MENORIGUAL=260;
public final static short MAYORIGUAL=261;
public final static short DISTINTO=262;
public final static short ASIGNACION=263;
public final static short DECREMENTO=264;
public final static short CADENA=265;
public final static short FLOAT=266;
public final static short INTEGER=267;
public final static short IF=268;
public final static short ENDIF=269;
public final static short FOR=270;
public final static short PRINT=271;
public final static short CTEINTEGER=272;
public final static short CTEFLOAT=273;
public final static short MATRIX=274;
public final static short ANOT0=275;
public final static short ANOT1=276;
public final static short ALLOW=277;
public final static short TO=278;
public final static short ELSE=279;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    1,    2,    2,    2,    2,    2,    2,
    6,    5,    5,    5,    5,    9,    9,    8,    8,   11,
   11,   11,   12,   12,    4,    7,    7,    7,   14,   14,
   14,   18,   15,   15,   19,   17,   20,   21,   17,   22,
   22,   23,   24,   28,   28,   28,   28,   28,   28,   28,
   28,   29,   29,   29,   16,   16,   16,    3,   30,   30,
   31,   31,   26,   26,   26,   32,   32,   32,   32,   33,
   33,   13,   13,   25,   25,   34,   27,   27,   27,   27,
   27,   27,   10,
};
final static short yylen[] = {                            2,
    2,    2,    1,    1,    1,    2,    2,    1,    1,    2,
    5,   10,   11,   10,    4,    5,    6,    1,    1,    2,
    3,    2,    1,    3,    3,    1,    1,    1,    1,    3,
    1,    0,    4,    2,    0,    4,    0,    0,    7,    8,
    4,    4,    4,    1,    1,    1,    3,    5,    3,    5,
    1,    4,    4,    3,    5,    3,    4,    1,    3,    1,
    2,    1,    1,    3,    3,    1,    2,    3,    3,    1,
    1,    1,    1,    1,    1,    7,    1,    1,    1,    1,
    1,    1,    1,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,   27,   26,    0,    0,    0,    0,
    0,    1,    0,    4,    5,    8,    9,    0,   44,   45,
    0,   60,   46,   58,   75,    0,   34,    0,   32,    0,
    0,    0,   28,    0,   51,    0,    0,    2,    6,    7,
   10,   31,    0,    0,    0,    0,   83,    0,    0,   72,
   73,   70,   71,    0,    0,   66,    0,    0,    0,    0,
    0,    0,   49,    0,    0,   61,   59,    0,    0,    0,
   25,    0,   47,   54,    0,    0,    0,    0,   67,    0,
    0,   56,   79,   80,   81,   77,   78,   82,    0,    0,
   33,   41,    0,    0,    0,    0,    0,   30,   15,    0,
   53,   52,    0,    0,    0,   68,   69,   57,    0,   35,
    0,    0,    0,    0,   50,   48,   11,    0,    0,   55,
    0,    0,   42,    0,    0,    0,   76,   36,    0,    0,
   43,    0,   38,   40,    0,    0,    0,   39,    0,   14,
    0,   18,   19,   13,    0,    0,    0,    0,   23,   22,
    0,    0,   20,    0,    0,   21,   24,   17,
};
final static short yydgoto[] = {                          2,
   12,   13,   14,   15,   16,   17,   18,  144,  140,   49,
  147,  148,   52,   45,   19,   29,   91,   59,  121,  111,
  136,   20,   62,   94,   53,   54,   89,   22,   23,   24,
   37,   55,   56,   25,
};
final static short yysindex[] = {                      -218,
 -108,    0,    0,  -43,    0,    0,  -40,   42,   -8, -187,
  -88,    0, -108,    0,    0,    0,    0, -199,    0,    0,
 -125,    0,    0,    0,    0, -169,    0, -182,    0, -175,
   -6, -179,    0, -161,    0,  -88,  -26,    0,    0,    0,
    0,    0,   76, -114,   64, -169,    0,   77, -171,    0,
    0,    0,    0,    7,  -36,    0,  120,  -18, -104,  124,
  -87, -169,    0,  134, -187,    0,    0, -107,  118,   87,
    0,   66,    0,    0,   67,   88, -169, -169,    0, -169,
 -169,    0,    0,    0,    0,    0,    0,    0, -151,  -85,
    0,    0, -169,  -73,  -18,  -46,  127,    0,    0,  -84,
    0,    0, -169,  -36,  -36,    0,    0,    0,   92,    0,
  -92,   71,  -74, -169,    0,    0,    0,   98,   13,    0,
  133, -104,    0,  152,   84,  103,    0,    0,  -72, -104,
    0,  -77,    0,    0,  105,  137,  -51,    0, -183,    0,
   78,    0,    0,    0, -195,  140,  -99,   29,    0,    0,
  141,   69,    0, -127, -183,    0,    0,    0,
};
final static short yyrindex[] = {                         0,
    0,    0,    2,  -38,    0,    0,    0,    0,    0,    0,
    0,    0,  202,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,   80,    0,    0,    0,    0,
    0,    0,  144,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,  -31,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -75,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  -25,    4,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    1,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   17,    0,    0,    0,
};
final static short yygindex[] = {                         0,
    0,    0,   25,  193,  194,  195,   31,   54,    0,  -22,
    0,   68,  -13,  143,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   59,    5,  119,   35,    0,    0,
  177,   79,   91,    0,
};
final static int YYTABLESIZE=294;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         28,
   12,   51,   74,   74,   74,   80,   74,  139,   74,   63,
   81,   63,  116,   63,   11,   64,   16,   64,   11,   64,
   74,   74,   74,   74,   77,  151,   78,   63,   63,   63,
   63,   32,   58,   64,   64,   64,   64,   38,   93,    1,
   34,   87,   88,   86,   65,   36,   65,   26,   65,   77,
   72,   78,   63,   75,   74,   77,   42,   78,   43,   21,
  146,   63,   65,   65,   65,   65,   95,   64,   33,   21,
   36,   21,  154,   57,   44,    4,   50,   51,    5,    6,
   60,   30,   61,   90,   74,   64,    4,  153,    4,   50,
   51,  142,  143,  109,   21,   97,   65,  112,   67,   76,
   50,   51,   50,   51,  108,  127,    4,  119,   77,   77,
   78,   78,  154,   77,  141,   78,   65,   21,  125,   68,
   50,   51,   71,   12,  101,  102,   77,  156,   78,  123,
   46,  149,  120,  149,   77,   73,   78,   47,   48,   16,
  157,   69,  131,   70,   50,   51,  129,    3,   42,    4,
   43,   35,  113,    4,  134,  104,  105,    5,    6,    7,
   82,    8,    9,    7,   92,    8,    9,   35,   10,    4,
  106,  107,   50,   51,   96,   47,   99,  100,  103,    7,
   21,    8,    9,  110,    4,  117,  122,  118,   21,  124,
  126,  128,  130,  132,  135,  138,  133,  137,  150,  155,
  145,    3,   29,   37,   62,   39,   40,   41,  158,  115,
   98,   47,   66,  114,  152,   27,    0,   74,    0,    0,
    0,   74,   74,   74,   74,   74,    0,   79,   63,   63,
   63,    0,    0,    0,   64,   64,   64,    0,    0,    0,
    0,   83,   84,   85,    0,    0,    0,   31,    0,    0,
    0,    0,    0,    0,    0,    0,   12,   28,   12,   28,
    0,    0,    0,   65,   65,   65,   12,   12,   12,    0,
   12,   12,   16,    0,   16,   28,    0,   12,    0,    0,
    0,    0,   16,   16,   16,    0,   16,   16,    0,    0,
    0,    0,    0,   16,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         40,
    0,    0,   41,   42,   43,   42,   45,   59,   47,   41,
   47,   43,   59,   45,  123,   41,    0,   43,  123,   45,
   59,   60,   61,   62,   43,  125,   45,   59,   60,   61,
   62,   40,   28,   59,   60,   61,   62,   13,   61,  258,
   10,   60,   61,   62,   41,   11,   43,   91,   45,   43,
   46,   45,   59,   49,   93,   43,  256,   45,  258,    1,
  256,   93,   59,   60,   61,   62,   62,   93,  256,   11,
   36,   13,   44,  256,  274,  258,  272,  273,  266,  267,
  256,   40,  258,   59,  256,  265,  258,   59,  258,  272,
  273,  275,  276,   89,   36,   65,   93,   93,  125,   93,
  272,  273,  272,  273,  256,   93,  258,  103,   43,   43,
   45,   45,   44,   43,  137,   45,  278,   59,  114,   44,
  272,  273,   59,  123,   59,   59,   43,   59,   45,   59,
  256,  145,   41,  147,   43,   59,   45,  263,  264,  123,
  154,  256,   59,  258,  272,  273,  122,  256,  256,  258,
  258,  256,   94,  258,  130,   77,   78,  266,  267,  268,
   41,  270,  271,  268,   41,  270,  271,  256,  277,  258,
   80,   81,  272,  273,   41,  263,   59,   91,   91,  268,
  122,  270,  271,  269,  258,   59,  279,  272,  130,  264,
   93,   59,   41,   91,  272,   59,  269,   93,   59,   59,
  123,    0,   59,  279,  125,   13,   13,   13,  155,  256,
   68,  263,   36,   95,  147,  256,   -1,  256,   -1,   -1,
   -1,  260,  261,  262,  263,  264,   -1,  264,  260,  261,
  262,   -1,   -1,   -1,  260,  261,  262,   -1,   -1,   -1,
   -1,  260,  261,  262,   -1,   -1,   -1,  256,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,  256,  256,  258,  258,
   -1,   -1,   -1,  260,  261,  262,  266,  267,  268,   -1,
  270,  271,  256,   -1,  258,  274,   -1,  277,   -1,   -1,
   -1,   -1,  266,  267,  268,   -1,  270,  271,   -1,   -1,
   -1,   -1,   -1,  277,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=279;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"COMENTARIO","IDENTIFICADOR",
"PALABRARESERVADA","MENORIGUAL","MAYORIGUAL","DISTINTO","ASIGNACION",
"DECREMENTO","CADENA","FLOAT","INTEGER","IF","ENDIF","FOR","PRINT","CTEINTEGER",
"CTEFLOAT","MATRIX","ANOT0","ANOT1","ALLOW","TO","ELSE",
};
final static String yyrule[] = {
"$accept : inicio",
"inicio : IDENTIFICADOR programa",
"programa : sentencia_declarativa bloque_sentencias",
"programa : sentencia_declarativa",
"programa : bloque_sentencias",
"sentencia_declarativa : sentencia_declarativa_datos",
"sentencia_declarativa : sentencia_declarativa sentencia_declarativa_datos",
"sentencia_declarativa : sentencia_declarativa sentencia_declarativa_matrix",
"sentencia_declarativa : sentencia_declarativa_matrix",
"sentencia_declarativa : sentencia_declarativa_conversion",
"sentencia_declarativa : sentencia_declarativa sentencia_declarativa_conversion",
"sentencia_declarativa_conversion : ALLOW tipo TO tipo ';'",
"sentencia_declarativa_matrix : tipo MATRIX IDENTIFICADOR '[' CTEINTEGER ']' '[' CTEINTEGER ']' ';'",
"sentencia_declarativa_matrix : tipo MATRIX IDENTIFICADOR '[' CTEINTEGER ']' '[' CTEINTEGER ']' ';' anotacion_matrix",
"sentencia_declarativa_matrix : tipo MATRIX IDENTIFICADOR '[' CTEINTEGER ']' '[' CTEINTEGER ']' inicializacion_matrix",
"sentencia_declarativa_matrix : tipo MATRIX error ';'",
"inicializacion_matrix : operador_asignacion '{' lista_valores_matrix '}' ';'",
"inicializacion_matrix : operador_asignacion '{' lista_valores_matrix '}' ';' anotacion_matrix",
"anotacion_matrix : ANOT0",
"anotacion_matrix : ANOT1",
"lista_valores_matrix : fila_valores_matrix ';'",
"lista_valores_matrix : lista_valores_matrix fila_valores_matrix ';'",
"lista_valores_matrix : error ';'",
"fila_valores_matrix : constante",
"fila_valores_matrix : fila_valores_matrix ',' constante",
"sentencia_declarativa_datos : tipo lista_variables ';'",
"tipo : INTEGER",
"tipo : FLOAT",
"tipo : error",
"lista_variables : IDENTIFICADOR",
"lista_variables : IDENTIFICADOR ',' lista_variables",
"lista_variables : error",
"$$1 :",
"inicio_IF : IF cond $$1 cuerpo_IF",
"inicio_IF : IF error",
"$$2 :",
"cuerpo_IF : bloque_sentencias ENDIF $$2 ';'",
"$$3 :",
"$$4 :",
"cuerpo_IF : bloque_sentencias $$3 ELSE bloque_sentencias ENDIF $$4 ';'",
"inicio_For : FOR '(' asig_for cond_for variable DECREMENTO ')' bloque_sentencias",
"inicio_For : FOR '(' error ')'",
"asig_for : IDENTIFICADOR operador_asignacion expresion ';'",
"cond_for : expresion comparador expresion ';'",
"sentencia_ejecutable : inicio_IF",
"sentencia_ejecutable : inicio_For",
"sentencia_ejecutable : asignacion",
"sentencia_ejecutable : variable DECREMENTO ';'",
"sentencia_ejecutable : PRINT '(' CADENA ')' ';'",
"sentencia_ejecutable : PRINT error ';'",
"sentencia_ejecutable : PRINT '(' CADENA ')' error",
"sentencia_ejecutable : error",
"asignacion : variable operador_asignacion expresion ';'",
"asignacion : variable error expresion ';'",
"asignacion : variable operador_asignacion error",
"cond : '(' expresion comparador expresion ')'",
"cond : '(' error ')'",
"cond : '(' expresion comparador error",
"bloque_sentencias : bloque_sentencias_ejecutable",
"bloque_sentencias_ejecutable : '{' grupo_sentencias '}'",
"bloque_sentencias_ejecutable : sentencia_ejecutable",
"grupo_sentencias : sentencia_ejecutable grupo_sentencias",
"grupo_sentencias : sentencia_ejecutable",
"expresion : termino",
"expresion : expresion '+' termino",
"expresion : expresion '-' termino",
"termino : factor",
"termino : termino DECREMENTO",
"termino : termino '*' factor",
"termino : termino '/' factor",
"factor : constante",
"factor : variable",
"constante : CTEINTEGER",
"constante : CTEFLOAT",
"variable : IDENTIFICADOR",
"variable : valor_matrix",
"valor_matrix : IDENTIFICADOR '[' expresion ']' '[' expresion ']'",
"comparador : '>'",
"comparador : '<'",
"comparador : MENORIGUAL",
"comparador : MAYORIGUAL",
"comparador : DISTINTO",
"comparador : '='",
"operador_asignacion : ASIGNACION",
};

//#line 907 "gramaticat.y"

          /**
          *
          *COMIENZO CODIGO AGREGADO POR NOSOTROS
          *
          */

          private LexicalAnalyzer lexAn;
          private ArrayList<String> errores;
          private ArrayList<String> estructuras;
          private int printLine=0;
          private Stack<Integer> numberLine;
          private ArrayList<Terceto> tercetos;
          private Stack<Terceto> stack;
          private AssignMatrix convertionMatrix;
          private DivisionMatrix divisionMatrix;
          private OperationMatrix operationMatrix;
          private static AuxGenerator generator;
          private HashMap<Object, Object> valoresMatriz=new HashMap<Object, Object>();


          private int annotation;


          private void yyerror(String string) {
            System.out.println("Error en elparseo de la sentencia:"+string);
            // TODO Auto-generated method stub

          }
          public void run(){
            parse();
          }

          public void parse(){
            yyparse();
            showTercetos();
          }

          public LexicalAnalyzer getLexicalAnalizer(){
            return lexAn;
          }

          public ArrayList<String> getErrores() {
            return errores;
          }

          public void showTercetos(){
            System.out.println("------TERCETOS------");
            System.out.println(this.tercetos.toString());
          }
          public ArrayList<String> getEstructuras(){
            return estructuras;
          }

          public ArrayList<Terceto> getTercetos() {
            return tercetos;
          }

          /**
          *
          * recorre la lista de tercetos buscando el terceto referencia con el valor cargado para
          * poder luego acceder a la fila indicada o columna
          *
          *
          * @return el valor del terceto referencia para asignarlo a la columna o fila indicada
          */



          private Object getValueReference(){
            return null;
          }


          public void putValuesMatrix(Object key, Object value){
            if(!valoresMatriz.containsKey(key)){
              valoresMatriz.put(key, value);
            }
          }


          /*
          *se asigna el valor de la expresion al identificador indicado
          * leftOp = IDENTIFICADOR rightOp = expresion
          */
          public void assignValue (Element leftOp, Element rightOp){
            System.out.println("== ASSIGN VALUE ==");
            System.out.println(leftOp+"   --------------   "+rightOp);

            //agregar control de tipos para realizar la asignacion
            if ( rightOp == null ) {
              System.out.println("error variable no inicializada");
            } else {
              System.out.println("valor asignado"+rightOp.getValue()+" a la variable "+leftOp.getLexema());
              leftOp.setValue(rightOp.getValue());
            }
            System.out.println("== AV END ==");
          }
		  
          public void assignType(String type, ArrayList<Token> tokens ) {
            Token tk=null;
            System.out.println("===================asignacion de tipos: type "+type);
            for (Token token : tokens) {
              tk = lexAn.getSymbolTable().getToken(token.getLexema());
              System.out.println("===================TOKEN"+tk.toString());
              if ((tk != null) && (tk.hasTypeVariable())){
				  UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". ERROR VARIABLE REDECLARADA"+"\n"); errores.add("Linea: "+lexAn.getLineNumber()+"ERROR VARIABLE REDECLARADA");
                //System.out.println("===================Error Sintactico");
              } else if (tk != null){
                tk.setTypeVariable(type);
                //System.out.println("===================SetType: tipo asignado "+type+" a token "+tk.toString());
              }
            }
          }
		  
          /* metodo para asignar tipos para asignaciones*/
          public String assignTypeVariable(Element left,Element right){
            String leftTypeVariable = left.getTypeVariable();
            String rightTypeVariable = right.getTypeVariable();
            String value = convertionMatrix.getTypeOperation(leftTypeVariable, rightTypeVariable);
            return value;
          }
          public String assignTypeVariable(Element element){

            String value = element.getTypeVariable();
            return value;
          }

          /*metodos paraasginar tipos en operaciones suma, resta, multiplicacion*/

          public String operationTypeVariable(Element left,Element right){
            String leftTypeVariable = left.getTypeVariable();
            String rightTypeVariable = right.getTypeVariable();
            String value = operationMatrix.getTypeOperation(leftTypeVariable, rightTypeVariable);
            return value;
          }

          public String operationTypeVariable(Element element){

            String value = element.getTypeVariable();
            return value;
          }

          /*metodos paraasginar tipos en operaciones division*/

          public String divisionTypeVariable(Element left,Element right){
            String leftTypeVariable = left.getTypeVariable();
            String rightTypeVariable = right.getTypeVariable();
            String value = divisionMatrix.getTypeOperation(leftTypeVariable, rightTypeVariable);
            return value;
          }

          public String divisionTypeVariable(Element element){

            String value = element.getTypeVariable();
            return value;
          }


          public void controlRedefVariables (Token token,Token aux, String prefix){

        	  System.out.println("========================controlRedefVariables");
        	  String lexema = token.getLexema();
        	  System.out.println("Uso que viene de la tabla de simbolos: "+aux);
        	  System.out.println("Uso que viene del token: "+token);
        	  if (token.getLexema().equals(aux.getLexema())){
        		  if (!token.getUse().equals(aux.getUse())) {
        			  System.out.println("========USO DISTINTO "+token.getUse().equals(prefix));
        			  System.out.println("========USO DISTINTO PREFIX "+prefix);
        			  if ( !lexAn.getSymbolTable().containsSymbol(prefix+"@"+lexema) ){
        				  System.out.println("========================USO DISTINTO, FIJATE LOS NOMBRES");
        				  token.setLexema(prefix+"@"+lexema);
        				  lexAn.getSymbolTable().addToken(prefix+"@"+lexema, token);
        				  System.out.println("AAA token lexema: "+token.getLexema());
        			  }
        			  else {
        				  //UI2.addText(UI2.txtDebug, lexAn.getLineNumber()+" VARIABLE REDEFINIDA \n");
						  errores.add(" VARIABLE REDEFINIDA ");
        				  System.out.println("VARIABLE YA DECLARADA==================");
        				  System.out.println("token re declarado: "+token);
        			  }

        		  }
        	  }
          }





          private boolean controlVarNotDeclared (Token token){
            String lexema = token.getLexema();
            if (lexAn.getSymbolTable().containsSymbol(lexema)){
              return (lexAn.getSymbolTable().getToken(lexema).getTypeVariable() == null);
            }
            return true;
          }


          public void changeTokenMatrix (Token token, Object indexStart,Object rows, Object columns){
            String newType = token.getType();
            String newTypeVariable = token.getTypeVariable();
            String newLexema = token.getLexema();
            int newLineNumber = token.getLineNumber();
            int newIndexStart = (Integer) indexStart;
            int newRows = (Integer) rows;
            int newColumns = (Integer) columns;
            System.out.println("Token matrix cambiado");
            Token newToken = new Token (newType, newLineNumber, newLexema, newIndexStart, newRows, newColumns, null);
            newToken.setTypeVariable(newTypeVariable);
            newToken.setUse("mat");
            lexAn.getSymbolTable().addToken(token.getLexema(), newToken);
          }



          /**
          * este metodo se encarga de generar los tercetos necesarios para acceder a un elemento de la matriz
          * calculando la posicion en memoria con la formula (rowIndex-indexStart)*shift+(columnIndex-indexStart)
          *
          * shift = columns - indexStart + 1
          *
          * indexStart se extrae del token de la tabla de simbolos
          * indexStart = lexAn.getSymbolTable().getToken(ide.getLexema()).getIndexStart();
          * @param ide token de la matriz
          * @param rowIndex valor de fila que se desea acceder
          * @param columnIndex valor de columna que se desea acceder
          * @param columns limite de columnas de la matriz en cuestion
          */
          public void makeMatrix(Token ide ,Object rowIndex, Object columnIndex){
            System.out.println("================MakeMatrix===========");

            int bytes = 2;
            //	int i1 =(Integer) rowIndex;
            //	int i2 =(Integer) columnIndex;
            int columns = lexAn.getSymbolTable().getToken(ide.getLexema()).getColumns();
            String typeVariable = lexAn.getSymbolTable().getToken(ide.getLexema()).getTypeVariable();
            System.out.println("type variable :"+typeVariable);
            if (typeVariable.equals("float")){
              bytes = 4;
            }
            System.out.println("indices de la matriz limite filas:"+rowIndex+" Limite de columnas:"+columnIndex);
            System.out.println("token ide"+ide);
            int indexStart = lexAn.getSymbolTable().getToken(ide.getLexema()).getIndexStart();
            System.out.println("makeMatrix, token de la tabla de simbolos"+lexAn.getSymbolTable().getToken(ide.getLexema()));
            int shift = columns-indexStart+1;
            System.out.println("indexStart"+indexStart);

            /**
            *INDICESNUEVOS
            *revisar si es necesario agregar estos token a la tabla de simbolos, mas adelante en la genereacion de assembler
            *puede ser que sea necesario
            *
            */
            Token tokenBytes = new Token ("INTEGER","_i"+String.valueOf(bytes),0,bytes);
            tokenBytes.setTypeVariable("integer");
            addTokenSymbolTable(tokenBytes);
            Token tokenShift = new Token ("INTEGER","_i"+String.valueOf(shift),0,shift);
            tokenShift.setTypeVariable("integer");
            addTokenSymbolTable(tokenShift);
            Token tokenIndexStart = new Token ("INTEGER","_i"+String.valueOf(indexStart),0,indexStart);
            tokenIndexStart.setTypeVariable("integer");
            addTokenSymbolTable(tokenIndexStart);

            Terceto base=new TercetoBase((Token)ide);
            tercetos.add((Terceto)base);
            ((Terceto)base).setPosition(tercetos.size());
            base.setTypeVariable(typeVariable);

            Terceto simpleResta = new TercetoSimple(tokenIndexStart);
            simpleResta.setTypeVariable("integer");
            simpleResta.setUse("SHIFT");
            tercetos.add((Terceto)simpleResta);
            ((Terceto)simpleResta).setPosition(tercetos.size());
            System.out.println("simpleResta"+simpleResta.getPosition());

            // Terceto simpleI1 = new TercetoSimple(rowIndex);
            //  simpleI1.setTypeVariable("integer");
            //  simpleI1.setUse("SHIFT");
            //  tercetos.add((Terceto)simpleI1);
            //              ((Terceto)simpleI1).setPosition(tercetos.size());

            Terceto resta= new TercetoResta(rowIndex,simpleResta);
            resta.setTypeVariable("integer");
            resta.setUse("SHIFT");
            tercetos.add((Terceto)resta);
            ((Terceto)resta).setPosition(tercetos.size());

            Terceto simpleMult  = new TercetoSimple(tokenShift);
            simpleMult.setTypeVariable("integer");
            simpleMult.setUse("SHIFT");
            tercetos.add((Terceto)simpleMult);
            ((Terceto)simpleMult).setPosition(tercetos.size());

            Terceto multi=new TercetoMultiplicacion((Terceto)resta,simpleMult);
            multi.setTypeVariable("integer");
            multi.setUse("SHIFT");
            tercetos.add((Terceto)multi);
            ((Terceto)multi).setPosition(tercetos.size());
            //TokenMatrix auxIde = lexAn.getSymbolTable().getToken(ide.getLexema())
            // int indexStart = lexAn.getSymbolTable().getToken(ide.getLexema()).getIndexStart();

            //  Terceto simpleI2 = new TercetoSimple(columnIndex);
            //  simpleI2.setTypeVariable("integer");
            //  simpleI2.setUse("SHIFT");
            //  tercetos.add((Terceto)simpleI2);
            //            ((Terceto)simpleI2).setPosition(tercetos.size());

            Terceto resta1= new TercetoResta(columnIndex,simpleResta);
            resta1.setTypeVariable("integer");
            resta1.setUse("SHIFT");
            tercetos.add((Terceto)resta1);
            ((Terceto)resta1).setPosition(tercetos.size());

            Terceto suma = new TercetoSuma((Terceto)resta1,(Terceto)multi);
            suma.setTypeVariable("integer");
            suma.setUse("SHIFT");
            tercetos.add((Terceto)suma);
            ((Terceto)suma).setPosition(tercetos.size());

            Terceto simpleBytes = new TercetoSimple(tokenBytes);
            simpleBytes.setTypeVariable("integer");
            simpleBytes.setUse("SHIFT");
            tercetos.add((Terceto)simpleBytes);
            ((Terceto)simpleBytes).setPosition(tercetos.size());

            Terceto multi1=new TercetoMultiplicacion((Terceto)suma,simpleBytes);
            multi1.setTypeVariable("integer");tercetos.add((Terceto)multi1);
            multi1.setUse("SHIFT");
            ((Terceto)multi1).setPosition(tercetos.size());

            Terceto suma1= new TercetoSuma(base,(Terceto)multi1);
            suma1.setTypeVariable("integer");
            suma1.setUse("SHIFT");
            tercetos.add((Terceto)suma1);
            ((Terceto)suma1).setPosition(tercetos.size());
          //  int value = ((i1-indexStart)*shift)+(i2-indexStart);
          //  suma1.setValue(value);

            Terceto ref= new TercetoReferencia(suma1,lexAn.getSymbolTable().getToken(ide.getLexema()));
            tercetos.add((Terceto)ref);
            ref.setUse("mat");
            ref.setTypeVariable(typeVariable);
            ((Terceto)ref).setPosition(tercetos.size());
            Terceto simple = new TercetoSimple(tercetos.size()+1);
            //tercetos.add((Terceto) simple);
          }



          /**
          * crea los tercetos necesarios para la inicializacion de la matriz usando MakeMatrix para calcular la posicion de memoria de cada elemento de la matriz
          *
          * al final se elimina un terceto simple usado para guardar la posicion del terceto que contiene la posicion de memoria del elemento de la matriz
          * @param listaValores arreglo generado en la lista de valores de matriz, la cantidad de elementos debera ser igual al producto de los limtes de la matriz
          * @param indexStart es el parametro annotation de la gramatica
          * @param ide token de la matrz
          * @param rowIndex cantidad de filas de la declaracion de la matriz
          * @param columnIndex cantidad de filas de la declaracion de la matriz
          */
          public void initMatrix (ArrayList<Token> listaValores,Object indexStart, Token ide, Object rowIndex , Object columnIndex ){
            int index = (Integer) indexStart;
            System.out.println("=================initMatrix========");
            System.out.println("row index value:"+rowIndex);
            int i1 = (Integer) rowIndex;
            int i2 = (Integer) columnIndex;
            int rowi = (Integer) indexStart;
            int columnj = (Integer) indexStart;
            System.out.println("indices de la matriz limite filas:"+i1+" Limite de columnas:"+i2);
            System.out.println("inicio de la matriz filas:"+rowi+" inicio de las columnas:"+columnj);
            if (  (index == 0 && ((i1+1)*(i2+1)) == listaValores.size()) || (index == 1 && (i1*i2) == listaValores.size()) ) {
              System.out.println("=======inicio del primer for");
              int i;
              int shift = i2-index+1;
              //System.out.println("============== for por fila, fila numero:"+rowi);
              for (rowi = (Integer) index; rowi <= i1; rowi=rowi+1) {
                System.out.println("============== for por fila, fila numero:"+rowi);

                for (columnj = (Integer) index; columnj <= i2; columnj=columnj+1) {
                  System.out.println("=====================for por columnas, columna numero:"+columnj);
                  i = (rowi-index)*shift+(columnj-index);

                  /**
                  *INDICESNUEVOS
                  *revisar si es necesario agregar estos token a la tabla de simbolos, mas adelante en la genereacion de assembler
                  *puede ser que sea necesario
                  *
                  */

                  Token tokenRowi = new Token ("INTEGER","_i"+String.valueOf(rowi),0,rowi);
                  tokenRowi.setTypeVariable("integer");
                  addTokenSymbolTable(tokenRowi);
                  Token tokenColumnj = new Token ("INTEGER","_i"+String.valueOf(columnj),0,columnj);
                  tokenColumnj.setTypeVariable("integer");
                  addTokenSymbolTable(tokenColumnj);
                  System.out.println("            fila: "+tokenRowi+" columna: "+tokenColumnj);
                  System.out.println("indice del arreglo del elemento a recuperar:"+i+" Elemento recuperado"+listaValores.get(i));
                  makeMatrix(ide, tokenRowi, tokenColumnj);
                  System.out.println("columns en for decolumnas"+columnj);

                  Terceto simpleAssign  = new TercetoSimple(tercetos.size()-1);
                  simpleAssign.setTypeVariable("integer");
                  //OLD: simpleAssign, NEW tercetos.get(tercetos.size()-2)aca cambiamos el segundo parametro del token asignacion para que cuando incializamos al matriz aparezca correctamente
                  //aca era -2 antes
                  TercetoAsignacion assign = new TercetoAsignacion(tercetos.get(tercetos.size()-1),listaValores.get(i));
                  System.out.println("tercetos suma???"+tercetos.get(tercetos.size()-2)+"  value:"+tercetos.get(tercetos.size()-2).getValue()+" valor (i)"+listaValores.get(i).getValue());
                  putValuesMatrix(tercetos.get(tercetos.size()-2).getValue(), listaValores.get(i).getValue());
                  assignValue(tercetos.get(tercetos.size()-1), listaValores.get(i));
                  assign.setTypeVariable(listaValores.get(i).getTypeVariable());
                  System.out.println("aall muuundoooooooooo  terceto asignacion creado"+assign);
                  //tercetos.remove(tercetos.size()-1);
                  //tercetos.add(simpleAssign);
                  simpleAssign.setPosition(tercetos.size());
                  tercetos.add(assign);
                  assign.setPosition(tercetos.size());

                }
              }

            } else {   System.out.println("Error en la cantidad de elementos declarados al inicializar la matriz");
					   errores.add("Error en la cantidad de elementos declarados al inicializar la matriz");
                       UI2.addText(UI2.txtDebug, "Error en la cantidad de elementos declarados al inicializar la matriz");
					}
          }



          public void addTokenSymbolTable (Token token){
            System.out.println("metodo para agreagar a la tabla de simbolos"+token);
            if ( !lexAn.getSymbolTable().containsSymbol(token.getLexema()) ){
                System.out.println("token agreagado");
                lexAn.getSymbolTable().addToken(token.getLexema(),token);
              }
            }

          /**
          * est metodo genera el terceto conversion en caso de ser necesario
          * se le pasan los dos operandos involucrados en la operacion y se retorna el terceto conversion creado en caso de que la
          * operacion asi lo requiera y el operando de la derecha en caso de que no deba realizar una conversion
          *
          */
          private Element makeConvertion(Element leftOperand, String leftType, String rightType, Element rightOperand, String typeResult){
            System.out.println("==makeConvertion==");
            Element expresion=rightOperand;
            System.out.println("leftOperand"+leftOperand+"  rightOperand"+rightOperand+"   leftType"+leftType+"     rightType"+rightType);
            System.out.println("accept Operation"+convertionMatrix.acceptOperation(leftType,rightType));
            if (convertionMatrix.acceptOperation(leftType,rightType)){
				
              if( (rightOperand.getTypeVariable()!= null) && (!rightOperand.getTypeVariable().equals(leftOperand.getTypeVariable()))){
                System.out.println("tipos distintos creacion terceto conversion");

                typeResult = convertionMatrix.getTypeOperation( leftOperand.getTypeVariable() , rightOperand.getTypeVariable() );
                Terceto conversion = new TercetoConversion(rightOperand, typeResult);
                tercetos.add(conversion);
                expresion = conversion;
                System.out.println("Terceto conv"+conversion);
                (conversion).setPosition(tercetos.size());
				}
				
              }else {   errores.add("No se pueden realizar la operacion: "+leftType+" := "+rightType );
                        UI2.addText(UI2.txtDebug, "No se pueden realizar la operacion: "+leftType+" := "+rightType );
					}
            
            return expresion;
          }


          //yylval = new ParserVal(token);

          private int yylex() {
            System.out.println("ejecuto yylex principio del metodo");
            Token token = lexAn.getToken();

            if (token != null){
              String type = token.getType();
              yylval = new ParserVal(token);

              if (type.equals("IDENTIFICADOR"))
              {
                //yylval = new ParserVal(token.getLexema());
                return IDENTIFICADOR;
              }
              if (type.equals("CADENA"))
              {
                //yylval = new ParserVal (token.getLineNumber());
                printLine = token.getLineNumber();
                //yylval = new ParserVal(token.getLexema());
                return CADENA;
              }
              if (type.equals("ANOT0")){
                return ANOT0;
              }
              if (type.equals("ANOT1")){
                return ANOT1;
              }

              if (type.equals("PALABRARESERVADA")){
                if (token.getLexema().equals("if")){
                  yylval = new ParserVal (token.getLineNumber());
                  numberLine.push(yylval.ival);
                  return IF;
                }
                if (token.getLexema().equals("else")){
                  return ELSE;
                }
                if (token.getLexema().equals("print")){
                  yylval = new ParserVal (token.getLineNumber());
                  printLine = yylval.ival;
                  return PRINT;
                }
                if (token.getLexema().equals("for")){
                  yylval = new ParserVal (token.getLineNumber());
                  numberLine.push(yylval.ival);
                  return FOR;
                }
                if (token.getLexema().equals("endif"))
                {
                  return ENDIF;
                }
                if (token.getLexema().equals("integer"))
                {
                  yylval = new ParserVal (token);
                  return INTEGER;
                }
                if (token.getLexema().equals("float"))
                {
                  return FLOAT;
                }
                if (token.getLexema().equals("matrix"))
                {
                  yylval = new ParserVal (token.getLineNumber());
                  printLine = yylval.ival;

                  return MATRIX;
                }
                if (token.getLexema().equals("allow"))
                {
                  return ALLOW;
                }
                if (token.getLexema().equals("to"))
                {
                  return TO;
                }
              }
              if (type.equals("MAYORIGUAL"))
              {
                return MAYORIGUAL;
              }
              if (type.equals("MENORIGUAL"))
              {
                return MENORIGUAL;
              }
              if (type.equals("COMENTARIO"))
              {
                return COMENTARIO;
              }
              if (type.equals("DISTINTO"))
              {
                return DISTINTO;
              }
              if (type.equals("ASIGNACION"))
              {
                return ASIGNACION;
              }
              if (type.equals("DECREMENTO"))
              {
                return DECREMENTO;
              }
              if (type.equals("INTEGER"))
              {
                //yylval = new ParserVal(token.getLexema());
                return CTEINTEGER;
              }
              if (type.equals("FLOAT"))
              {
                //yylval = new ParserVal(token.getLexema());
                return CTEFLOAT;
              }
              if (type.equals("LITERAL")){
                return lexAn.getLexema().charAt(0);
              }
            }
            return 0;
          }


          public Parser(String sourcePath) {
            lexAn       = new LexicalAnalyzer(sourcePath);
            errores     = new ArrayList<String>();
            estructuras = new ArrayList<String>();
            tercetos    = new ArrayList<Terceto>();
            stack 		= new Stack<Terceto>();
            numberLine  = new Stack<Integer>();
            convertionMatrix = new AssignMatrix();
            operationMatrix = new OperationMatrix();
            divisionMatrix = new DivisionMatrix();
            generator = new AuxGenerator();

          }
//#line 1017 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 44 "gramaticat.y"
{ lexAn.getSymbolTable().getToken(((Token) val_peek(1).obj).getLexema()).setUse("var");
  ((Token) val_peek(1).obj).setUse("nombre de programa");}
break;
case 5:
//#line 52 "gramaticat.y"
{estructuras.add(lexAn.getLineNumber()+". sentencia declarativa de datos\n");}
break;
case 6:
//#line 53 "gramaticat.y"
{estructuras.add(lexAn.getLineNumber()+". sentencia declarativa de datos\n");}
break;
case 7:
//#line 54 "gramaticat.y"
{estructuras.add(printLine+". sentencia declarativa de matrices\n");}
break;
case 8:
//#line 55 "gramaticat.y"
{estructuras.add(printLine+". sentencia declarativa de matrices\n");}
break;
case 9:
//#line 56 "gramaticat.y"
{estructuras.add(lexAn.getLineNumber()+". sentencia declarativa de conversion de tipos\n");}
break;
case 10:
//#line 57 "gramaticat.y"
{estructuras.add(lexAn.getLineNumber()+". sentencia declarativade conversion de tipos\n");}
break;
case 11:
//#line 62 "gramaticat.y"
{convertionMatrix.addConvertion(((Token)val_peek(3).obj).getLexema(), ((Token)val_peek(1).obj).getLexema());}
break;
case 12:
//#line 65 "gramaticat.y"
{ String tipoVariable = ((Token) val_peek(9).obj).getLexema();
                                                                                                      ((Token) val_peek(7).obj).setTypeVariable(tipoVariable);
                                                                                                      ((Token) val_peek(7).obj).setUse("mat");
                                                                                                      Token aux = lexAn.getSymbolTable().getToken(((Token)val_peek(7).obj).getLexema());
                                                                                                      controlRedefVariables(((Token) val_peek(7).obj),aux,"mat");
                                                                                                      annotation = 0;
                                                                                                      changeTokenMatrix (((Token) val_peek(7).obj),annotation,((Element) val_peek(5).obj).getValue(),((Element) val_peek(2).obj).getValue());
                                                                                                      /*TercetoAnotacion anot = new TercetoAnotacion(0);*/
                                                                                                      /*tercetos.add(anot);*/
                                                                                                      /*anot.setPosition(tercetos.size());*/
                                                                                                      /*TercetoMatrix matrix= new TercetoMatrix (((Token) $3.obj).getLexema(),((Token) $5.obj).getValue(),((Token) $8.obj).getValue());*/
                                                                                                      /*tercetos.add(matrix);*/
                                                                                                      /*matrix.setPosition(tercetos.size());*/
                                                                                                    }
break;
case 13:
//#line 80 "gramaticat.y"
{ String tipoVariable = ((Token) val_peek(10).obj).getLexema();
                                                                                           ((Token) val_peek(8).obj).setTypeVariable(tipoVariable);
                                                                                           ((Token) val_peek(8).obj).setUse("mat");
                                                                                           Token aux = lexAn.getSymbolTable().getToken(((Token)val_peek(8).obj).getLexema());
                                                                                           controlRedefVariables(((Token) val_peek(8).obj),aux,"mat");
                                                                                           annotation = (Integer)((Token) val_peek(0).obj).getValue();
                                                                                           System.out.print("declaracion de matrices: annotation"+annotation);
                                                                                           changeTokenMatrix (((Token) val_peek(8).obj),annotation,((Token) val_peek(6).obj).getValue(),((Token) val_peek(3).obj).getValue());

                                                                                         }
break;
case 14:
//#line 91 "gramaticat.y"
{ String tipoVariable = ((Token) val_peek(9).obj).getLexema();
                                                                                            ((Token) val_peek(7).obj).setTypeVariable(tipoVariable);
                                                                                            ((Token) val_peek(7).obj).setUse("mat");
                                                                                            Token aux = lexAn.getSymbolTable().getToken(((Token)val_peek(7).obj).getLexema());
                                                                                            controlRedefVariables(((Token) val_peek(7).obj),aux,"mat");
                                                                                            /*int anot = (Integer)tercetos.get(tercetos.size()-1).getFirst();*/
                                                                                            /*TercetoMatrix matrix= new TercetoMatrix (((Token) $3.obj).getLexema(),((Token) $5.obj).getValue(),((Token) $8.obj).getValue());*/
                                                                                            /*tercetos.add(matrix);*/
                                                                                            /* matrix.setPosition(tercetos.size());*/
                                                                                            System.out.print("declaracion de matrices: annotation"+annotation);
                                                                                            changeTokenMatrix (((Token) val_peek(7).obj),annotation,((Token) val_peek(5).obj).getValue(),((Token) val_peek(2).obj).getValue());
                                                                                            System.out.print("declaracion de matrices ide: token"+((Token) val_peek(7).obj));
                                                                                            initMatrix (((ArrayList<Token>) val_peek(0).obj),annotation, ((Token) val_peek(7).obj), ((Token) val_peek(5).obj).getValue(), ((Token) val_peek(2).obj).getValue() );

                                                                                          }
break;
case 15:
//#line 107 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+printLine+". DECLARACION ERRONEA"+"\n"); errores.add("Linea: "+printLine+" DECLARACION ERRONEA");}
break;
case 16:
//#line 111 "gramaticat.y"
{yyval.obj=new ArrayList<Token>();
                                                                                ((ArrayList<Token>)(yyval.obj)).addAll((ArrayList<Token>)val_peek(2).obj);
                                                                                annotation = 0;
                                                                              }
break;
case 17:
//#line 116 "gramaticat.y"
{yyval.obj=new ArrayList<Token>();
                                                                           ((ArrayList<Token>)(yyval.obj)).addAll((ArrayList<Token>)val_peek(3).obj);
                                                                           annotation = (Integer)((Token)(val_peek(0).obj)).getValue();
                                                                           System.out.print("lista devalores matriz: annotation"+annotation);
                                                                         }
break;
case 18:
//#line 122 "gramaticat.y"
{yyval.obj = ((Token) val_peek(0).obj);}
break;
case 19:
//#line 122 "gramaticat.y"
{yyval.obj = ((Token) val_peek(0).obj);}
break;
case 20:
//#line 124 "gramaticat.y"
{yyval.obj=new ArrayList<Token>();
                                                  ((ArrayList<Token>)(yyval.obj)).addAll((ArrayList<Token>)val_peek(1).obj);}
break;
case 21:
//#line 127 "gramaticat.y"
{yyval.obj=new ArrayList<Token>();
                                                    ((ArrayList<Token>)(yyval.obj)).addAll((ArrayList<Token>)val_peek(2).obj);
                                                    ((ArrayList<Token>)(yyval.obj)).addAll((ArrayList<Token>)val_peek(1).obj);}
break;
case 22:
//#line 131 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+printLine+". ERROR EN LA LISTA DE VARIABLES"+"\n"); errores.add("Linea: "+printLine+"ERROR EN LA LISTA DE VARIABLES"+"\n");}
break;
case 23:
//#line 134 "gramaticat.y"
{yyval.obj=new ArrayList<Token>();
                                       ((ArrayList<Token>)(yyval.obj)).add((Token)val_peek(0).obj);}
break;
case 24:
//#line 137 "gramaticat.y"
{yyval.obj=new ArrayList<Token>();
                                                               ((ArrayList<Token>)(yyval.obj)).addAll((ArrayList<Token>)val_peek(2).obj);
                                                               ((ArrayList<Token>)(yyval.obj)).add((Token)val_peek(0).obj);}
break;
case 25:
//#line 142 "gramaticat.y"
{System.out.println("ejecuto regla de ASSIGN TYPE"+val_peek(1).obj);
																  if ( !val_peek(1).obj.getClass().toString().equals("class structures.Token") ) 
																	assignType(((Token)val_peek(2).obj).getLexema(),(ArrayList<Token>)val_peek(1).obj);}
break;
case 26:
//#line 148 "gramaticat.y"
{yyval.obj = val_peek(0).obj;
                            System.out.println("Problando INTEGER: "+((Token)val_peek(0).obj).getLexema());}
break;
case 27:
//#line 150 "gramaticat.y"
{yyval.obj = val_peek(0).obj;
                            System.out.println("Problando FLOAT: "+((Token)val_peek(0).obj).getLexema());}
break;
case 28:
//#line 152 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". ERROR EN DECLARACION DE VARIABLES "+"\n"); errores.add("Linea: "+lexAn.getLineNumber()+"ERROR EN DECLARACION DE VARIABLES");}
break;
case 29:
//#line 154 "gramaticat.y"
{yyval.obj=new ArrayList<Token>();
                                                 Token aux=lexAn.getSymbolTable().getToken(((Token) val_peek(0).obj).getLexema());
                                                 ((Token) val_peek(0).obj).setUse("var");
                                                 System.out.println("no se que poner en este mensaje"+((Token) val_peek(0).obj).getUse());
                                                 System.out.println("no se que poner en este mensaje"+lexAn.getSymbolTable().getToken(((Token) val_peek(0).obj).getLexema()));
                                                 ((ArrayList<Token>)(yyval.obj)).add((Token)val_peek(0).obj);
                                                 controlRedefVariables(((Token) val_peek(0).obj),aux,"var");
                                                 System.out.println(((Token) val_peek(0).obj));
                                                 System.out.println(" Probando IDENTIFICADOR Lista de variables: ");
                                               }
break;
case 30:
//#line 165 "gramaticat.y"
{yyval.obj=new ArrayList<Token>();
                                                                    Token aux=lexAn.getSymbolTable().getToken(((Token) val_peek(2).obj).getLexema());
                                                                    ((Token) val_peek(2).obj).setUse("var");
                                                                    ((ArrayList<Token>)(val_peek(0).obj)).add((Token)val_peek(2).obj);
                                                                    controlRedefVariables(((Token) val_peek(2).obj),aux,"var");
                                                                    ((ArrayList<Token>)(yyval.obj)).addAll((ArrayList<Token>)val_peek(0).obj);
                                                                    System.out.print("SYSOUT DEL VECTOR "+((ArrayList<Token>)(yyval.obj)).size());}
break;
case 31:
//#line 173 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". ERROR LISTA DEVARIABLES"+"\n"); errores.add("Linea: "+lexAn.getLineNumber()+"ERROR LISTA DEVARIABLES");}
break;
case 32:
//#line 175 "gramaticat.y"
{Terceto bFalse = new TercetoBFalse(tercetos.get(tercetos.size()-1)); /*Terceto comparacion*/
                                      System.out.println("Tamaño del ARREGLO TERCETO EN IF CONDICION: "+tercetos.size());
                                      System.out.println("mostraaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
                                      tercetos.add(bFalse);
                                      System.out.println("El tamaño del arreglo TERCETO en IF es: "+tercetos.size());
                                      bFalse.setPosition((Integer)tercetos.size());
                                      stack.push(bFalse);}
break;
case 34:
//#line 182 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+numberLine.peek()+". ERROR EN if"+"\n"); errores.add("Linea: "+numberLine.peek()+"ERROR EN CONDICION");}
break;
case 35:
//#line 186 "gramaticat.y"
{Terceto bFalse = stack.pop();
                                                          Terceto simple = new TercetoSimple((Integer)tercetos.size()+1);
                                                          simple.setPosition((Integer)tercetos.size()+1);
                                                          tercetos.add(new TercetoLabel((Integer)tercetos.size()+1,(Integer)tercetos.size()+1));
                                                          bFalse.setSecond(simple);}
break;
case 37:
//#line 192 "gramaticat.y"
{	Terceto bInconditional = new TercetoBInconditional();
                                                      tercetos.add(bInconditional);
                                                      bInconditional.setPosition((Integer)tercetos.size());
                                                      tercetos.add(new TercetoLabel((Integer)tercetos.size()+2,(Integer)tercetos.size()+1));/*+2 xq si y creo que anda*/
                                                      bInconditional.setHasLabel(true);
                                                      Terceto bFalse = stack.pop();
                                                      Terceto simple = new TercetoSimple((Integer)tercetos.size()+1);
                                                      simple.setPosition((Integer)tercetos.size()+1);
                                                      System.out.println("El tamaÃ±o del arreglo TERCETO en CUERPO_IF SIMPLE es: "+tercetos.size());
                                                      stack.push(bInconditional);
                                                      bFalse.setSecond(simple);/*Set linea donde termina el then*/
                                                    }
break;
case 38:
//#line 203 "gramaticat.y"
{ Terceto bInconditional = stack.pop();
                                                                                     System.out.println("El tamaño del arreglo TERCETO en CUERPO_IF FINCONDICIONAL es: "+tercetos.size());
                                                                                     Terceto simple = new TercetoSimple((Integer)tercetos.size()+1);
                                                                                     simple.setPosition((Integer)tercetos.size()+1);
                                                                                     bInconditional.setFirst(simple);
                                                                                     bInconditional.setHasLabel(true);
                                                                                     tercetos.add(new TercetoLabel((Integer)tercetos.size()+1,(Integer)tercetos.size()+1));
                                                                                     System.out.println("Finalizando el terceto Incondicional");}
break;
case 40:
//#line 214 "gramaticat.y"
{ System.out.println("====== INICIO FOR ======= ");
                                                                                                      estructuras.add(numberLine.pop()+". sentencia ejecutable for\n");
                                                                                                      /*Terceto varUpdate = new TercetoDecremento((Token)$5.obj);*/
                                                                                                      /*tercetos.add(varUpdate);*/
																									  Token one = new Token ("INTEGER","_i1",0,1);
																									  one.setTypeVariable("integer");
																									  addTokenSymbolTable(one);
																									  
																									  Terceto resta= new TercetoResta((Token)val_peek(3).obj,one);
																									  tercetos.add(resta);
																									  resta.setPosition(tercetos.size());
																									  resta.setTypeVariable("integer");
																									  
																									  Terceto varUpdate = new TercetoAsignacion((Token)val_peek(3).obj,tercetos.get(tercetos.size()-1));
																									  tercetos.add(varUpdate);
																									  varUpdate.setPosition(tercetos.size());
                                                                                                      varUpdate.setTypeVariable("integer");

                                                                                                      Terceto bInconditional = new TercetoBInconditional();
                                                                                                      tercetos.add(bInconditional);
                                                                                                      bInconditional.setPosition(tercetos.size());
                                                                                                      bInconditional.setHasLabel(true);
																									  
                                                                                                      Terceto bFalse = stack.pop();
                                                                                                      System.out.println("terceto size "+tercetos.size());
                                                                                                      tercetos.add(new TercetoLabel((Integer)tercetos.size()+2,(Integer)tercetos.size()+1));
                                                                                                      Terceto simple = new TercetoSimple(tercetos.size()+1);/*+1*/
																									  System.out.println("terceto simple "+bFalse);	 
                                                                                                      bFalse.setSecond(simple);
                                                                                                      simple.setPosition(tercetos.size()+1);
																									  simple = stack.pop();
                                                                                                      /*se cambio la linea siguiente como la posicion del simple para que no falle laposicion a  la uqe se debe volver en el, salto incondicional*/
                                                                                                      /*simple.setPosition(simple.getPosition());*/
                                                                                                      bInconditional.setFirst(simple);
                                                                                                    }
break;
case 41:
//#line 249 "gramaticat.y"
{ System.out.println("tFOR ERROR");}
break;
case 42:
//#line 253 "gramaticat.y"
{System.out.println("isssssss show time");
                                                                              Terceto initFor=null;
                                                                              Token id=lexAn.getSymbolTable().getToken(((Element)val_peek(1).obj).getLexema());
                                                                              System.out.println(id.getUse()+"this es el uso de asgi_for");
                                                                              if ( id.getUse()!= null && id.getUse().equals("mat"))
                                                                                  initFor = new TercetoAsignacion(lexAn.getSymbolTable().getToken(((Element)val_peek(3).obj).getLexema()),tercetos.get(tercetos.size()-1));
                                                                              else {
                                                                                  assignValue((Element)val_peek(3).obj,(Element)val_peek(1).obj);
                                                                                  initFor = new TercetoAsignacion(lexAn.getSymbolTable().getToken(((Element)val_peek(3).obj).getLexema()),(Element)val_peek(1).obj);
                                                                                }
                                                                              tercetos.add(initFor);
                                                                              initFor.setTypeVariable("integer");
                                                                              initFor.setPosition(tercetos.size());
                                                                              /*stack.push(tercetos.get(tercetos.size()-1));*/
                                                                              /*System.out.println("terceto inicio"+stack.peek().toString());*/

                                                                              if ( !lexAn.getSymbolTable().getToken(((Element)val_peek(3).obj).getLexema()).getTypeVariable().equals("integer") ){
                                                                                  UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". ERROR EN LIMITES DE ITERACION FOR: TIPO INCORRECTO. Debe ser integer"+"\n");
                                                                                  errores.add("Linea: "+lexAn.getLineNumber()+"ERROR EN LIMITES DE ITERACION FOR: TIPO INCORRECTO. Debe ser integer");
                                                                                }

                                                                              }
break;
case 43:
//#line 278 "gramaticat.y"
{System.out.println("=== COND FOR ==== ");
                                                                  System.out.println("terceto size "+tercetos.size());
                                                                  tercetos.add(new TercetoLabel((Integer)tercetos.size()+2,(Integer)tercetos.size()+1));
                                                                  if(  !((Element)val_peek(3).obj).getTypeVariable().equals(((Element) val_peek(1).obj).getTypeVariable()) ){
                                                                      String typeResult = operationMatrix.getTypeOperation( ((Element)val_peek(3).obj).getTypeVariable() , ((Element) val_peek(1).obj).getTypeVariable() );
                                                                      Terceto conversion;
                                                                      if (typeResult.equals(((Element)val_peek(3).obj).getTypeVariable())) {
                                                                          conversion = new TercetoConversion(((Element)val_peek(1).obj), typeResult);
                                                                        } else {
                                                                            conversion = new TercetoConversion(((Element)val_peek(3).obj), typeResult);
                                                                          }
                                                                      tercetos.add(conversion);
                                                                      (conversion).setPosition(tercetos.size());
                                                                    }

                                                                    Element eright=(Element)val_peek(3).obj;
                                                                    Element eleft=(Element)val_peek(1).obj;
                                                                    System.out.println("el de la derecha"+eleft.getUse());
                                                                    if (eright.getClassType().equals("Token")    && (eright.getUse()!=null) && (eright.getUse().equals("mat")))
                                                                      eright=tercetos.get(tercetos.size()-2);
                                                                    if (eleft.getClassType().equals("Token")&& (eleft.getUse()!=null) && (eleft.getUse().equals("mat")))
                                                                      eleft=tercetos.get(tercetos.size()-2);

                                                                      Terceto comp = new TercetoComparador((Token)val_peek(2).obj,eright,eleft); /*ANDA*/
                                                                      comp.setTypeVariable(operationTypeVariable((Element)val_peek(3).obj,(Element) val_peek(1).obj));

                                                                      tercetos.add(comp);
                                                                      comp.setPosition(tercetos.size());
                                                                      comp.setHasLabel(true);
                                                                      stack.push(tercetos.get(tercetos.size()-1));
                                                                      /*System.out.println("==================COND FOR==========El tamaño del arreglo TERCETO en CONDICION DEL FOR es: "+tercetos.size());*/
                                                                      Terceto bFalse = new TercetoBFalse(tercetos.get(tercetos.size()-1));
                                                                      tercetos.add(bFalse);
																	  
                                                                      bFalse.setPosition(tercetos.size());
                                                                      stack.push(bFalse);}
break;
case 44:
//#line 321 "gramaticat.y"
{estructuras.add(numberLine.pop()+". sentencia ejecutable if\n");}
break;
case 46:
//#line 331 "gramaticat.y"
{estructuras.add(lexAn.getLineNumber()+". asignacion\n");}
break;
case 47:
//#line 333 "gramaticat.y"
{estructuras.add(lexAn.getLineNumber()+". variable  decremento\n");}
break;
case 48:
//#line 335 "gramaticat.y"
{System.out.println("===CADENA "+ (Element)val_peek(2).obj+" palabra reservada "+(Token)val_peek(4).obj);
                                                                 Token token = lexAn.getSymbolTable().getToken((((Token)val_peek(2).obj).getLexema()));
                                                                 Terceto cadena = new TercetoPrint(token);
                                                                 tercetos.add(cadena);
                                                                 cadena.setPosition(tercetos.size());
                                                                 estructuras.add( printLine+". print\n");
                                                                }
break;
case 49:
//#line 343 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+printLine+". ERROR EN PRINT "+"\n"); errores.add("Linea: "+lexAn.getLineNumber()+"ERROR EN PRINT");}
break;
case 50:
//#line 345 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+printLine+". ERROR EN PRINT FALTA \n"); errores.add("Linea: "+printLine+"ERROR EN PRINT FALTA ;");}
break;
case 51:
//#line 347 "gramaticat.y"
{ UI2.addText(UI2.txtDebug,"Linea: "+printLine+" ERROR EN BLOQUE DE SENTENCIA \n"); errores.add("Linea: "+printLine+"ERROR EN SENTENCIA EJECUTABLE");}
break;
case 52:
//#line 350 "gramaticat.y"
{System.out.println("==ASIGNACION==");
                                                                          System.out.println("(left)"+(Element)val_peek(3).obj+" := (right)"+(Element)val_peek(1).obj);
                                                                          /*	String typeResult = ((Element)$1.obj).getTypeVariable();*/
																		if( ((Element)val_peek(1).obj).getTypeVariable()!=null ){
                                                                          if(((Element)val_peek(3).obj).getTypeVariable()!=null){
                                                                             /* Token T1 =lexAn.getSymbolTable().getToken((((Element)val_peek(3).obj).getLexema())); /* it's the left operand of assign* /*/
                                                                              Element rightExpresion = (Element)val_peek(1).obj;
                                                                              /**/
                                                                              /**/
                                                                              /* 			 FALTA AGREGAR LAS CONVERSIONES EN LAS ASIGNACIONES*/
                                                                              /**/
                                                                              /**/
                                                                              if ( ((Element)val_peek(1).obj).getClassType().equals("Token") ){
                                                                                  rightExpresion = lexAn.getSymbolTable().getToken((((Element)val_peek(1).obj).getLexema()));
                                                                                }
                                                                              String typeResult = ((Element)val_peek(3).obj).getTypeVariable();
                                                                              String leftType = ((Element)val_peek(3).obj).getTypeVariable();
                                                                              System.out.println("la expresion a asignar es"+ rightExpresion);
                                                                              String rightType = rightExpresion.getTypeVariable();
                                                                              
                                                                             /* Element leftExpresion = T1;*/
																			 Element leftExpresion = ((Element)val_peek(3).obj);
																			 
                                                                              if ( ( rightExpresion.getUse() != null && rightExpresion.getUse().equals("mat")) && ( leftExpresion.getUse() != null && leftExpresion.getUse().equals("mat")) ){
                                                                                    System.out.println("matrices de los dos lados de la asignacion"+leftExpresion+" := "+rightExpresion);
                                                                                    System.out.println("asignado a la izquierda de la asignacion"+tercetos.get(tercetos.size()-12));
                                                                                    System.out.println("asignado a la derecha de la asignacion"+tercetos.get(tercetos.size()-1));
                                                                                    leftExpresion = tercetos.get(tercetos.size()-12);
                                                                                    putValuesMatrix(tercetos.get(tercetos.size()-2).getValue(), tercetos.get(tercetos.size()-1).getValue());

                                                                                    rightExpresion = makeConvertion(leftExpresion, leftType, rightType, tercetos.get(tercetos.size()-1), typeResult);
                                                                                    System.out.println("rightExpresion dsps de make matrix"+tercetos.get(tercetos.size()-1));
                                                                                    /*rightExpresion = tercetos.get(tercetos.size()-1);*/

                                                                                    yyval.obj = new TercetoAsignacion(leftExpresion,rightExpresion);
                                                                                }
                                                                                  else
                                                                                      if ( rightExpresion.getUse() != null && rightExpresion.getUse().equals("mat")){
                                                                                            System.out.println("matriz del lado derecho solamente"+leftExpresion+" := "+rightExpresion);
                                                                                            System.out.println("asignado a la derecha de la asignacion"+tercetos.get(tercetos.size()-1));
                                                                                            /*rightExpresion = tercetos.get(tercetos.size()-1);*/
                                                                                            putValuesMatrix(tercetos.get(tercetos.size()-2).getValue(), tercetos.get(tercetos.size()-1).getValue());

                                                                                            rightExpresion = makeConvertion(leftExpresion, leftType, rightType, tercetos.get(tercetos.size()-1), typeResult);
                                                                                            yyval.obj = new TercetoAsignacion(leftExpresion,rightExpresion);
                                                                                        } else
                                                                                             if ( leftExpresion.getUse() != null && leftExpresion.getUse().equals("mat")) {
                                                                                                  System.out.println("matriz del lado izquierdo solamente"+leftExpresion+" := "+rightExpresion);
                                                                                                  System.out.println("asignado a la derecha de la asignacion"+tercetos.get(tercetos.size()-1));
                                                                                                 /* leftExpresion = tercetos.get(tercetos.size()-1);*/
                                                                                                 /* System.out.println("tercetos suma???"+tercetos.get(tercetos.size()-2)+"  value:"+tercetos.get(tercetos.size()-2).getValue()+" valor (i)"+rightExpresion.getValue());*/

                                                                                                 /*putValuesMatrix(tercetos.get(tercetos.size()-2).getValue(), rightExpresion.getValue());*/

                                                                                                  rightExpresion = makeConvertion(leftExpresion, leftType, rightType, rightExpresion, typeResult);
                                                                                                  yyval.obj = new TercetoAsignacion(leftExpresion,rightExpresion);
                                                                                                } else {
                                                                                                      System.out.println("caso que ninguno sea  matriz"+leftExpresion+" := "+rightExpresion);
                                                                                                      rightExpresion = makeConvertion(leftExpresion, leftType, rightType, rightExpresion, typeResult);
                                                                                                      yyval.obj = new TercetoAsignacion(leftExpresion,rightExpresion);
                                                                                                    }
                                                                                System.out.println("antes de  assign value"+leftExpresion+" := "+rightExpresion);
                                                                                System.out.println("antes de  assign value"+leftExpresion.getTypeVariable()+" := "+rightExpresion.getTypeVariable());
                                                                                assignValue(leftExpresion,rightExpresion);
                                                                                System.out.println("$$.obj "+yyval.obj);
                                                                                ((Terceto) yyval.obj).setTypeVariable(typeResult); /*cambiar el tipo*/
                                                                                System.out.println("$$.obj seteado type variable "+yyval.obj+" tipo asignado "+((Terceto)yyval.obj).getTypeVariable());
                                                                                tercetos.add((Terceto)yyval.obj);
                                                                                ((Terceto)yyval.obj).setPosition((Integer)tercetos.size());
                                                                                System.out.println("Tipo variable asignacion "+typeResult+"El tipo del TERCETO en ASIGNACION es: "+((Terceto)yyval.obj).getTypeVariable()+":="+((Element)val_peek(1).obj).getTypeVariable());








                                                                                /*if ( T1.getUse() != null && T1.getUse().equals("mat"))*/
                                                                                /*	if ( ((Element)val_peek(3).obj).getUse() != null && ((Element)val_peek(3).obj).getUse().equals("mat"))*/
                                                                                /*	*/
                                                                                /*System.out.println("IF MATRIZ DEL LADO IZQUIERDO"+T1);*/
                                                                                /*int currentRow = T1.getCurrentRow();*/
                                                                                /*int currentColumn = T1.getCurrentColumn();*/
                                                                                /*makeMatrix(((Token)val_peek(3).obj),currentRow,currentColumn);*/
                                                                                /*System.out.println("se cambio MAKEmatrix matifacu hacia abajo"+tercetos.get(tercetos.size()-1));*/
                                                                                /*leftExpresion = tercetos.get(tercetos.size()-1);*/
                                                                                /**/

                                                                                /*if ( rightExpresion.getUse() != null && rightExpresion.getUse().equals("mat"))*/
                                                                                /*if ( ((Element)val_peek(1).obj).getUse() != null && ((Element)val_peek(1).obj).getUse().equals("mat"))*/
                                                                                /**/
                                                                                /*System.out.println("el elemento de la derecha es matriz");*/
                                                                                /*Token matriz = lexAn.getSymbolTable().getToken(((Element)val_peek(1).obj).getLexema());*/
                                                                                /*int currentRow = ((Token) rightExpresion).getCurrentRow();*/
                                                                                /*int currentColumn = ((Token) rightExpresion).getCurrentColumn();*/
                                                                                /*makeMatrix(((Token)val_peek(1).obj),currentRow,currentColumn);*/
                                                                                /*agregar llamado a nuevo metodo para crear conversiones*/
                                                                                /*System.out.println("se cambio MAKEmatrix leguajesmati hacia abajo"+tercetos.get(tercetos.size()-1));*/
                                                                                /*rightExpresion = makeConvertion(T1, leftType, rightType, tercetos.get(tercetos.size()-1), typeResult);*/

                                                                                /*yyval.obj = new TercetoAsignacion(leftExpresion,rightExpresion);*/
                                                                                /*System.out.print("Terceto Asignacion "+leftExpresion+" := "+rightExpresion+"");*/
                                                                                /*assignValue(T1,rightExpresion);*/
                                                                                /*tercetos.add((Terceto)yyval.obj);*/
                                                                                /*((Terceto) yyval.obj).setTypeVariable(typeResult);*/
                                                                                /*((Terceto)yyval.obj).setPosition((Integer)tercetos.size());*/
                                                                                /*System.out.println("Tipo variableMMMMMM asignacion "+typeResult+"El tipo del TERCETO en ASIGNACION es: "+((Terceto)yyval.obj).getTypeVariable()+":="+((Element)val_peek(1).obj).getTypeVariable());*/

                                                                                /*else */
                                                                                /*/System.out.println("el elemento de la derecha no es una matriz");*/
                                                                                /*System.out.println("expresion de la asignacion"+rightExpresion);*/
                                                                                /*agregar llamado a conversion metodo nuevo*/
                                                                                /*rightExpresion = makeConvertion(T1, leftType, rightType, rightExpresion, typeResult);*/
                                                                                /*yyval.obj = new TercetoAsignacion(leftExpresion,rightExpresion);*/
                                                                                /*System.out.println("terceto asignacion creado $$.obj"+yyval.obj);*/
                                                                                /*((Element)$3.obj).setTypeVariable(assignTypeVariable(((Element)$1.obj),((Element)$3.obj)));*/
                                                                                /*assignValue(T1,rightExpresion);*/
                                                                                /*System.out.println("$$.obj "+yyval.obj);*/
                                                                                /*((Terceto) yyval.obj).setTypeVariable(typeResult); /*cambiar el tipo* /*/
                                                                                /*System.out.println("$$.obj seteado type variable "+yyval.obj+" tipo asignado "+((Terceto)yyval.obj).getTypeVariable());*/
                                                                                /*tercetos.add((Terceto)yyval.obj);*/
                                                                                /*((Terceto)yyval.obj).setPosition((Integer)tercetos.size());*/
                                                                                /*System.out.println("Tipo variableMMMMMM asignacion   "+typeResult+"El tipo del TERCETO en ASIGNACION es: "+((Terceto)yyval.obj).getTypeVariable()+":="+((Element)val_peek(1).obj).getTypeVariable());*/
                                                                              }
                                                                              else {
                                                                                  UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". ERROR tipos incompatibles\n");
                                                                                  errores.add("Linea: "+lexAn.getLineNumber()+"ERROR tipos incompatibles\n");
                                                                                }
																			}/* aca se controla el error de variables no declaradas*/
                                                                          }
break;
case 53:
//#line 482 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". ERROR DE OPERADOR ASIGNACION"+"\n"); errores.add("Linea: "+lexAn.getLineNumber()+"ERROR DE OPERADOR ASIGNACION\n");}
break;
case 54:
//#line 483 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". ERROR DE ASIGNACION"+"\n"); errores.add("Linea: "+lexAn.getLineNumber()+"ERROR DE ASIGNACION\n");}
break;
case 55:
//#line 486 "gramaticat.y"
{	System.out.println("==== comparador if ====");
                                                                          /*if(  !((Element)$2.obj).getTypeVariable().equals(((Element) $4.obj).getTypeVariable()) ){
                                                                          String typeResult = operationMatrix.getTypeOperation( ((Element)$2.obj).getTypeVariable() , ((Element) $4.obj).getTypeVariable() );
                                                                          Terceto conversion;
                                                                          System.out.println("===conversiones====");
                                                                          if (typeResult.equals(((Element)$2.obj).getTypeVariable())) {
                                                                          conversion = new TercetoConversion(((Element)$4.obj), typeResult);
                                                                          } else {
                                                                          conversion = new TercetoConversion(((Element)$2.obj), typeResult);
                                                                          }
                                                                          System.out.println("conversion creada"+conversion;
                                                                          tercetos.add(conversion);
                                                                          (conversion).setPosition(tercetos.size());
                                                                          }*/


                                                                          Element eright=(Element)val_peek(3).obj;
                                                                          Element eleft=(Element)val_peek(1).obj;
                                                                          System.out.println("el de la derecha"+eleft.getUse());
                                                                          /*(eright.getClassType().equals("Token")    && */
                                                                          if (((eright.getUse()!=null) && (eright.getUse().equals("mat"))) && (eleft.getUse()!=null && eleft.getUse().equals("mat"))){
                                                                              eright=tercetos.get(tercetos.size()-12);
                                                                              eleft= tercetos.get(tercetos.size()-1);
                                                                          } else
                                                                                if (eright.getClassType().equals("Token")    && (eright.getUse()!=null) && (eright.getUse().equals("mat"))){
                                                                                    eright=tercetos.get(tercetos.size()-1);
                                                                                  }
                                                                                  else
                                                                                      if (eleft.getClassType().equals("Token")&& (eleft.getUse()!=null) && (eleft.getUse().equals("mat"))){
                                                                                            eleft=tercetos.get(tercetos.size()-1);
                                                                                          }


                                                                                /**/
                                                                                /*		Conversiones*/
                                                                                /**/

                                                                        String typeResult = operationMatrix.getTypeOperation( ((Element)val_peek(3).obj).getTypeVariable() , ((Element) val_peek(1).obj).getTypeVariable() );
                                                                        if(  !((Element)val_peek(3).obj).getTypeVariable().equals(((Element) val_peek(1).obj).getTypeVariable()) ){
                                                                              System.out.println("====conversiones ====");
                                                                              Terceto conversion;
                                                                              if (typeResult.equals(((Element)val_peek(3).obj).getTypeVariable())) {
                                                                                  conversion = new TercetoConversion(eleft, typeResult);
                                                                                  eleft = conversion;
                                                                               } else {
                                                                                    conversion = new TercetoConversion(eright, typeResult);
                                                                                    eright = conversion;
                                                                                  }
                                                                              System.out.println("conversion creada "+conversion);
                                                                              tercetos.add(conversion);
                                                                              conversion.setTypeVariable(typeResult);
                                                                              (conversion).setPosition(tercetos.size());
                                                                          }
                                                                        System.out.println("expresion right"+eleft+"     terminoleft"+eright);

                                                                        yyval.obj = new TercetoComparador((Token)val_peek(2).obj,eright,eleft); /*ANDA*/
                                                                        ((Element)yyval.obj).setTypeVariable(typeResult);
                                                                        tercetos.add((Terceto)yyval.obj);
                                                                        ((Terceto)yyval.obj).setPosition(tercetos.size());
                                                                        /*((Element)$$.obj).setHasLabel(true);*/
                                                                        /*System.out.println("El tamaño del arreglo TERCETO en CONDICION es: "+tercetos.size());*/
                                                                      }
break;
case 56:
//#line 548 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+"."+lexAn.getIndexLine()+" ERROR EN CONDICION"+"\n"); errores.add("Linea: "+lexAn.getLineNumber()+"ERROR EN CONDICION");}
break;
case 57:
//#line 550 "gramaticat.y"
{UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". ERROR EN IF paren2"+"\n"); errores.add("Linea: "+lexAn.getLineNumber()+"ERROR EN CONDICION");}
break;
case 63:
//#line 565 "gramaticat.y"
{yyval.obj = val_peek(0).obj;}
break;
case 64:
//#line 567 "gramaticat.y"
{    	System.out.println("==== expresion + termino ==== ");
                                                  System.out.println("expresion "+ val_peek(2).obj+"  termino "+val_peek(0).obj);
                                                  System.out.println(((Element)val_peek(2).obj).getTypeVariable()+"lola"+((Element) val_peek(0).obj).getTypeVariable());
                                                  /*if(  !((Element)$1.obj).getTypeVariable().equals(((Element) $3.obj).getTypeVariable()) ){
                                                  System.out.println("====nooooooooooooooo por aca noooooooooooooo ==== ");
                                                  String typeResult = operationMatrix.getTypeOperation( ((Element)$1.obj).getTypeVariable() , ((Element) $3.obj).getTypeVariable() );
                                                  Terceto conversion;
                                                  if (typeResult.equals(((Element)$1.obj).getTypeVariable())) {
                                                  conversion = new TercetoConversion(((Element)$3.obj), typeResult);
                                                  } else {
                                                  conversion = new TercetoConversion(((Element)$1.obj), typeResult);
                                                  }
                                                  tercetos.add(conversion);
                                                  (conversion).setPosition(tercetos.size());
                                                  }*/
                                                  /*Token vexp =lexAn.getSymbolTable().getToken((((Element)$1.obj).getLexema()));*/
                                                  /*Token vter =lexAn.getSymbolTable().getToken((((Element)$3.obj).getLexema()));*/

                                                  Element expresionRight=(Element)val_peek(0).obj;/* esto es termino*/
                                                  Element terminoLeft=(Element)val_peek(2).obj;  /* esto es expresion, SI ESTAN AL REVES PERO BUENO ANDA*/


                                                  System.out.println("el de la derecha"+terminoLeft.getUse());
                                                  /*(eright.getClassType().equals("Token")    && */
                                                  if (((expresionRight.getUse()!=null) && (expresionRight.getUse().equals("mat"))) && (terminoLeft.getUse()!=null && terminoLeft.getUse().equals("mat"))){
                                                        expresionRight=tercetos.get(tercetos.size()-1);
                                                        terminoLeft= tercetos.get(tercetos.size()-12);
                                                  } else
                                                        if (expresionRight.getClassType().equals("Token")  && (expresionRight.getUse()!=null) && (expresionRight.getUse().equals("mat"))){
                                                            expresionRight=tercetos.get(tercetos.size()-1);
                                                          }
                                                          else
                                                              if (terminoLeft.getClassType().equals("Token")&& (terminoLeft.getUse()!=null) && (terminoLeft.getUse().equals("mat"))){
                                                                  terminoLeft=tercetos.get(tercetos.size()-1);
                                                                }

                                                /**/
                                                /*		Conversiones*/
                                                /**/
                                                String typeResult = operationMatrix.getTypeOperation( ((Element)val_peek(2).obj).getTypeVariable() , ((Element) val_peek(0).obj).getTypeVariable() );
                                                if(  !((Element)val_peek(2).obj).getTypeVariable().equals(((Element) val_peek(0).obj).getTypeVariable()) ){
                                                      System.out.println("====conversiones ====");
                                                      Terceto conversion;
                                                      if (typeResult.equals(((Element)val_peek(2).obj).getTypeVariable())) {
                                                          conversion = new TercetoConversion(expresionRight, typeResult);
                                                          expresionRight = conversion;
                                                        } else {
                                                              conversion = new TercetoConversion(terminoLeft, typeResult);
                                                              terminoLeft = conversion;
                                                            }
                                                      System.out.println("conversion creada "+conversion);
                                                      tercetos.add(conversion);
                                                      conversion.setTypeVariable(typeResult);
                                                      (conversion).setPosition(tercetos.size());
                                                  }
                                            System.out.println("expresion right"+expresionRight+"     terminoleft"+terminoLeft);

                                            yyval.obj = new TercetoSuma(expresionRight,terminoLeft);

                                            /*ANDA*/
                                            /**
                                            *INDICESNUEVOS
                                            *es proable que con los nuvos cambios esto no sea necesario
                                            *
                                            */
                                            /*Object valueNew=null;*/
                                            /*if ( expresionRight.getTypeVariable().equals("integer") ){*/
                                            /*    valueNew= ((Integer)expresionRight.getValue())+((Integer)terminoLeft.getValue());*/
                                            /*  } else valueNew= ((Float)expresionRight.getValue())+((Float)terminoLeft.getValue());*/
                                            /*  assignValue(((Element)$$.obj),new Token("","",-1,valueNew));*/

                                              /*((Element)$$.obj).setTypeVariable(operationTypeVariable(expresionRight,terminoLeft));*/
                                            ((Element)yyval.obj).setTypeVariable(typeResult);
                                            tercetos.add((Terceto)yyval.obj);
                                            ((Terceto)yyval.obj).setPosition(tercetos.size());
                                            System.out.println("terceto suma creado"+yyval.obj);
                                            System.out.println("El tipo del TERCETO en SUMA es: "+((Element)yyval.obj).getTypeVariable());}
break;
case 65:
//#line 648 "gramaticat.y"
{     /*	if(  !((Element)$1.obj).getTypeVariable().equals(((Element) $3.obj).getTypeVariable()) ){
            String typeResult = operationMatrix.getTypeOperation( ((Element)$1.obj).getTypeVariable() , ((Element) $3.obj).getTypeVariable() );
            Terceto conversion;
            if (typeResult.equals(((Element)$1.obj).getTypeVariable())) {
            conversion = new TercetoConversion(((Element)$3.obj), typeResult);
          } else {
          conversion = new TercetoConversion(((Element)$1.obj), typeResult);
        }
        tercetos.add(conversion);
        (conversion).setPosition(tercetos.size());
      }*/
      /*Token vexp =lexAn.getSymbolTable().getToken((((Element)$1.obj).getLexema()));*/
      /*Token vter =lexAn.getSymbolTable().getToken((((Element)$3.obj).getLexema()));*/

      Element expresionRight=(Element)val_peek(2).obj;
      Element terminoLeft=(Element)val_peek(0).obj;


      System.out.println("el de la derecha"+terminoLeft.getUse());
      /*(eright.getClassType().equals("Token")    && */
      if (((expresionRight.getUse()!=null) && (expresionRight.getUse().equals("mat"))) && (terminoLeft.getUse()!=null && terminoLeft.getUse().equals("mat"))){
        expresionRight=tercetos.get(tercetos.size()-12);
        terminoLeft= tercetos.get(tercetos.size()-1);
      } else
      if (expresionRight.getClassType().equals("Token")  && (expresionRight.getUse()!=null) && (expresionRight.getUse().equals("mat"))){
        expresionRight=tercetos.get(tercetos.size()-1);
      }
      else
      if (terminoLeft.getClassType().equals("Token")&& (terminoLeft.getUse()!=null) && (terminoLeft.getUse().equals("mat"))){
        terminoLeft=tercetos.get(tercetos.size()-1);
      }

      /* revisar conversiones sobre matrices quizas haya que agregar una condicion DONE*/
      /**/
      /*		Conversiones*/
      /**/
      String typeResult = operationMatrix.getTypeOperation( ((Element)val_peek(2).obj).getTypeVariable() , ((Element) val_peek(0).obj).getTypeVariable() );
      if(  !((Element)val_peek(2).obj).getTypeVariable().equals(((Element) val_peek(0).obj).getTypeVariable()) ){
        System.out.println("====conversiones ====");
        Terceto conversion;
        if (typeResult.equals(((Element)val_peek(2).obj).getTypeVariable())) {
          conversion = new TercetoConversion(terminoLeft, typeResult);
          terminoLeft = conversion;
        } else {
          conversion = new TercetoConversion(expresionRight, typeResult);
          expresionRight = conversion;
        }
        System.out.println("conversion creada "+conversion);
        tercetos.add(conversion);
        conversion.setTypeVariable(typeResult);
        (conversion).setPosition(tercetos.size());
      }
      System.out.println("expresion right"+expresionRight+"     terminoleft"+terminoLeft);

      yyval.obj = new TercetoResta(expresionRight,terminoLeft); /*ANDA*/

  /*    Object valueNew=null;*/
    /*  if ( expresionRight.getTypeVariable().equals("integer") ){*/
    /*    valueNew= ((Integer)expresionRight.getValue())+((Integer)terminoLeft.getValue());*/
    /*  } else valueNew= ((Float)expresionRight.getValue())+((Float)terminoLeft.getValue());*/
/**/
      /*assignValue(((Element)$$.obj),new Token("","",-1,valueNew));*/

      ((Element)yyval.obj).setTypeVariable(typeResult);
      tercetos.add((Terceto)yyval.obj);
      ((Terceto)yyval.obj).setPosition(tercetos.size());
      /*System.out.println("El tamaño del arreglo TERCETO en RESTA es: "+tercetos.size());*/
    }
break;
case 66:
//#line 718 "gramaticat.y"
{yyval.obj = val_peek(0).obj;}
break;
case 67:
//#line 720 "gramaticat.y"
{yyval.obj = new TercetoDecremento((Element)val_peek(1).obj);
      System.out.println("ANTES de setear la posicion en terceto decremento");
      tercetos.add((Terceto)yyval.obj);
      ((Terceto)yyval.obj).setPosition(tercetos.size());
      System.out.println("posicion terceto decremento: "+tercetos.size());}
break;
case 68:
//#line 726 "gramaticat.y"
{



        Element expresionRight=(Element)val_peek(2).obj;
        Element terminoLeft=(Element)val_peek(0).obj;


        System.out.println("el de la derecha"+terminoLeft.getUse());
        /*(eright.getClassType().equals("Token")    && */
        if (((expresionRight.getUse()!=null) && (expresionRight.getUse().equals("mat"))) && (terminoLeft.getUse()!=null && terminoLeft.getUse().equals("mat"))){
          expresionRight=tercetos.get(tercetos.size()-12);
          terminoLeft= tercetos.get(tercetos.size()-1);
        } else
        if (expresionRight.getClassType().equals("Token")  && (expresionRight.getUse()!=null) && (expresionRight.getUse().equals("mat"))){
          expresionRight=tercetos.get(tercetos.size()-1);
        }
        else
        if (terminoLeft.getClassType().equals("Token")&& (terminoLeft.getUse()!=null) && (terminoLeft.getUse().equals("mat"))){
          terminoLeft=tercetos.get(tercetos.size()-1);
        }

        /* revisar conversiones sobre matrices quizas haya que agregar una condicion DONE*/
        /**/
        /*		Conversiones*/
        /**/
        String typeResult = operationMatrix.getTypeOperation( ((Element)val_peek(2).obj).getTypeVariable() , ((Element) val_peek(0).obj).getTypeVariable() );
        if(  !((Element)val_peek(2).obj).getTypeVariable().equals(((Element) val_peek(0).obj).getTypeVariable()) ){
          System.out.println("====conversiones ====");
          Terceto conversion;
          if (typeResult.equals(((Element)val_peek(2).obj).getTypeVariable())) {
            conversion = new TercetoConversion(terminoLeft, typeResult);
            terminoLeft = conversion;
          } else {
            conversion = new TercetoConversion(expresionRight, typeResult);
            expresionRight = conversion;
          }
          System.out.println("conversion creada "+conversion);
          tercetos.add(conversion);
          conversion.setTypeVariable(typeResult);
          (conversion).setPosition(tercetos.size());
        }
        System.out.println("expresion right"+expresionRight+"     terminoleft"+terminoLeft);
        yyval.obj = new TercetoMultiplicacion(expresionRight,terminoLeft);

      /*  Object valueNew=null;*/
      /*  if ( expresionRight.getTypeVariable().equals("integer") ){*/
      /*    valueNew= ((Integer)expresionRight.getValue())+((Integer)terminoLeft.getValue());*/
      /*  } else valueNew= ((Float)expresionRight.getValue())+((Float)terminoLeft.getValue());*/
      /*  assignValue(((Element)$$.obj),new Token("","",-1,valueNew));*/

        ((Element)yyval.obj).setTypeVariable(typeResult);
        tercetos.add((Terceto)yyval.obj);
        ((Terceto)yyval.obj).setPosition((Integer)tercetos.size());
        /* System.out.println("posicion terceto ENZO multiplicacion");*/
        /* System.out.println("posicion terceto multi: "+tercetos.size());*/
      }
break;
case 69:
//#line 784 "gramaticat.y"
{

        String typeResult = divisionTypeVariable( (Element)val_peek(2).obj , (Element) val_peek(0).obj );
        Element expresionRight=(Element)val_peek(2).obj; /* esto es termino*/
        Element terminoLeft=(Element)val_peek(0).obj;  /* esto es factor,  es lo mismo pero cambiarian los nombres jaja*/
        System.out.println("el de la derecha"+terminoLeft.getUse());
        /*(eright.getClassType().equals("Token")    && */
        if (((expresionRight.getUse()!=null) && (expresionRight.getUse().equals("mat"))) && (terminoLeft.getUse()!=null && terminoLeft.getUse().equals("mat"))){
          expresionRight=tercetos.get(tercetos.size()-12);
          terminoLeft= tercetos.get(tercetos.size()-1);
        } else
        if (expresionRight.getClassType().equals("Token")  && (expresionRight.getUse()!=null) && (expresionRight.getUse().equals("mat"))){
          expresionRight=tercetos.get(tercetos.size()-1);
        }
        else
        if (terminoLeft.getClassType().equals("Token")&& (terminoLeft.getUse()!=null) && (terminoLeft.getUse().equals("mat"))){
          terminoLeft=tercetos.get(tercetos.size()-1);
        }


        /**/
        /*		Conversiones*/
        /**/
        /*String typeResult = operationMatrix.getTypeOperation( ((Element)$1.obj).getTypeVariable() , ((Element) $3.obj).getTypeVariable() );*/
        if(  !((Element)val_peek(2).obj).getTypeVariable().equals(((Element) val_peek(0).obj).getTypeVariable()) ){
          System.out.println("====conversiones ====");
          Terceto conversion;
          if (typeResult.equals(((Element)val_peek(2).obj).getTypeVariable())) {
            conversion = new TercetoConversion(terminoLeft, typeResult);
            terminoLeft = conversion;
          } else {
            conversion = new TercetoConversion(expresionRight, typeResult);
            expresionRight = conversion;
          }
          System.out.println("conversion creada "+conversion);
          tercetos.add(conversion);
          conversion.setTypeVariable(typeResult);
          (conversion).setPosition(tercetos.size());
        }
        System.out.println("expresion right"+expresionRight+"     terminoleft"+terminoLeft);



        yyval.obj = new TercetoDivision(expresionRight,terminoLeft);
        ((Element)yyval.obj).setTypeVariable(typeResult);
        tercetos.add((Terceto)yyval.obj);
        ((Terceto)yyval.obj).setPosition(tercetos.size());
        System.out.println(" posicion terceto division: "+tercetos.size());}
break;
case 70:
//#line 834 "gramaticat.y"
{yyval.obj = val_peek(0).obj;}
break;
case 71:
//#line 836 "gramaticat.y"
{yyval.obj = val_peek(0).obj;}
break;
case 72:
//#line 838 "gramaticat.y"
{yyval.obj = lexAn.getSymbolTable().getToken(((Token)val_peek(0).obj).getLexema()); /*ANDA*/
          System.out.println("Probando CTEINTEGER: "+lexAn.getSymbolTable().getToken(((Token)val_peek(0).obj).getLexema()));}
break;
case 73:
//#line 841 "gramaticat.y"
{yyval.obj = lexAn.getSymbolTable().getToken(((Token)val_peek(0).obj).getLexema());}
break;
case 74:
//#line 843 "gramaticat.y"
{
            if (controlVarNotDeclared(((Token)val_peek(0).obj))){
              UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". Variable ["+((Token)val_peek(0).obj).getLexema()+"] no declarada \n");
              errores.add("Linea: "+lexAn.getLineNumber()+"Variable no declarada");
            } else { if ( lexAn.getSymbolTable().containsSymbol("var@"+((Token)val_peek(0).obj).getLexema()) ){
							yyval.obj = lexAn.getSymbolTable().getToken("var@"+((Token)val_peek(0).obj).getLexema());
						} else {
								yyval.obj = lexAn.getSymbolTable().getToken(((Token)val_peek(0).obj).getLexema());
							}/* ANDA*/
              System.out.println("Probando IDENTIFICADOR en regla de variable ident: "+lexAn.getSymbolTable().getToken(((Token)val_peek(0).obj).getLexema()));}
            }
break;
case 75:
//#line 854 "gramaticat.y"
{ yyval.obj = val_peek(0).obj;}
break;
case 76:
//#line 858 "gramaticat.y"
{System.out.println("==== valor matrix ==== ");
			
			if (controlVarNotDeclared(((Token)val_peek(6).obj))){
              UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". Variable ["+((Token)val_peek(6).obj).getLexema()+"] no declarada \n");
              errores.add("Linea: "+lexAn.getLineNumber()+"Variable no declarada");
			  }
				else {
			
			
            /*makeMatrix((Token)$1.obj,((Token)$3.obj).getValue(),((Token)$6.obj).getValue());*/
            /* $$.obj = tercetos.get(tercetos.size()-1);*/
            if ( !((Element)val_peek(4).obj).getTypeVariable().equals("integer") || !((Element)val_peek(1).obj).getTypeVariable().equals("integer") ){
              UI2.addText(UI2.txtDebug,"Linea: "+lexAn.getLineNumber()+". Variable ["+((Token)val_peek(6).obj).getLexema()+"] con limites no enteros");
              errores.add("Linea: "+lexAn.getLineNumber()+"Variable matriz con limites de tipo erroneos");
            }else {
				Token identificador=null;
				if (lexAn.getSymbolTable().containsSymbol("mat@"+((Token)val_peek(6).obj).getLexema())){
							identificador = lexAn.getSymbolTable().getToken("mat@"+((Token)val_peek(6).obj).getLexema());
						} else {
								identificador = lexAn.getSymbolTable().getToken(((Token)val_peek(6).obj).getLexema());
							}
              String typeVariable = lexAn.getSymbolTable().getToken(identificador.getLexema()).getTypeVariable();
              System.out.println("elementos enviados a make matrix fila"+((Element)val_peek(4).obj)+"  columna"+((Element)val_peek(1).obj));
              makeMatrix(identificador,((Element)val_peek(4).obj),((Element)val_peek(1).obj));
              System.out.println("referencia: "+tercetos.get(tercetos.size()-1)+"   suma: "+tercetos.get(tercetos.size()-2));
              tercetos.get(tercetos.size()-1).setValue(valoresMatriz.get(tercetos.get(tercetos.size()-2).getValue()));
              System.out.println("get value "+tercetos.get(tercetos.size()-1).getValue());

              yyval.obj = tercetos.get(tercetos.size()-1);
              ((Element)yyval.obj).setTypeVariable(typeVariable);
              ((Element)yyval.obj).setUse("mat");

            }
          }
		  }
break;
//#line 2101 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
//## The -Jnorun option was used ##
//## end of method run() ########################################



//## Constructors ###############################################
//## The -Jnoconstruct option was used ##
//###############################################################



}
//################### END OF CLASS ##############################
