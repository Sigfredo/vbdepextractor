package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import config.Constants;

public class BruteParser {
	
	private HashSet<String> variaveis = new HashSet<String>();
	private HashSet<String> dependencias = new HashSet<String>();
	
	
	public void traduzir(){
		try(BufferedReader br = new BufferedReader(new FileReader(Constants.FILE_NAME))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    
		    while (line != null) {
		    		    	
		    	if (isVariavel(line)){
		    		extrairVariavel(line);
		    	}
		    	
		    	if (isChamada(line)){
		    		extrairDependencia(line);
		    	}	
		        line = br.readLine();
		    };
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo não encontrado");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Variáveis: \n");

		for (String string : variaveis) {
			System.out.println(string);
		}

		System.out.println("Dependencias: \n");

		for (String string : dependencias) {
			System.out.println(string);
		}
		
	}
	
	public boolean isChamada(String line){
		Pattern pattern = Pattern.compile("\\s\\w+\\.\\w+\\s");
		//Pattern pattern = Pattern.compile("[\\w]");
		Matcher matcher = pattern.matcher(line);
		if (line.contains(".") && matcher.find()){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isVariavel(String line){
		if (line.contains("Begin ") | line.contains("BeginProperty ") | line.contains("Dim ")){
			return true;
		}else{
			return false;
		}
	}
	
	public void extrairDependencia(String line){
		String parts[] = line.split(Pattern.quote("."));
		if (parts[0].contains(" ")){
			String parts2[] = parts[0].split(" ");
			if (!variaveis.contains(parts2[parts2.length - 1])){
				dependencias.add(Constants.FILE_NAME + ",access," + parts2[parts2.length - 1]);
			}
		}
	} 
	
	public void extrairVariavel(String line){
		if (line.contains("Begin ")){
			String[] parts = line.split(" ");
			variaveis.add(parts[parts.length - 1]);
		}else if (line.contains("BeginProperty ")){
			String[] parts = line.split(" ");
			variaveis.add(parts[0]);
		}else if (line.contains("Dim ")){
			String[] parts = line.split(" ");
			variaveis.add(parts[0]);
		}
	}


}
