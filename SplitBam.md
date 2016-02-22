**splitbam** splits a BAM by chromosomes.

Using the reference sequence dictionary (`*.dict`), it also creates some empty BAM files if no sam record was found for a chromosome.
A pair of 'mock' SAM-Records can also be added to those empty BAMs to avoid some tools (like samtools) to crash.

# Usage #
```
java -jar splitbam.jar -p OUT/__CHROM__/__CHROM__.bam -R ref.fasta (bam|sam|stdin)
```

# Options #

  * -h help; This screen.
  * -R (indexed reference file) REQUIRED.
  * -u (unmapped chromosome name): default:Unmapped
  * -e | --empty : generate EMPTY bams for chromosome having no read mapped
  * -m | --mock : if option '-e', add a mock pair of sam records to the empty bam
  * -p (output file/bam pattern) REQUIRED. MUST contain **`__CHROM__`** and end with .bam
  * -s assume input is sorted.
  * -x | --index  create index.
  * -t | --tmp  (dir) tmp file directory
  * -G (file) chrom-group file (see below)

# Chromosome group #

by default splitBam produces one file per chromosome. But you can use a group-file to group some chromosomes. The format is :
```
(group-name1)\tchrom1\tchrom2\tchrom3...\n
(group-name2)\tchrom11\tchrom12\tchrom22...\n
```
The missing chromosomes are defined in their own group by default.

Example:
```
XY	X	Y
GL_CHROM	SGL000207.1	GL000226.1	GL000229.1	GL000231.1	GL000210.1	GL000239.1	GL000235.1	GL000201.1	GL000247.1	GL000245.1	GL000197.1	GL000203.1	GL000246.1	GL000249.1	GL000196.1	GL000248.1	GL000244.1	GL000238.1	GL000202.1	GL000234.1	GL000232.1	GL000206.1	GL000240.1	GL000236.1	GL000241.1	GL000243.1	GL000242.1	GL000230.1	GL000237.1	GL000233.1	GL000204.1	GL000198.1	GL000208.1	GL000191.1	GL000227.1	GL000228.1	GL000214.1	GL000221.1	GL000209.1	GL000218.1	GL000220.1	GL000213.1	GL000211.1	GL000199.1	GL000217.1	GL000216.1	GL000215.1	GL000205.1	GL000219.1	GL000224.1	GL000223.1	GL000195.1	GL000212.1	GL000222.1	GL000200.1	GL000193.1	GL000194.1	GL000225.1	GL000192.1
```

# Compilation #

```
$ cd jvarkit
```
Edit the file build.properties if needed:

```
picard.jar=/path/to/picard-1.xx.jar
sam.jar=/path/to/sam-1.xx.jar
```

invoke ant
```
$ ant splitbam

splitbam:
    [mkdir] Created dir: tmp
    [javac] Compiling 1 source file to tmp
      [jar] Building jar: dist/splitbam.jar
   [delete] Deleting directory tmp

BUILD SUCCESSFUL
Total time: 1 second

```