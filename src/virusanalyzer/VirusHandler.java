package virusanalyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class VirusHandler {
	
	public boolean readVirusDefinition() {
		String fileName = "VirusDefinition/virusDef.txt";
		
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line = br.readLine();
			while(line != null) {
				String[] lineArray = line.split("\\|");
				VirusAnalyzer.virusDefinitions.add(lineArray[1]);
				VirusAnalyzer.virusNames.add(lineArray[2]);
				VirusAnalyzer.virusTypes.add(lineArray[3]);
				line = br.readLine();
			}
			
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
