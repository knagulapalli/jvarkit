package fr.inserm.umr1087.jvarkit.tools.vcfbed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


import net.sf.samtools.tabix.TabixReader;
import net.sf.samtools.util.BlockCompressedInputStream;


public class VCFBed
	{
	private CompiledScript  script=null;
	private ScriptEngine engine=null;

	private PrintStream out=System.out;
	private String tabixFile;
	private TabixReader tabixReader =null;
	private Set<String> infoIds=new LinkedHashSet<String>();
	
	
	private void run(BufferedReader in) throws Exception
		{
		Bindings bindings = this.engine.createBindings();
		Pattern tab=Pattern.compile("[\t]");
		String line;
		while((line=in.readLine())!=null)
			{

			if(line.isEmpty()) continue;
			
			if(line.startsWith("#"))
				{
				if(line.startsWith("#CHROM"))
					{
					out.println("##Annotated with "+getClass()+":"+tabixFile);
					if(!this.infoIds.isEmpty())
						{
						BlockCompressedInputStream src=new BlockCompressedInputStream(new File(tabixFile));
						BufferedReader r2=new BufferedReader(new InputStreamReader(src));
						String line2;
						while((line2=r2.readLine())!=null)
							{
							if(line2.startsWith("#CHROM")) break;
							if(!line2.startsWith("##")) break;
							if(!line2.startsWith("##INFO=")) continue;
							
							int i=line2.indexOf("ID=");
							if(i==-1) continue;
							int j=line2.indexOf(',',i+1);
							if(j==-1) j=line2.indexOf('>',i+1);
							if(j==-1) continue;
							if(this.infoIds.contains(line2.substring(i+3,j)))
								{
								this.out.println(line2);
								}
							} 
						r2.close();
						}
					
					out.println(line);
					continue;
					}
				out.println(line);
				continue;
				}
			
			String tokens[]=tab.split(line,9);
			if(tokens.length<8)
				{
				System.err.println("[VCFTabix] Error not enough columns in vcf: "+line);
				continue;
				}
			String chrom=tokens[0];
			Integer pos1=Integer.parseInt(tokens[1]);
			
			
			List<String> tabixrows=new ArrayList<String>();
			TabixReader.Iterator iter=tabixReader.query(chrom+":"+pos1+"-"+(pos1+1));
			String line2;
			
			while(iter!=null && (line2=iter.next())!=null)
				{
				tabixrows.add(line2);
				}
			bindings.put("chrom",chrom);
			bindings.put("pos",pos1);
			bindings.put("id",tokens[2]);
			bindings.put("ref",tokens[3]);
			bindings.put("alt",tokens[4]);
			bindings.put("tabix",tabixrows);
			
			Object result = script.eval(bindings);
			if(result!=null)
				{
				
				}
			
			StringBuilder b=new StringBuilder();
			
			
			for(int i=0;i< tokens.length;++i)
				{
				if(i>0) out.print('\t');
				out.print(i==7?b.toString():tokens[i]);
				}
			out.println();
			}
		
		}
	public int run(String[] args) throws Exception
		{
		String scriptStr=null;
		File scriptFile=null;

		int optind=0;
		while(optind<args.length)
			{
			if(args[optind].equals("-h"))
				{
				System.out.println("VCF Tabix. Author: Pierre Lindenbaum PhD. 2013.");
				System.out.println("Usage: java -jar vcftabix.jar -f src.vcf.gz (file.vcf|stdin) " );
				System.out.println(" -f (vcf indexed with tabix) REQUIRED.");
				System.out.println(" -T (tag String) VCF-INFO-ID optional can be used several times.");
				}
			else if(args[optind].equals("-e") && optind+1< args.length)
				{
				scriptStr=args[++optind];
				}
			else if(args[optind].equals("-f") && optind+1< args.length)
				{
				scriptFile=new File(args[++optind]);
				}
			else if(args[optind].equals("-T") && optind+1< args.length)
				{
				this.infoIds.add(args[++optind]);
				}
			else if(args[optind].equals("-f") && optind+1< args.length)
				{
				this.tabixFile=args[++optind];
				}
			else if(args[optind].equals("--"))
				{
				optind++;
				break;
				}
			else if(args[optind].startsWith("-"))
				{
				System.err.println("Unnown option: "+args[optind]);
				return -1;
				}
			else
				{
				break;
				}
			++optind;
			}
		
		ScriptEngineManager manager = new ScriptEngineManager();
		this.engine = manager.getEngineByName("js");
		if(this.engine==null)
			{
			System.err.println("not available: javascript. Use the SUN/Oracle JDK ?");
			System.exit(-1);
			}
		
		Compilable compilingEngine = (Compilable)this.engine;
		this.script = null;
		if(scriptFile!=null)
			{
			FileReader r=new FileReader(scriptFile);
			this.script=compilingEngine.compile(r);
			r.close();
			}
		else
			{
			this.script=compilingEngine.compile(scriptStr);
			}

		
		if(tabixFile==null)
			{
			System.err.println("Undefined tabix File");
			return -1;
			}
		
		this.tabixReader=new TabixReader(this.tabixFile);
		
		
		
		if(optind==args.length)
			{
			this.run(new BufferedReader(new InputStreamReader(System.in)));
			}
		else if(optind+1==args.length)
			{
			String inputName=args[optind++];
			BufferedReader in=new BufferedReader(new FileReader(inputName));
			this.run(in);
			in.close();
			}
		else
			{
			System.err.println("Illegal Number of arguments");
			return -1;
			}
		return 0;
		}
	
	public static void main(String[] args) throws Exception
		{
		VCFBed app=new VCFBed();
		app.run(args);
		}
}