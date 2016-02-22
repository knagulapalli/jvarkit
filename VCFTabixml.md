retrieves the annotations from a **BED** (CHROM/START/END/**XML**) and using **XLST**, annotate a VCF. See, for example [EVS2BED](EVS2BED.md), wich produces such file:

```
1	69427	69428	<snpList><positionString>1:69428</positionString><chrPosition>6942 .... 
1	69475	69476	<snpList><positionString>1:69476</positionString><chrPosition>6947 .... 
1	69495	69496	<snpList><positionString>1:69496</positionString><chrPosition>6949 .... 
1	69510	69511	<snpList><positionString>1:69511</positionString><chrPosition>6951 .... 
1	69589	69590	<snpList><positionString>1:69590</positionString><chrPosition>6959 .... 
1	69593	69594	<snpList><positionString>1:69594</positionString><chrPosition>6959 .... 
1	69619	69620	<snpList><positionString>1:69620</positionString><chrPosition>6962 .... 
1	69744	69745	<snpList><positionString>1:69745</positionString><chrPosition>6974 .... 
1	69760	69761	<snpList><positionString>1:69761</positionString><chrPosition>6976 .... 
```



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
ant vcftabixml
```

# Usage #

```
java -jar dist/vcftabixml.jar -f src.vcf.gz -x style.xsl (file.vcf|stdin)
```
# Options #

```
 -f (BED indexed with tabix. The 4th column is a XML string.) REQUIRED.
 -H (String like '##INFO=...') append extra-info header
 -x xslt-stylesheet. REQUIRED. Should produce a valid set of INFO fields.
```

# Example #

In the following example, a few variations are downloaded from EVS using [EVS2BED](EVS2BED.md). Then a small VCF is created and annotated using the bed and the following XSLT stylesheet: http://code.google.com/p/jvarkit/source/browse/trunk/src/main/java/fr/inserm/umr1087/jvarkit/tools/vcftabixml/evs2vcf.xsl


```
java -jar dist/evs2bed.jar -L 10  |\
       LC_ALL=C sort -k1,1 -k2,2n -k3,3n -k4,4 -u |\
       tabix-0.2.6/bgzip -c > test.bed.gz

/tabix-0.2.6/tabix -p bed -f test.bed.gz
java -jar  dist/vcftabixml.jar -f test.bed.gz -x  dist/evs2vcf.xsl test.vcf



echo "#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO" > test.vcf
echo "1	1	.	C	T	.	.	T=test1" >> test.vcf
echo "1	69476	.	T	C	.	.	T=test2" >> test.vcf
echo "1	69511	.	A	C	.	.	T=test3" >> test.vcf	
java -jar  dist/vcftabixml.jar -f test.bed.gz -x  dist/evs2vcf.xsl test.vcf


##Annotated with fr.inserm.umr1087.jvarkit.tools.vcftabixml.VCFTabixml:test.bed.gz
#CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO
1	1	.	C	T	.	.	T=test1;
1	69476	.	T	C	.	.	T=test2;EVS_UAMAF=0.0285;EVS_AAMAF=0.0;EVS_TOTALMAF=0.0183;EVS_AVGSAMPLEREADDEPTH=123;EVS_GENELIST=OR4F5;EVS_CONSERVATIONSCORE=0.6;EVS_CONSERVATIONSCOREGERP=2.3;EVS_RSIDS=rs148502021;EVS_CLINICALLINK=unknown;EVS_ONEXOMECHIP=false;EVS_GWASPUBMEDIDS=unknown;
1	69511	.	A	C	.	.	T=test3;EVS_UAMAF=11.2571;EVS_AAMAF=45.5899;EVS_TOTALMAF=24.0234;EVS_AVGSAMPLEREADDEPTH=69;EVS_GENELIST=OR4F5;EVS_CONSERVATIONSCORE=1.0;EVS_CONSERVATIONSCOREGERP=1.1;EVS_RSIDS=rs75062661;EVS_CLINICALLINK=unknown;EVS_ONEXOMECHIP=false;EVS_GWASPUBMEDIDS=unknown;EVS_CONFLICTALT=G;
```