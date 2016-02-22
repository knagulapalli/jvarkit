Retrieve the annotations in a VCF file indexed with **tabix** and put it into another VCF.

# Download and Compile #

```
svn checkout http://jvarkit.googlecode.com/svn/trunk/ jvarkit
cd jvarkit
```
edit the file build.properies and add the path to  the picard API:

```
picard.jar=/path/to/picard-tools-1.79/picard-1.79.jar
sam.jar=/path/to/picard-tools-1.79/sam-1.79.jar
```

and compile:

```
ant vcftabix
```

# Usage #

```
java -jar dist/vcftabix.jar -f src.vcf.gz (file.vcf|stdin)
```
# Options #

```
 -f (vcf indexed with tabix) REQUIRED.
 -T (tag String) VCF-INFO-ID optional can be used several times.
 -R doesn't use REF allele
 -A doesn't use ALT allele
 -I don't replace ID if it exists.
 -F don't replace INFO field if it exists.
 -C (TAG) use this tag in case of conflict with the ALT allele.
```

# Example #

```
java -jar dist/vcftabix.jar -f ALL.wgs.phase1_release_v3.20101123.snps_indels_sv.sites.vcf.gz \
   -T AC -T SNPSOURCE  input.vcf 
```

```

##fileformat=VCFv4.1
(....)
##Annotated with fr.inserm.umr1087.jvarkit.tools.vcftabix.VCFTabix:ALL.wgs.phase1_release_v3.20101123.snps_indels_sv.sites.vcf.gz
##INFO=<ID=AC,Number=.,Type=Integer,Description="Alternate Allele Count">
##INFO=<ID=SNPSOURCE,Number=.,Type=String,Description="indicates if a snp was called when analysing the low coverage or exome alignment data">
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	CD10977
5	2999819	rs21234	A	G	1620	.	AC=2156;AF=1.00;AN=2;DB;DP=41;Dels=0.00;FS=0.000;HaplotypeScore=0.0000;MLEAC=2;MLEAF=1.00;MQ=59.44;MQ0=0;QD=39.51;SB=-6.519e-03;SNPSOURCE=LOWCOV;	GT:AD:DP:GQ:PL	1/1:0,41:41:99:1653,123,0
(...)
```