Downloads the content of the exome variant server ( http://evs.gs.washington.edu/EVS/ ) to a bed file chrom/start/end/xml

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

## Usage ##
```
Usage: java -jar evs2bed.jar > evs.bed 
```
## Options ##

```
 -S (int) download using a step of  'S' bases.  OPTIONAL.
 -L  (int) limit to 'N' records for testing.  OPTIONAL.
 --proxyHost <string> optional
 --proxyPort <string> optional
```


## Example ##
Download and index the data with tabix:

```
java -jar dist/evs2bed.jar -L 10 | LC_ALL=C sort -k1,1 -k2,2n -k3,3n -k4,4 -u |\
		/path/totabix-0.2.6/bgzip -c > test.bed.gz
/path/totabix-0.2.6/tabix -p bed -f test.bed.gz


$ gunzip -c  test.bed.gz  | cut -c 1-500 | fold -w 80
1	69427	69428	<snpList><positionString>1:69428</positionString><chrPos
ition>69428</chrPosition><alleles>G/T</alleles><uaAlleleCounts>G=313/T=6535</uaA
lleleCounts><aaAlleleCounts>G=14/T=3808</aaAlleleCounts><totalAlleleCounts>G=327
/T=10343</totalAlleleCounts><uaMAF>4.5707</uaMAF><aaMAF>0.3663</aaMAF><totalMAF>
3.0647</totalMAF><avgSampleReadDepth>110</avgSampleReadDepth><geneList>OR4F5</ge
neList><snpFunction><chromosome>1</chromosome><position>69428</position><conserv
ationScore>1.0</conservationSc
1	69475	69476	<snpList><positionString>1:69476</positionString><chrPos
ition>69476</chrPosition><alleles>C/T</alleles><uaAlleleCounts>C=2/T=7020</uaAll
eleCounts><aaAlleleCounts>C=0/T=3908</aaAlleleCounts><totalAlleleCounts>C=2/T=10
928</totalAlleleCounts><uaMAF>0.0285</uaMAF><aaMAF>0.0</aaMAF><totalMAF>0.0183</
totalMAF><avgSampleReadDepth>123</avgSampleReadDepth><geneList>OR4F5</geneList><
snpFunction><chromosome>1</chromosome><position>69476</position><conservationSco
re>0.6</conservationScore><con
1	69495	69496	<snpList><positionString>1:69496</positionString><chrPos
ition>69496</chrPosition><alleles>A/G</alleles><uaAlleleCounts>A=2/G=6764</uaAll
eleCounts><aaAlleleCounts>A=23/G=3785</aaAlleleCounts><totalAlleleCounts>A=25/G=
10549</totalAlleleCounts><uaMAF>0.0296</uaMAF><aaMAF>0.604</aaMAF><totalMAF>0.23
64</totalMAF><avgSampleReadDepth>91</avgSampleReadDepth><geneList>OR4F5</geneLis
t><snpFunction><chromosome>1</chromosome><position>69496</position><conservation
Score>0.5</conservationScore><
1	69510	69511	<snpList><positionString>1:69511</positionString><chrPos
ition>69511</chrPosition><alleles>G/A</alleles><uaAlleleCounts>G=5337/A=677</uaA
lleleCounts><aaAlleleCounts>G=1937/A=1623</aaAlleleCounts><totalAlleleCounts>G=7
274/A=2300</totalAlleleCounts><uaMAF>11.2571</uaMAF><aaMAF>45.5899</aaMAF><total
MAF>24.0234</totalMAF><avgSampleReadDepth>69</avgSampleReadDepth><geneList>OR4F5
</geneList><snpFunction><chromosome>1</chromosome><position>69511</position><con
servationScore>1.0</conservati
1	69589	69590	<snpList><positionString>1:69590</positionString><chrPos
ition>69590</chrPosition><alleles>A/T</alleles><uaAlleleCounts>A=0/T=6214</uaAll
eleCounts><aaAlleleCounts>A=1/T=3555</aaAlleleCounts><totalAlleleCounts>A=1/T=97
69</totalAlleleCounts><uaMAF>0.0</uaMAF><aaMAF>0.0281</aaMAF><totalMAF>0.0102</t
otalMAF><avgSampleReadDepth>119</avgSampleReadDepth><geneList>OR4F5</geneList><s
npFunction><chromosome>1</chromosome><position>69590</position><conservationScor
e>0.8</conservationScore><cons
1	69593	69594	<snpList><positionString>1:69594</positionString><chrPos
ition>69594</chrPosition><alleles>C/T</alleles><uaAlleleCounts>C=2/T=6190</uaAll
eleCounts><aaAlleleCounts>C=0/T=3548</aaAlleleCounts><totalAlleleCounts>C=2/T=97
38</totalAlleleCounts><uaMAF>0.0323</uaMAF><aaMAF>0.0</aaMAF><totalMAF>0.0205</t
otalMAF><avgSampleReadDepth>109</avgSampleReadDepth><geneList>OR4F5</geneList><s
npFunction><chromosome>1</chromosome><position>69594</position><conservationScor
e>0.8</conservationScore><cons
1	69619	69620	<snpList><positionString>1:69620</positionString><chrPos
ition>69620</chrPosition><alleles>T/TA</alleles><uaAlleleCounts>A1=2/R=4694</uaA
lleleCounts><aaAlleleCounts>A1=0/R=2954</aaAlleleCounts><totalAlleleCounts>A1=2/
R=7648</totalAlleleCounts><uaMAF>0.0426</uaMAF><aaMAF>0.0</aaMAF><totalMAF>0.026
1</totalMAF><avgSampleReadDepth>56</avgSampleReadDepth><geneList>OR4F5</geneList
><snpFunction><chromosome>1</chromosome><position>69620</position><conservationS
core>0.7</conservationScore><c
1	69744	69745	<snpList><positionString>1:69745</positionString><chrPos
ition>69745</chrPosition><alleles>CA/C</alleles><uaAlleleCounts>A1=4/R=4254</uaA
lleleCounts><aaAlleleCounts>A1=0/R=2716</aaAlleleCounts><totalAlleleCounts>A1=4/
R=6970</totalAlleleCounts><uaMAF>0.0939</uaMAF><aaMAF>0.0</aaMAF><totalMAF>0.057
4</totalMAF><avgSampleReadDepth>10</avgSampleReadDepth><geneList>OR4F5</geneList
><snpFunction><chromosome>1</chromosome><position>69745</position><conservationS
core>0.9</conservationScore><c
1	69760	69761	<snpList><positionString>1:69761</positionString><chrPos
ition>69761</chrPosition><alleles>T/A</alleles><uaAlleleCounts>T=645/A=4093</uaA
lleleCounts><aaAlleleCounts>T=62/A=2840</aaAlleleCounts><totalAlleleCounts>T=707
/A=6933</totalAlleleCounts><uaMAF>13.6133</uaMAF><aaMAF>2.1365</aaMAF><totalMAF>
9.2539</totalMAF><avgSampleReadDepth>8</avgSampleReadDepth><geneList>OR4F5</gene
List><snpFunction><chromosome>1</chromosome><position>69761</position><conservat
ionScore>0.1</conservationScor

```