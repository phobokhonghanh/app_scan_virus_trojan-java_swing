package virusanalyzer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.net.*;
import java.nio.channels.FileChannel;
import java.util.*;

public class VirusAnalyzer {
    public static ArrayList<String> virusDefinitions = new ArrayList<>();
    public static ArrayList<String> virusNames = new ArrayList<>();
    public static ArrayList<String> virusTypes = new ArrayList<>();
    public static VirusHandler virusHandler = new VirusHandler();
    public static boolean isVirus = virusHandler.readVirusDefinition();
    private static int scanCount = 1;

    public static ArrayList<Object[]> infectedFiles = new ArrayList<>(); // 20130340
    public static File fileSession; // 20130340
    public static File fileError; // 20130340

    public static ArrayList<Object[]> scanFolder(File folder) {
        ArrayList<Object[]> listVirus = new ArrayList<>();
        Stack<File> stack = new Stack<>();
        stack.push(folder);
        while (!stack.isEmpty()) {
            File currentFolder = stack.pop();
            if (currentFolder.isDirectory()) {
                File[] files = currentFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            stack.push(file);
                        } else {
                            Object[] isVirus = checkVirus(file);
                            if (isVirus != null) {
                                listVirus.add(isVirus);
                            }
                        }
                    }
                }
            }
        }
        if (listVirus.size() > 0) {
            writeVirusToFile(listVirus);
            infectedFiles = listVirus;
        }
        return listVirus;
    }

    public static Object[] checkVirus(File file) {
        if (file != null) {
            AnalyzingLogic logic = new AnalyzingLogic();
            String fileChecksum = "";
            try {
                fileChecksum = logic.md5Generator(file.toString());
                if (isVirus) {
                    int index = logic.analyze(fileChecksum, virusDefinitions);
                    if (index != -1) {
                        return new Object[]{file.getAbsolutePath(), virusTypes.get(index).toString(),
                                virusNames.get(index).toString()};
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //20130340
    public static boolean deleteFile(File file) {
        try {
            if (file.exists()) {
                System.out.println(file);
                return file.delete();
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    // 20130340
    public static boolean deleteFileInfected(String filePath) {
        File file = new File(filePath);
        if (deleteFile(file)) {
            System.out.println("true");
            for (Object[] o : infectedFiles) {
                for (Object s : o) {
                    if (s.equals(filePath)) {
                        infectedFiles.remove(o);
                        return updateFile();
                    }
                }
            }
        } else {
            System.out.println("false");
            fileError = file;
        }
        return false;
    }
    //20130340
    public static boolean deleteFilesInfected(ArrayList<String> listChoose) {
        // xóa hết tất cả
        // basic: xoa thanh cong -> xoa het tat ca cac file - xoa het tat ca bo nho - xoa luon file session
        // alt : xoa khong thanh cong ->  xoa khong thanh cong file nao -> return false
        for (String s : listChoose) {
            if (!deleteFileInfected(s)) {
                return false;
            }
        }
        if (infectedFiles.size() == 0) {
            // delete file session
            deleteFile(fileSession);
            // file session == null
            fileSession = null;
        }
        return true;
    }

    // 20130340
    public static boolean updateFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileSession))) {
            if (infectedFiles != null) {
                for (Object[] objects : infectedFiles) {
                    String outputString = String.join(",",
                            Arrays.stream(objects).map(Object::toString).toArray(CharSequence[]::new)) + "\n";
                    bw.write(outputString);
                }
                bw.close();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    // 20130340
    public static ArrayList<Object[]> findFile(String keyWords) {
        ArrayList<Object[]> rs = new ArrayList<>();
        for (Object[] o : infectedFiles) {
            ArrayList<String> array = new ArrayList<>();
            for (Object obj : o) {
                array.add((String) obj);
            }
            for (String s : array) {
                if (s.contains(keyWords)) {
                    if (!rs.contains(o))
                        rs.add(o);
                }
            }
        }
        return rs;
    }

    public static void writeVirusToFile(ArrayList<Object[]> listVirus) {
        String dateStr = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File outFile = new File("Virus/virus_" + dateStr + "_scan" + scanCount + ".txt");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {
            if (listVirus != null) {
                for (Object[] objects : listVirus) {
                    String outputString = String.join(",",
                            Arrays.stream(objects).map(Object::toString).toArray(CharSequence[]::new)) + "\n";
                    bw.write(outputString);
                }
                bw.close();
            }
        } catch (Exception e) {
        }
        scanCount++;
        fileSession = outFile;
    }

    public static ArrayList<Object[]> readVirusFromFile(File file) {
        ArrayList<Object[]> listVirus = new ArrayList<>();
        if (file != null && file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    Object[] virus = new Object[values.length];
                    for (int i = 0; i < values.length; i++) {
                        virus[i] = values[i];
                    }
                    listVirus.add(virus);
                }
                br.close();
            } catch (Exception e) {
            }
        }
        infectedFiles = listVirus; // 20130340
        fileSession = file; // 20130340
        return listVirus;
    }
	@SuppressWarnings("resource")
	public static void UpdateApp(File sourceFile, File destFile) throws IOException {
		// Kiểm tra nếu tệp tin đích đã tồn tại, xóa nó đi
		if (destFile.exists()) {
			destFile.delete();
		}

		// sao chép nội dung của tệp tin nguồn vào tệp tin đích
		FileChannel sourceChannel = null;
		FileChannel destChannel = null;
		try {
			sourceChannel = new FileInputStream(sourceFile).getChannel();
			destChannel = new FileOutputStream(destFile).getChannel();
			destChannel.transferFrom(sourceChannel, 0, sourceChannel.size()); // Sao chép nội dung
		} finally {
			// Đóng các kênh để giải phóng tài nguyên
			if (sourceChannel != null) {
				sourceChannel.close();
			}
			if (destChannel != null) {
				destChannel.close();
			}
		}
	}

	// 2.5 tạo hàm downloadAndSaveFile
	public static void downloadAndSaveFile(String fileUrl, String saveDir) {
		try {
			URL url = new URL(fileUrl);
			URLConnection conn = url.openConnection();
			InputStream in = conn.getInputStream();
			FileOutputStream out = new FileOutputStream(saveDir);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		} catch (IOException e) {
			// Handle any exceptions that may occur
			e.printStackTrace();
		}
	}
}
