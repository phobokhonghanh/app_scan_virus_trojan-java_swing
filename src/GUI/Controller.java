package GUI;

import virusanalyzer.VirusAnalyzer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Controller implements ActionListener, MouseListener {

    MainView view;

    public Controller(MainView view) {
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (view.btn_scan_simple == e.getSource()) {
        	scanfolder();
        }
        if (view.btn_scan_all == e.getSource()) {
            scanSystem();
        }
        if (view.btn_quit == e.getSource() || view.jMenuItem_quit == e.getSource()) {
            System.exit(0);
        }
        //20130340
        if (view.btn_view == e.getSource()) {
            updateDataTable(VirusAnalyzer.readVirusFromFile(fileChooser()));
        }
        // 20130340
        if (view.btn_deltete == e.getSource()) {
            // duyệt hết 1 lần trong bảng -> lấy ra danh sách file đã được chọn
            // duyệt hết danh sách file đã được chọn và xóa
            if (VirusAnalyzer.infectedFiles.size() == 0) {
                JOptionPane.showMessageDialog(this.view, "Không tìm thấy file", "Thông báo", JOptionPane.ERROR_MESSAGE);
            } else {
                if (VirusAnalyzer.deleteFilesInfected(listChoose())) {
                    updateDataTable(VirusAnalyzer.infectedFiles);
                    // hien thi thong bao xoa thanh cong
                    JOptionPane.showMessageDialog(this.view, "Xóa thành công", "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // hien thi thong bao xoa khong thanh cong
                    updateDataTable(VirusAnalyzer.infectedFiles);
                    JOptionPane.showMessageDialog(this.view, "Xóa " + VirusAnalyzer.fileError.getName() + " không thành công", "Thông báo",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        }
        // 20130340
        if (view.checkbox == e.getSource()) {
            for (int i = 0; i < view.table_view.getRowCount(); i++) {
                if (view.checkbox.isSelected()) {
                    view.table_model.setValueAt(true, i, 3);
                } else {
                    view.table_model.setValueAt(false, i, 3);
                }
            }
        }
        // 20130340
        if (view.btn_search == e.getSource()) {
            ArrayList<Object[]> arrayList = VirusAnalyzer.findFile(view.field_search.getText());
            if (arrayList.size() == 0) {
                JOptionPane.showMessageDialog(this.view, "Không tìm thấy file", "Thông báo", JOptionPane.ERROR_MESSAGE);
            } else {
                updateDataTable(arrayList);
            }
        }
        if (view.btn_update == e.getSource()) {
			// Hệ thống kiểm tra nếu có phiên bản mới thì thông báo cho người dùng
			if (getVersion().equals(getLatestVersion())) {
				JOptionPane.showMessageDialog(null, "Bạn đang ở phiên bản mới nhất");
			} else {
				// Hiển thị đường dẫn đến tệp cập nhật mà người dùng chọn
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(null);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					String filePath = selectedFile.getAbsolutePath();
					System.out.println(filePath);
					File targetFile = new File("VirusDefinition/virusDef.txt"); // đích
					if (targetFile.exists()) {
						// Hiện thị thông báo bạn có muôn cập nhật
						int option = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn cập nhật không?");
						if (option == JOptionPane.YES_OPTION) {
							// Nếu người dùng chọn thay thế, thực hiện thay thế tệp tin đích bằng tệp tin
							// người dùng đã chọn
							try {
								// 1.5 Gọi hàm UpdateApp đưa đường dẫn của file nguồn đến file đích
								VirusAnalyzer.UpdateApp(selectedFile, targetFile);
								view.jLabel_date.setText(getLatestVersion());
								// 1.6 Hiện thị thông báo bạn có muôn cập nhật
								JOptionPane.showMessageDialog(null, "Đã cập nhật tệp tin thành công!");
							} catch (IOException ex) {
								JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
							}
						}
					} else {

						try {
							// 1.6 Hiện thị thông báo bạn có muôn cập nhật
							// Nếu tệp tin đích chưa tồn tại, thực hiện tạo mới tệp tin đích và ghi đường
							// dẫn đến tệp tin người dùng đã chọn vào tệp tin đích
							VirusAnalyzer.UpdateApp(selectedFile, targetFile);
							view.jLabel_date.setText(getLatestVersion());
							JOptionPane.showMessageDialog(null, "Đã cập nhật tệp tin thành công!");
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
						}
					}
				}

			}

        // Cập nhật tự động
        if (view.btn_auto_update == e.getSource()) {
			// Hệ thống kiểm tra nếu có phiên bản mới thì thông báo cho người dùng
			if (getVersion().equals(getLatestVersion())) {
				JOptionPane.showMessageDialog(null, "Bạn đang ở phiên bản mới nhất");
			} else {
				// Người dùng chọn nút cập nhật tự động
				String fileUrl = "https://www.mediafire.com/file/r6azh3lha3vh20l/virusDef.txt/file"; // Đường dẫn đến //
				// file cần tải
				String saveDir = "D:\\visu.txt"; // Thư mục lưu file
				try {
					// Hệ thống sẽ tự động kiểm tra phiên bản hiện tại và nếu có phiên bản mới hệ
					// thống sẽ tự động tải về và tiến hành cập nhật

					File selectedFile = new File(saveDir);
					File targetFile = new File("VirusDefinition/virusDef.txt"); // đích
					// 2.5 Tiến hành cài đặt file virus từ trên mạng
					VirusAnalyzer.downloadAndSaveFile(fileUrl, saveDir);
					// 2.6 Gọi hàm UpdateApp đưa đường dẫn của file nguồn đến file đích
					VirusAnalyzer.UpdateApp(selectedFile, targetFile);
					view.jLabel_date.setText(getLatestVersion());
					// 2.7 sau khi hoàn tất hiển thị thông báo cập nhật thành công
					JOptionPane.showMessageDialog(null, "Update successful!");
					System.out.println("Download successful!");
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Update failed, Check your network");
					System.out.println("Lỗi: " + e1.getMessage());
				}
			}
		}
        }
    }

    @SuppressWarnings("resource")
    public void replaceFile(File sourceFile, File destFile) throws IOException {
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
            view.jLabel_date.setText(getLatestVersion());
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

    private String getVersion() {
        // Lấy phiên bản hiện tại của phần mềm
        String getVersion = view.jLabel_date.getText();
        return getVersion;
    }

    private String getLatestVersion() {
        // TODO Auto-generated method stub
        return "14/5/2023-v0.2";
    }
    
    public void scanfolder() {
    	runProgress(false);
    }

    public void scanSystem() {
        runProgress(true);
    }

    public void updateDataTable(ArrayList<Object[]> listVirus) {
        if (listVirus != null) {
            view.table_model.setRowCount(0);// 20130340 update table
            view.checkbox.setSelected(false); // 20130340
            for (Object[] object : listVirus) {
                view.table_model.addRow(object);
                view.table_view.revalidate();
                view.repaint();
                view.sp.revalidate();
                view.sp.repaint();
            }
        }
    }

    //20130340
    public File fileChooser() {
        String projectDirectory = System.getProperty("user.dir"); // lấy đường dẫn tuyệt đối của thư mục gốc của dự án
        File path = new File(projectDirectory, "Virus"); // sử dụng đường dẫn tương đối để đến thư mục "Virus"
        JFileChooser chooser = new JFileChooser(path);
        chooser.setDialogTitle("Select the file to detect");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }
    // 20130340
    public ArrayList<String> listChoose() {
        ArrayList<String> rs = new ArrayList<>();
        boolean checked = false;
        for (int i = 0; i < view.table_view.getRowCount(); i++) {
                checked = (boolean) view.table_view.getValueAt(i, 3);
            if (checked) {
                String col = view.table_view.getValueAt(i, 0).toString();
                rs.add(col);
            }
        }
        return rs;
    }

    public File folderChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select the file to detect");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    public void runProgress(boolean scanSystem) {
        view.jProgress();
        final SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            private File file;

            @Override
            protected Void doInBackground() throws Exception {
                boolean isUpdating = false;
                File[] roots = File.listRoots();
                int progress = 0;
                while (progress < 100 && !isCancelled()) {
                    progress++;
                    if (!isUpdating) {
                        if (!scanSystem) {
                            file = folderChooser();
                            updateDataTable(VirusAnalyzer.scanFolder(file));
                        } else {
                            for (File fileItem : roots) {
                                updateDataTable(VirusAnalyzer.scanFolder(fileItem));
                            }
                        }
                    }
                    isUpdating = true;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                    }
                    publish(progress);
                }
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                for (int progress : chunks) {
                    view.progressBar.setValue(progress);
                    view.progressLabel.setText("Progress: " + String.valueOf(progress) + "%");
                }
            }

            @Override
            protected void done() {
            }
        };
        view.cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                worker.cancel(true);
                view.progressDialog.dispose();
            }
        });
        worker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("state".equals(evt.getPropertyName()) && SwingWorker.StateValue.DONE == evt.getNewValue()) {
                    view.progressBar.setValue(100);
                    view.progressDialog.setTitle("Done");
                    view.progressLabel.setText("Complete!");
                }
            }
        });
        worker.execute();
        view.progressDialog.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // 20130340
        if (view.table_view == e.getSource()) {
            if (e.getClickCount() == 2) {
                int row = view.table_view.getSelectedRow();
                String path = (String) view.table_view.getValueAt(row, 0);
                File selectedFile = new File(path);
                String[] columnNames = {"Property", "Value"};
                Object[][] data = {{"Name", selectedFile.getName()}, {"Path", selectedFile.getAbsolutePath()},
                        {"Size", selectedFile.length() + " bytes"},
                        {"Last Modified", new Date(selectedFile.lastModified()).toString()}};
                JTable table = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(500, 300));
                JOptionPane.showMessageDialog(null, scrollPane, "File Details", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
