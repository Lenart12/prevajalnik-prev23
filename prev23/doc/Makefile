JAVADOC		= javadoc
ANTLRDIR	= ../lib/antlr-4.11.1-complete.jar
RM		= 'rm' -fr
FIND		= 'find'

all	:
	$(JAVADOC) -cp ../src:$(ANTLRDIR) -d javadoc -private -subpackages prev23

.PHONY	: clean
clean	:
	$(RM) *.aux *.log *.pdf *.synctex.gz
	$(RM) javadoc
