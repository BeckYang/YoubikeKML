/*
 New BSD License http://www.opensource.org/licenses/bsd-license.php
 Copyright (c) 2017, Beck Yang
 All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright notice, this 
   list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice, this 
   list of conditions and the following disclaimer in the documentation and/or 
   other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND 
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, 
INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, 
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED 
OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.beck.tool;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.*;

public class YoubikeKMLUI extends JPanel {
	private static final long serialVersionUID = 4773652794390020385L;

	private final String[] URLS = new String[] {"http://data.taipei/youbike",
			"http://ntpc.youbike.com.tw/cht/f12.php",
			"http://ntpc.youbike.com.tw/en/f12.php",
			"http://tycg.youbike.com.tw/cht/f12.php",
			"http://tycg.youbike.com.tw/en/f12.php",
			"http://hccg.youbike.com.tw/cht/f12.php",
			"http://hccg.youbike.com.tw/en/f12.php",
			"http://i.youbike.com.tw/cht/f12.php",
			"http://i.youbike.com.tw/en/f12.php",
			"http://chcg.youbike.com.tw/cht/f12.php",
			"http://chcg.youbike.com.tw/en/f12.php",
			"http://taipei.youbike.com.tw/cht/f12.php"};
	
	private JTextArea txLog;
	private JComboBox<String> cmbUrl;
	private JCheckBox ckOutputEnglish;
	private JRadioButton rdUrl;
	private JRadioButton rdFile;
	
	private File inputFile;
	private File outputFile;

	public YoubikeKMLUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {65, 0, 0};
		gridBagLayout.rowHeights = new int[]{23, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		rdFile = new JRadioButton("[Select input file]");
		buttonGroup.add(rdFile);
		GridBagConstraints gbc_rdFile = new GridBagConstraints();
		gbc_rdFile.gridwidth = 2;
		gbc_rdFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdFile.insets = new Insets(0, 0, 5, 0);
		gbc_rdFile.gridx = 0;
		gbc_rdFile.gridy = 0;
		add(rdFile, gbc_rdFile);
		
		rdUrl = new JRadioButton("Download from ");
		rdUrl.setSelected(true);
		buttonGroup.add(rdUrl);
		GridBagConstraints gbc_rdUrl = new GridBagConstraints();
		gbc_rdUrl.anchor = GridBagConstraints.WEST;
		gbc_rdUrl.insets = new Insets(0, 0, 5, 5);
		gbc_rdUrl.gridx = 0;
		gbc_rdUrl.gridy = 1;
		add(rdUrl, gbc_rdUrl);
		
		cmbUrl = new JComboBox<String>();
		cmbUrl.setEditable(true);
		cmbUrl.setModel(new DefaultComboBoxModel<String>(URLS));
		GridBagConstraints gbc_cmbUrl = new GridBagConstraints();
		gbc_cmbUrl.insets = new Insets(0, 0, 5, 0);
		gbc_cmbUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbUrl.gridx = 1;
		gbc_cmbUrl.gridy = 1;
		add(cmbUrl, gbc_cmbUrl);
		
		ckOutputEnglish = new JCheckBox("Output English");
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.insets = new Insets(0, 0, 5, 0);
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.gridwidth = 2;
		gbc_chckbxNewCheckBox.gridx = 0;
		gbc_chckbxNewCheckBox.gridy = 2;
		add(ckOutputEnglish, gbc_chckbxNewCheckBox);
		
		JButton btnNewButton = new JButton("Convert to KML");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 3;
		add(btnNewButton, gbc_btnNewButton);
		
		txLog = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(txLog);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		add(scrollPane, gbc_scrollPane);
			
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				convertKML();
			}
		});
		rdFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				selectInputFile();
			}
		});
	}
	
	private void selectInputFile() {
		File nFile = openDialog(inputFile, "Select source data file");
		if (nFile != null) {
			inputFile = nFile;
			rdFile.setText("Input file ["+inputFile.getAbsolutePath()+"]");
		}
	}
	
	private File openDialog(File f, String dialogTitle) {
		JFileChooser dialog = new JFileChooser();
		dialog.setDialogTitle(dialogTitle);
		if (f != null) {
			dialog.setSelectedFile(f);
		}
		if (dialog.showDialog(getParent(), null) == JFileChooser.APPROVE_OPTION) {
			return dialog.getSelectedFile();
		}
		return null;
	}
	
	private String download(String  url) throws Exception {
		URLConnection urlc = new URL(url).openConnection();
		if (urlc instanceof HttpURLConnection) {
			HttpURLConnection hconn = (HttpURLConnection)urlc;
			hconn.setInstanceFollowRedirects(true);
			if (hconn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
				String location = hconn.getHeaderField("Location");
				if (location.indexOf("//") == -1) {
					urlc = new URL(new URL(url), location).openConnection();
				} else {
					urlc = new URL(location).openConnection();
				}
			}
		}
		return YoubikeKML.extractJson(urlc.getInputStream());
	}
	
	private void convertKML() {
		StringWriter sw = new StringWriter(256);
		PrintWriter pw = new PrintWriter(sw);
		try {
			YoubikeKML youbikeKML = new YoubikeKML();
			String json = null;
			if (rdFile.isSelected()) {
				if (inputFile == null || !inputFile.exists()) {
					pw.append("Input file does not exist");
				} else {
					pw.append("Try to read data from [").append(inputFile.getAbsolutePath()).println("]");
					json = youbikeKML.read(inputFile);
				}
			} else if (rdUrl.isSelected()){
				String url = String.valueOf(cmbUrl.getSelectedItem());
				pw.append("Try to download data from [").append(url).println("]");
				json = download(url);
			}
			if (json != null) {
				if (outputFile == null) {
					outputFile = new File("youbike.kml");
				}
				File fout = openDialog(outputFile, "Select output kml file");
				if (fout == null) {
					pw.println("Action canceled - output file must be selected");
				} else {
					outputFile = fout;
					if (ckOutputEnglish.isSelected()) {
						youbikeKML.outputEnglish();
					}
					youbikeKML.parse(json);
					pw.append("parse complete - ").println(" stations found");;
					youbikeKML.write(outputFile);
					pw.append("Complete - output file [").append(outputFile.getAbsolutePath()).println("]");;
				}
			}
		} catch (IllegalArgumentException ie) {
			pw.println(ie.getMessage());
		} catch (Throwable e) {
			e.printStackTrace(pw);
		}
		txLog.setText(sw.toString());
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			//launch GUI
			JFrame frame = new JFrame();
			frame.setTitle("Convert Youbike open data to KML file");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(new YoubikeKMLUI());
			frame.setSize(420, 400);
			frame.doLayout();
			frame.setVisible(true);
			return;
		}
		YoubikeKML youbikeKML = new YoubikeKML();
		String inputFile = "youbike";
		String outputFile = "youbike.kml";
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].trim();
			if ("-en".equals(arg)) {
				youbikeKML.outputEnglish();
			} else if (arg.startsWith("-h")) {
				System.out.println("YoutubeKML - Convert Youbike open data to KML file");
				System.out.println("  Command line options:");
				System.out.println("-i [file] input file");
				System.out.println("-o [file] output file(kml)");
				System.out.println("-h        print help");
				System.out.println("-en       output English description in KML(May not work if input file is download from web page)");
				System.out.println("  GUI will start up if no command options.");
			} else {
				String fn = (i+1)==args.length?null:args[i+1];
				if ("-i".equals(arg)) {
					inputFile = fn;
				} else if ("-o".equals(arg)) {
					outputFile = fn;
				}
			}
		}
		String jsonStr = youbikeKML.read(new File(inputFile));
		youbikeKML.parse(jsonStr);
		youbikeKML.write(new File(outputFile));
			
	}
}
