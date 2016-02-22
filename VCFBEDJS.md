# Compilation #

```
ant vcfbedjs
```

# Example #

```
#ant vcfbedjs
cat input.vcf | java -jar dist/vcfbedjs.jar \
	-E segdup.js \
	-T segdup.vcfheader.txt \
	-f hg19/database/genomicSuperDups.txt.gz 
```


## segdup.headers.txt ##

```
##INFO=<ID=SEGDUP,Number=1,Type=String,Description="hg19 segmental duplication.">
##FILTER=<ID=SEGDUP,Description="hg19 segmental duplication">
```

## segdup.js ##

```
/** script jvarkit and segmental dups */
var newinfo=null;
for(var i in tabix)
	{
	var seg=tabix[i];
	if(newinfo==null)
		{
		newinfo="";
		}
	else
		{
		newinfo+="|";
		}
	var tokens=seg.split("[\t]");
	newinfo+=(tokens[7]+":"+tokens[8]+"-"+tokens[9]);
	}
if(newinfo!=null)
	{
	ctx.getFilterSet().add("SEGDUP");
	ctx.infoMap.put("SEGDUP",newinfo);
	}
```