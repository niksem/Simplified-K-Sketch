TARG=MCVMain
OUTDIR=bin
SRCDIR=src
 
SOURCES=$(shell find $(SRCDIR) -name "*.java")
CLASSES=$(shell find $(SRCDIR) -name "*.java" | sed "s/$(SRCDIR)\//$(OUTDIR)\//" | sed "s/\.java/.class/" )
 
all: $(CLASSES)
 
$(CLASSES): $(SOURCES)
	mkdir -p $(OUTDIR)
	javac -sourcepath src -d $(OUTDIR) $^
 
run: $(CLASSES)
	java -cp $(OUTDIR) $(TARG)
 
clean:
	rm -r $(OUTDIR)/*.class
