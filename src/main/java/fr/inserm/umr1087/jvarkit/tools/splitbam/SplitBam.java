package fr.inserm.umr1087.jvarkit.tools.splitbam;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.picard.reference.IndexedFastaSequenceFile;
import net.sf.samtools.DefaultSAMRecordFactory;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileHeader.SortOrder;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMFileReader.ValidationStringency;
import net.sf.samtools.SAMRecordFactory;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMSequenceRecord;

public class SplitBam
	{
	private static final Logger LOG=Logger.getLogger("split");
	private final String REPLACE_CHROM="__CHROM__";
	private String outFilePattern="";
	private String underterminedName="Unmapped";
	private boolean generate_empty_bams=false;
	private SAMSequenceDictionary  samSequenceDictionary;
	private boolean if_bam_empty_add_mock_sam_record=false;
	private long id_generator=System.currentTimeMillis();
	private boolean input_is_sorted=false;
	private boolean create_index=false;
	private File tmpDir=null;

	private SplitBam()
		{
		
		}
	
	private void addMockPair(
			SAMFileWriter sw,
			SAMFileHeader header
			) throws IOException
		{
		List<SAMReadGroupRecord> G=header.getReadGroups();
		String bases="NNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN";
		SAMRecordFactory f=new DefaultSAMRecordFactory();
		++id_generator;
		for(int i=0;i< 2;++i)
			{
			SAMRecord rec=f.createSAMRecord(header);
			rec.setFirstOfPairFlag(i%2==0);
			rec.setReadBases(bases.getBytes());
			rec.setMappingQuality(0);
			rec.setBaseQualityString(bases.replace('N', 'I'));
			rec.setReadUnmappedFlag(true);
			rec.setMateUnmappedFlag(true);
			rec.setReadPairedFlag(true);
			rec.setReadName("MOCKREAD"+(id_generator)+":6:190:289:82");
			rec.setAttribute("MK",1);
			if(G!=null && !G.isEmpty())
				{
				rec.setAttribute("RG", G.get(0).getId());
				}
			sw.addAlignment(rec);
			}
		}
	
	private void createEmptyFile(
			SAMFileWriterFactory sf,
			SAMFileHeader header,
			String chromName
			) throws IOException
		{
		File fileout=new File(this.outFilePattern.replaceAll(REPLACE_CHROM, chromName));
		LOG.info("creating mock BAM file "+fileout);
		File parent=fileout.getParentFile();
		if(parent!=null) parent.mkdirs();

		SAMFileWriter sw=sf.makeBAMWriter(header, true, fileout);
		if(if_bam_empty_add_mock_sam_record)
			{
			addMockPair(sw,header);
			}
		sw.close();
		}
	
	private void scan(InputStream in) throws Exception
		{
		Set<String> all_chromosomes=new HashSet<String>();
		for(SAMSequenceRecord seq:this.samSequenceDictionary.getSequences())
			{
			all_chromosomes.add(seq.getSequenceName());
			}
		Map<String,SAMFileWriter> seen=new HashMap<String,SAMFileWriter>(all_chromosomes.size());
		SAMFileReader samFileReader=new SAMFileReader(in);
		samFileReader.setValidationStringency(ValidationStringency.SILENT);
		SAMFileHeader header=samFileReader.getFileHeader();
		header.setSortOrder(SortOrder.coordinate);
		
        SAMFileWriterFactory sf=new SAMFileWriterFactory();
       if(this.tmpDir!=null)
    	   {
    	   sf.setTempDirectory(this.tmpDir);
    	   }
        sf.setCreateIndex(this.create_index);
        
        long nrecords=0L;
       
       
        
		for(Iterator<SAMRecord> iter=samFileReader.iterator();
				iter.hasNext(); )
			{
			SAMRecord record=iter.next();
			++nrecords;
			if(nrecords%1E6==0)
				{
				LOG.info("nRecord:"+nrecords);
				}
			String recordChrom=null;
			if( record.getReadUnmappedFlag() )
				{
				if(record.getMateUnmappedFlag())
					{
					recordChrom=this.underterminedName;
					}
				else
					{
					recordChrom=record.getMateReferenceName();
					}
				}
			else
				{
				recordChrom=record.getReferenceName();
				if(!all_chromosomes.contains(recordChrom))
					{
					throw new IOException("Undefined chromosome "+recordChrom+" (not in ref dictionary "+all_chromosomes+").");
					}
				}
			SAMFileWriter writer=seen.get(recordChrom);
			
			if(writer==null)
				{
				File fileout=new File(this.outFilePattern.replaceAll(REPLACE_CHROM, recordChrom));
				LOG.info("opening "+fileout);
				File parent=fileout.getParentFile();
				if(parent!=null) parent.mkdirs();
				writer=sf.makeBAMWriter(header,this.input_is_sorted,fileout);
				seen.put(recordChrom, writer);
				nrecords=0L;
				}
			
			writer.addAlignment(record);
			}
		
		for(String k:seen.keySet())
			{
			LOG.info("closing "+k);
			seen.get(k).close();
			}
		samFileReader.close();
		
		if(generate_empty_bams)
			{
			for(SAMSequenceRecord seq:this.samSequenceDictionary.getSequences())
				{
				if(seen.containsKey(seq.getSequenceName())) continue;
				createEmptyFile(sf,header, seq.getSequenceName());
				}
			if(!seen.containsKey(this.underterminedName))
				{
				createEmptyFile(sf,header, this.underterminedName);
				}
			}
		
		}
	
	
	private void run(String[] args)
		throws Exception
		{
		File referenceFile=null;
		
		int optind=0;
		while(optind< args.length)
			{
			if(args[optind].equals("-h") ||
			   args[optind].equals("-help") ||
			   args[optind].equals("--help"))
				{
				System.err.println("Pierre Lindenbaum PhD. 2013");
				System.err.println("Options:");
				System.err.println(" -h help; This screen.");
				System.err.println(" -R (reference file) REQUIRED.");
				System.err.println(" -u (unmapped chromosome name): default:"+this.underterminedName);
				System.err.println(" -e | --empty : generate EMPTY bams for chromosome having no read mapped");
				System.err.println(" -m | --mock : if option '-e', add a mock pair of sam records to the bam");
				System.err.println(" -p (output file/bam pattern) REQUIRED. MUST contain "+REPLACE_CHROM+" and end with .bam");
				System.err.println(" -s assume input is sorted.");
				System.err.println(" -x | --index  create index.");
				System.err.println(" -t | --tmp  (dir) tmp file directory");
				return;
				}
			else if(args[optind].equals("-e")|| args[optind].equals("--empty"))
				{
				this.generate_empty_bams=true;
				}
			else if(args[optind].equals("-m") || args[optind].equals("--mock"))
				{
				this.generate_empty_bams=true;
				this.if_bam_empty_add_mock_sam_record=true;
				}
			else if((args[optind].equals("-t") || args[optind].equals("--tmp")) && optind+1< args.length)
				{
				this.tmpDir=new File(args[++optind]);
				}
			else if(args[optind].equals("-R") && optind+1< args.length)
				{
				referenceFile=new File(args[++optind]);
				}
			else if(args[optind].equals("-u") && optind+1< args.length)
				{
				underterminedName= args[++optind];
				}
			else if(args[optind].equals("-p") && optind+1< args.length)
				{
				outFilePattern= args[++optind];
				}
			else if(args[optind].equals("-s"))
				{
				this.input_is_sorted=true;
				}
			else if(args[optind].equals("-x") || args[optind].equals("--index"))
				{
				this.create_index=true;
				}
			else if(args[optind].equals("--"))
				{
				optind++;
				break;
				}
			else if(args[optind].startsWith("-"))
				{
				System.err.println("Unknown option "+args[optind]);
				return;
				}
			else 
				{
				break;
				}
			++optind;
			}
		if(!outFilePattern.contains(REPLACE_CHROM))
			{
			System.err.println("output file pattern undefined or doesn't contain "+REPLACE_CHROM);
			System.exit(-1);
			}
		
		if(referenceFile==null)
			{
			System.err.println("Reference file undefined");
			System.exit(-1);
			}
		IndexedFastaSequenceFile indexedFastaSequenceFile=new IndexedFastaSequenceFile(referenceFile);
		this.samSequenceDictionary=indexedFastaSequenceFile.getSequenceDictionary();
		if(this.samSequenceDictionary==null)
			{
			System.err.println("Reference file dictionary missing. use picard to create it.");
			System.exit(-1);
			}
		
		if(optind==args.length)
			{
			scan(System.in);
			}
		else if(optind+1==args.length)
			{
			FileInputStream fin=new FileInputStream(args[optind]);
			scan(fin);
			fin.close();
			}
		else 
			{
			System.err.println("illegal number of arguments.");
			System.exit(-1);
			}
		}
		
	public static void main(String[] args) throws Exception
		{
		new SplitBam().run(args);
		}
	
	}