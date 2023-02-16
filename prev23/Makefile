JAVA		= java --enable-preview
JAVAC		= javac --enable-preview --release 19
ANTLR           = org.antlr.v4.Tool
ANTLRDIR	= lib/antlr-4.11.1-complete.jar
RM		= 'rm' -fr
FIND		= 'find'

all	:
	if [ -d src/prev23/phase/lexan ] ; then $(MAKE) -C src/prev23/phase/lexan ; fi
	if [ -d src/prev23/phase/synan ] ; then $(MAKE) -C src/prev23/phase/synan ; fi
	$(JAVAC) -encoding us-ascii -cp $(ANTLRDIR):src -d bin src/prev23/Compiler.java
	@echo ":-) OK"

.PHONY	: clean
clean	:
	if [ -d doc ] ; then $(MAKE) -C doc clean ; fi
	if [ -d src ] ; then $(MAKE) -C prg clean ; fi
	if [ -d src/prev23/phase/lexan ] ; then $(MAKE) -C src/prev23/phase/lexan clean ; fi
	if [ -d src/prev23/phase/synan ] ; then $(MAKE) -C src/prev23/phase/synan clean ; fi
	$(FIND) . -type f -iname "*~" -exec $(RM) {} \;
	$(FIND) . -type f -iname "*.class" -exec $(RM) {} \;
	$(RM) bin
