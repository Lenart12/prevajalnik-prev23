JAVA            = java --enable-preview
ANTLRDIR        = ../lib/antlr-4.11.1-complete.jar
XSLTPROC	= xsltproc
RM              = 'rm' -fr

PHASE		= all
LOG		= all
NUM_REGS = 4

.PHONY	: %
%	: %.p23
	$(RM) -fr $@.*.xml $@.*.html
	if $(JAVA) -cp ../bin:../src:$(ANTLRDIR) prev23.Compiler --num-regs=$(NUM_REGS) --xsl=../lib/xsl/ --logged-phase=$(LOG) --target-phase=$(PHASE) $< ; then for XML in `'ls' -1 $@.*.xml` ; do $(XSLTPROC) $$XML -o `basename $$XML xml`html; done ; fi

.PHONY  : clean
clean   :
	$(RM) *~
	$(RM) *xml *html *mms *mmo

