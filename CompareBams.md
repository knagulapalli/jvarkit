#compare the alignments produced by two or more bams.

# Download & install #

checkout the sources:
```
svn checkout http://jvarkit.googlecode.com/svn/trunk/ jvarkit
cd jvarkit
```

Download the libraries for picard and berkeleydb-java-edition, and create a file  build.properties**with the path to the jars:**

```
$ cat build.properties 
picard.jar=/commun/data/packages/picard-tools-1.79/picard-1.79.jar
sam.jar=/commun/data/packages/picard-tools-1.79/sam-1.79.jar
berkeleydb.jar=/commun/data/packages/je-5.0.34/lib/je-5.0.34.jar

```

compile:
```
$ ant cmpbams

Buildfile: build.xml

cmpbams:
    [javac] Compiling 1 source file to /home/lindenb/src/jvarkit/tmp
      [jar] Building jar: /home/lindenb/src/jvarkit/dist/comparebams.jar
   [delete] Deleting directory /home/lindenb/src/jvarkit/tmp

BUILD SUCCESSFUL
Total time: 1 second
```



# Options #
```
$ java -jar ~/src/jvarkit/dist/comparebams.jar -h

Pierre Lindenbaum PhD. 2013
Options:
 -h help; This screen.
 -d <berkeleydb-dir>.
 -F use samFlag
```

## Example ##

The following Makefile align the same pair of FASTQs with 5 different parameters for **bwa** **aln** **-O** **(gap\_open\_penalty)**

```
FASTQ1=001.fastq.gz
FASTQ2=002.fastq.gz
REF=/path/to/human_g1k_v37.fasta
BWA=bwa
SAMTOOLS=samtools
ALL_BAMS= 

define SAI
$(1)_1.sai : ${FASTQ1}  $(REF)
	$(BWA) aln  $(2) -t 2  -f $$@ $(REF) $$<
$(1)_2.sai :${FASTQ2}  $(REF)
	$(BWA) aln  $(2) -t 2  -f $$@ $(REF) $$<

endef

define ALN

ALL_BAMS+= $(1).bam 

$(eval $(call SAI,$(1),$(2)))

$(1).bam: $(1)_1.sai $(1)_2.sai
	$(BWA) sampe $(3)  ${REF} \
		$(1)_1.sai $(1)_2.sai  \
		$(FASTQ1) $(FASTQ2) |\
	$(SAMTOOLS) view -S -b -o $$@ -T $(REF) - 

endef

.PHONY:all

all: diff.gz

$(eval $(foreach GAP, 8 9 10 11 12 , $(call ALN,GAP$(GAP), -O $(GAP) , )))


diff.gz: $(ALL_BAMS)
	mkdir -p tmp.bdb
	java -jar  /commun/data/packages/jvarkit/comparebams.jar -d tmp.bdb $^ | gzip --best > $@
```

execute:
```
make

(...)
java -jar  /path/to/jvarkit/comparebams.jar -d tmp.bdb GAP8.bam GAP9.bam GAP10.bam GAP11.bam GAP12.bam | gzip --best > diff.gz
Feb 07, 2013 2:09:57 PM fr.inserm.umr1087.jvarkit.tools.cmpbams.CompareBams run
#INFO: in GAP8.bam count:1
Feb 07, 2013 2:10:30 PM fr.inserm.umr1087.jvarkit.tools.cmpbams.CompareBams run
INFO: in GAP9.bam count:1
(....)
```

The file diff.gz  is a tab delimited file containing
  * the name of the read
  * the comparaison of each bam vs the others
  * the positions of the reads in each bam

```
#READ-Name	GAP8.bam GAP9.bam|GAP8.bam GAP10.bam|GAP8.bam GAP11.bam|GAP8.bam GAP12.bam|GAP9.bam GAP10.bam|GAP9.bam GAP11.bam|GAP9.bam GAP12.bam|GAP10.bam GAP11.bam|GAP10.bam GAP12.bam|GAP11.bam GAP12.bam	GAP8.bam	GAP9.bam	GAP10.bam	GAP11.bam	GAP12.bam
M00491:10:000000000-A27BP:1:1101:10029:10672	EQ|NE|NE|NE|NE|NE|NE|EQ|EQ|EQ	6:123892013,6:123892006	6:123892013,6:123892006	6:123892005,6:123892013	6:123892005,6:123892013	6:123892005,6:123892013
M00491:10:000000000-A27BP:1:1101:10265:10054	EQ|EQ|NE|NE|EQ|NE|NE|NE|NE|NE	19:49671437,19:49671412	19:49671437,19:49671412	19:49671437,19:49671412	19:49671435	19:49671412,19:49671435
M00491:10:000000000-A27BP:1:1101:10904:12333	EQ|NE|NE|NE|NE|NE|NE|EQ|EQ|EQ	10:88681151,10:88681156	10:88681151,10:88681156	10:88681156,10:88681150	10:88681156,10:88681150	10:88681156,10:88681150
M00491:10:000000000-A27BP:1:1101:11211:13492	EQ|NE|NE|NE|NE|NE|NE|EQ|EQ|EQ	8:52321469	8:52321469	8:52321470	8:52321470	8:52321470
M00491:10:000000000-A27BP:1:1101:11298:18283	NE|NE|NE|NE|EQ|EQ|EQ|EQ|EQ|EQ	6:126071103,6:126071110	6:126071104,6:126071110	6:126071104,6:126071110	6:126071104,6:126071110	6:126071104,6:126071110
M00491:10:000000000-A27BP:1:1101:11381:15675	EQ|NE|NE|NE|NE|NE|NE|EQ|EQ|EQ	1:156106900,1:156106905	1:156106900,1:156106905	1:156106905,1:156106899	1:156106905,1:156106899	1:156106905,1:156106899
M00491:10:000000000-A27BP:1:1101:12189:14088	EQ|NE|NE|EQ|NE|NE|EQ|EQ|NE|NE	15:22015803	15:22015803	15:21009140	15:21009140	15:22015803
M00491:10:000000000-A27BP:1:1101:12382:11193	EQ|NE|NE|NE|NE|NE|NE|EQ|EQ|EQ	4:111542263,4:111542256	4:111542263,4:111542256	4:111542263,4:111542254	4:111542263,4:111542254	4:111542263,4:111542254
M00491:10:000000000-A27BP:1:1101:12998:24492	EQ|NE|NE|NE|NE|NE|NE|EQ|EQ|EQ	2:179433503,2:179433496	2:179433503,2:179433496	2:179433503,2:179433497	2:179433503,2:179433497	2:179433503,2:179433497
(...)
```