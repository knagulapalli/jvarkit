filters **SAM**, **BAM** records using javascript.

# Usage #

```
java -jar samjs.jar [options] (-e script | -f script) (file.bam|stdin)
```


the program injects

  * '**record**' a **SamRecord** ( http://picard.sourceforge.net/javadoc/net/sf/samtools/SAMRecord.html ).
  * '**header**' a **SAMFileHeader** ( http://picard.sourceforge.net/javadoc/net/sf/samtools/SAMFileHeader.html)

in the javascript context .



# Options #

  * -h help; This screen.
  * -S : SAM output.
  * -e (script).
  * -f (scriptfile).
  * -L (int) limit to 'L' records.

# Compilation #

update 'build.properties' and run:

```
ant samjs
```

# Example #

```
$ java -jar dist/samjs.jar -L 4  -S \
  -e '(record.inferredInsertSize > 60 || record.alignmentStart  < 50000000) && !record.readUnmappedFlag && record.getAttribute("MD")!=null ' \
 my.bam
@HD	VN:1.0	SO:unsorted
@SQ	SN:chr1	LN:247249719
IL31_4368:1:1:997:15684	163	chr1	241356442	60	54M	=	241356612	224	CAGCCTCAGATTCAGCATTCTCAAATTCAGCTGCGGCTGAAACAGCAGCAGGAC	EEEEDEEE9EAEEDEEEEEEEEEECEEAAEEDEE<CD=D=*BCAC?;CB,<D@,	X0:i:1	X1:i:0	MD:Z:53T0	XG:i:0	AM:i:37	NM:i:1	SM:i:37	XM:i:1	XO:i:0	XT:A:U
IL31_4368:1:1:997:1657	163	chr1	143630066	60	54M	=	143630364	352	CCCACCTCTCTCAATGTTTTCCATATGGCAGGGACTCAGCACAGGTGGATTAAT	A;0A?AA+@A<7A7019/<65,3A;'''07<A=<=>?7=?6&)'9('*%,>/(<	X0:i:1	X1:i:0	MD:Z:54	XG:i:0	AM:i:37	NM:i:0	SM:i:37	XM:i:0	XO:i:0	XT:A:U
IL31_4368:1:1:999:9391	163	chr1	195364100	29	54M	=	195364272	226	AAAAAAAAAAACCCTCATTTTTTTTAAGTACTAAATTTTTTTTCCCATTTGAAA	1>??E?>@BB>0A/43;,=9A98A(',0/<*4>>/@=90A51&(**3;>'*;=;	MD:Z:11A5A7C12A2G1A1T1C6	XG:i:0	AM:i:29	NM:i:8	SM:i:29	XM:i:8	XO:i:0	XT:A:M
IL31_4368:1:1:1002:11012	99	chr1	147142473	60	54M	=	147142575	156	NGATTAGTACATAGTAAGTACTCAATAGATGTTAGCTATTATTGTAATCACCGC	(1*4+236332679?1..87><-6<@<7>>@><7;@@@>962$-6075584093	X0:i:1	X1:i:0	MD:Z:0A40C12	XG:i:0	AM:i:37	NM:i:2	SM:i:37	XM:i:2	XO:i:0	XT:A:U
```