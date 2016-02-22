annotates a VCF file with a bigwig file
# Download and Compile #

```
svn checkout http://jvarkit.googlecode.com/svn/trunk/ jvarkit
cd jvarkit
```
edit the file build.properies and add the path to the picard and bigwi API ( http://code.google.com/p/bigwig/)

```
picard.jar=/path/to/picard-tools-1.79/picard-1.79.jar
sam.jar=/path/to/picard-tools-1.79/sam-1.79.jar
bigwig.dir=/path/to/packages/bigwig.directory
```

and compile:

```
ant vcfbigwig
```

# Usage #

```
java -jar dist/vcfbigwig.jar -f file.bw (file.vcf|stdin)
```
# Options #

```
 -f (bigwig-file) required.
 -i (String) VCF-ID optional.
```

# Example #

```
$ java -jar dist/vcfbigwig.jar -f /commun/data/pubdb/ucsc/hg19/bbi/All_hg19_RS_noprefix.bw  test.vep.vcf
##fileformat=VCFv4.1
##Annotated with class fr.inserm.umr1087.jvarkit.tools.vcfbigwig.VCFBigWig:/commun/data/pubdb/ucsc/hg19/bbi/All_hg19_RS_noprefix.bw
##INFO=<ID=All_hg19_RS_noprefix,Number=1,Type=Float,Description="Annotations from /commun/data/pubdb/ucsc/hg19/bbi/All_hg19_RS_noprefix.bw">
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
22	12345	.	G	T	100.0	.	All_hg19_RS_noprefix=4.829999923706055
```