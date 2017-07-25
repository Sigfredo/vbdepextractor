package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import config.Constants;

public class BruteParser {
	
	private HashSet<String> variaveisUpperCase = new HashSet<String>();
	private HashSet<String> dependencias = new HashSet<String>();
	
	
	public void traduzir(){
		
		try (Stream<Path> paths = Files.walk(Paths.get(Constants.FOLDER_PATH))) {
		    paths
		        .filter(Files::isRegularFile)
		        .forEach(arquivo->lerArquivo(arquivo));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		FileWriter writer;
		try {
			writer = new FileWriter(Constants.OUTPUT_FILE);
			for(String dep: dependencias) {
				  writer.write(dep+ "\n");
				}
				writer.close();
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	public boolean isChamada(String line){
		Pattern pattern = Pattern.compile("\\s\\w+\\.\\w+\\s");
		//Pattern pattern = Pattern.compile("[\\w]");
		Matcher matcher = pattern.matcher(line);
		if (matcher.find()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isVariavel(String line){
		if (line.contains("Begin ") | line.contains("BeginProperty ") | line.contains("Dim ") || line.contains(" As ")){
			return true;
		}else{
			return false;
		}
	}
	
	public void extrairDependencia(String line, Path path){
		
		String[] s = path.getParent().toString().split("\\\\");	
		
		Pattern pattern = Pattern.compile("\\w+\\.\\w+");
		Matcher matcher = pattern.matcher(line);			
		if (matcher.find() && !variaveisUpperCase.contains(matcher.group(0).split("\\.")[0].toUpperCase())){
			dependencias.add(s[s.length - 1]+"."+path.getFileName().toString().replaceFirst("[.][^.]+$", "") + ",access," + matcher.group(0));
		}
		
	} 
	
//	public void extrairDependencia(String line, Path path){
//		String[] s = path.getParent().toString().split("\\\\");		
//		String parts[] = line.split(Pattern.quote("."));
//		if (parts[0].contains(" ")){
//			String parts2[] = parts[0].split(" ");
//			if (!variaveis.contains(parts2[parts2.length - 1])){
//				dependencias.add(s[s.length - 1]+"."+path.getFileName() + ",access," + parts2[parts2.length - 1]);
//			}
//		}
//	} 
	
	public void extrairVariavel(String line){
		if (line.contains("Begin ")){
			String[] parts = line.split(" ");
			variaveisUpperCase.add(parts[parts.length - 1].toUpperCase());
		}else if (line.contains("BeginProperty ")){
			String[] parts = line.trim().split(" ");
			variaveisUpperCase.add(parts[1].toUpperCase());
		}else if (line.contains("Dim ")){
			String[] parts = line.trim().split(" ");
			variaveisUpperCase.add(parts[1].toUpperCase());
		}else if (line.contains(" As ")){
			String[] parts = line.trim().substring(0,line.trim().lastIndexOf(" As ")).split(" ");
			variaveisUpperCase.add(parts[parts.length-1].toUpperCase());
		}else{
			System.out.println(line);
		}
	}
	
	public void lerArquivo(Path path){
		
		String[] nome_split = path.toString().split("\\.");
		if (nome_split[nome_split.length - 1].equals("frm") || nome_split[nome_split.length - 1].equals("cls")){
			
			variaveisUpperCase = new HashSet<String>(); 
			
			try(BufferedReader br = new BufferedReader(new FileReader(path.toString()))) {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();
			    
			    while (line != null) {
			    		    	
			    	if (isVariavel(line)){
			    		extrairVariavel(line);
			    	}
			    	
			    	if (isChamada(line)){
			    		extrairDependencia(line,path);
			    	}	
			        line = br.readLine();
			    };
			} catch (FileNotFoundException e) {
				System.out.println("Arquivo não encontrado");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}else{
			//System.out.println("Arquivo ignorado: "+path.toString());
		}
		//System.out.println(path.getParent().toString().split("\\")[]);
		//String[] s = path.getParent().toString().split("\\\\");
		//System.out.println(s[s.length - 1]);
		//System.out.println(path.getParent().toString().substring(path.getParent().lastIndexOf(File.separator) + 1));
		//System.out.println(path.);
		
		
//		System.out.println("Dependencias: \n");
//
//		for (String string : dependencias) {
//			System.out.println(string);
		
	}


}
