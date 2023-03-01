JAVADOC		= javadoc -quiet -Xmaxerrs 100000 -Xmaxwarns 100000
ANTLRDIR	= ../lib/antlr-4.11.1-complete.jar
RM		= 'rm' -fr
FIND		= 'find'

all	:
	$(JAVADOC) -cp ../src:$(ANTLRDIR) -d javadoc -private -subpackages prev23 2>&1 | gawk 'BEGIN{SKIP=0;}/(Report|LexAn|PrevLexer|Token|PrevParser|AstTrees|AstVisitor|AstNullVisitor|AstFullVisitor).java/{SKIP=2;next;}(SKIP>0){SKIP--;next;}{print;}'

.PHONY	: clean
clean	:
	$(RM) *.aux *.log *.pdf *.synctex.gz
	$(RM) javadoc
