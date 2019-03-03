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

import static com.beck.kml.source.KMLSource.unGzip;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.*;

import com.beck.kml.model.Placemark;
import com.beck.kml.source.KMLSource;
import com.beck.kml.source.KMLSourceFactory;
import com.beck.kml.source.YoubikeKML;

public class YoubikeKMLUI extends JPanel {
	private static final long serialVersionUID = 4773652794390020385L;

	private final String[] URLS = new String[] {"ntpc", "taichung", "chcg", "tycg", "hccg", "sipa", "taipei"};
	
	final private JTextArea txLog;
	final private JComboBox<String> cmbUrl;
	final private JComboBox<String> ckOutputEnglish;
	final private JRadioButton rdOpenData;
	final private JRadioButton rdUrl;
	final private JRadioButton rdFile;
	
	private File inputFile;
	private File outputFile;

	public YoubikeKMLUI() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {65, 0, 0};
		gridBagLayout.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		ButtonGroup buttonGroup = new ButtonGroup();
		rdFile = new JRadioButton("[Select input file]");
		buttonGroup.add(rdFile);
		GridBagConstraints gbc_rdFile = new GridBagConstraints();
		gbc_rdFile.gridwidth = 2;
		gbc_rdFile.anchor = GridBagConstraints.NORTHWEST;
		gbc_rdFile.gridx = 0;
		gbc_rdFile.gridy = 0;
		add(rdFile, gbc_rdFile);
		
		rdOpenData = new JRadioButton("Download data of Taipei city (opendata)");
		rdOpenData.setSelected(true);
		buttonGroup.add(rdOpenData);
		add(rdOpenData, new GridBagConstraints(0, 1,
                2, 1, 0, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
		
		rdUrl = new JRadioButton("Download from official site, location:");
		buttonGroup.add(rdUrl);
		GridBagConstraints gbc_rdUrl = new GridBagConstraints();
		gbc_rdUrl.anchor = GridBagConstraints.WEST;
		gbc_rdUrl.insets = new Insets(0, 0, 0, 3);
		gbc_rdUrl.gridx = 0;
		gbc_rdUrl.gridy = 2;
		add(rdUrl, gbc_rdUrl);
		
		cmbUrl = new JComboBox<String>();
		cmbUrl.setEditable(true);
		cmbUrl.setModel(new DefaultComboBoxModel<String>(URLS));
		GridBagConstraints gbc_cmbUrl = new GridBagConstraints();
		gbc_cmbUrl.fill = GridBagConstraints.HORIZONTAL;
		gbc_cmbUrl.gridx = 1;
		gbc_cmbUrl.gridy = 2;
		add(cmbUrl, gbc_cmbUrl);
		
		add(new JLabel("Data language:"), new GridBagConstraints(0, 3,
                1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 7), 0, 0));
		ckOutputEnglish = new JComboBox<String>();
		ckOutputEnglish.setModel(new DefaultComboBoxModel<String>(new String[]{"tw","en"}));
		GridBagConstraints gbc_chckbxNewCheckBox = new GridBagConstraints();
		gbc_chckbxNewCheckBox.anchor = GridBagConstraints.WEST;
		gbc_chckbxNewCheckBox.gridwidth = 1;
		gbc_chckbxNewCheckBox.gridx = 1;
		gbc_chckbxNewCheckBox.gridy = 3;
		add(ckOutputEnglish, gbc_chckbxNewCheckBox);
		
		JButton btnNewButton = new JButton("Convert to KML");
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		//gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(3, 10, 3, 10);
		gbc_btnNewButton.gridwidth = 2;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 4;
		add(btnNewButton, gbc_btnNewButton);
		
		txLog = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(txLog);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 5;
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
		openDialog(inputFile, "Select source data file").ifPresent(file -> {
			inputFile = file;
			rdFile.setText("Input file ["+inputFile.getAbsolutePath()+"]");
		});
	}
	
	private Optional<File> openDialog(final File f, final String dialogTitle) {
		final JFileChooser dialog = new JFileChooser();
		dialog.setDialogTitle(dialogTitle);
		if (f != null) {
			dialog.setSelectedFile(f);
		}
		if (dialog.showDialog(getParent(), null) == JFileChooser.APPROVE_OPTION) {
			return Optional.of(dialog.getSelectedFile());
		}
		return Optional.empty();
	}
	
	private void convertKML() {
		final StringWriter sw = new StringWriter(256);
		final PrintWriter pw = new PrintWriter(sw);
		try {
			final YoubikeKML youbikeKML = new YoubikeKML();
			Map<String, String> options = new HashMap<>();
			byte[] data = null;
			if (rdFile.isSelected()) {
				if (inputFile == null || !inputFile.exists()) {
					pw.println("Input file does not exist");
				} else {
					pw.printf("Try to read data from [%s]\n", inputFile.getAbsolutePath());
					data = Files.readAllBytes(inputFile.toPath());
				}
			} else if (rdOpenData.isSelected()){
				data = download("http://data.taipei/youbike", null, null);
				options.put("en", "true");
			} else if (rdUrl.isSelected()){
				final String loc = String.valueOf(cmbUrl.getSelectedItem());
				final String lang = String.valueOf(ckOutputEnglish.getSelectedItem());
				pw.printf("Try to download data location=%s, lang=%s\n", loc, lang);
				data = download("https://apis.youbike.com.tw/useAPI", "https://"+loc+".youbike.com.tw/station/list", "action=ub_site_by_sno_class&datas[lang]="+lang+"&datas[loc]="+loc);
			}
			if (data != null) {
				final String json = new String(unGzip(data), "UTF-8");
				if (outputFile == null) {
					outputFile = new File("youbike.kml");
				}
				Optional<File> fout = openDialog(outputFile, "Select output kml file");
				if (!fout.isPresent()) {
					pw.println("Action canceled - output file must be selected");
				} else {
					outputFile = fout.get();
					final Collection<Placemark> list = youbikeKML.parse(json, options);
					pw.println("parse complete - stations found");;
					new KMLWriter(youbikeKML.getKMLName(), true).out(list).write(outputFile);
					pw.printf("Complete - output file [%s]\n", outputFile.getAbsolutePath());
				}
			}
		} catch (IllegalArgumentException ie) {
			pw.println(ie.getMessage());
		} catch (Throwable e) {
			e.printStackTrace(pw);
		}
		txLog.setText(sw.toString());
	}
	
	public static byte[] download(final String url, final String refer, final String postData) throws Exception {
		URLConnection urlc = new URL(url).openConnection();
		if (urlc instanceof HttpURLConnection) {
			final HttpURLConnection hconn = (HttpURLConnection)urlc;
			hconn.setRequestProperty("Referer", refer);
			hconn.setInstanceFollowRedirects(true);
			if (postData != null) {
				hconn.setDoOutput(true);
				try (final OutputStream out = hconn.getOutputStream()){
					out.write(postData.getBytes());
				}
			}
			final int stat = hconn.getResponseCode();
			if (stat == HttpURLConnection.HTTP_MOVED_PERM || stat == HttpURLConnection.HTTP_MOVED_TEMP) {
				final String location = hconn.getHeaderField("Location");
				if (location.indexOf("//") == -1) {
					urlc = new URL(new URL(url), location).openConnection();
				} else {
					urlc = new URL(location).openConnection();
				}
			}
		}
		try (final InputStream in = urlc.getInputStream()) {
			return KMLSource.readAllBytes(in);
		} catch (Exception e) {
			byte[] result = null;
			if (urlc instanceof HttpURLConnection) {
				try (final InputStream err = ((HttpURLConnection)urlc).getErrorStream()) {
					if (err != null) {
						result = KMLSource.readAllBytes(err);
					}
				}
			}
			if (result == null) {
				throw e;
			} else {
				return result;
			}
		}
	}
	
	public static void main(final String[] args) throws Exception {
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
		KMLSource kmlSource = null;
		String inputFile = "youbike";
		String outputFile = "youbike.kml";
		Map<String, String> options = new HashMap<>();
		boolean checkSamePoint = true;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i].trim();
			if (arg.startsWith("-h")) {
				System.out.println("YoutubeKML - Convert Youbike open data to KML file");
				System.out.println("  Command line options:");
				System.out.println("-i [file] input file, or folder with input files");
				System.out.println("-o [file] output file(kml)");
				System.out.println("-t [type] source type or qualified java class");
				System.out.println("-h        print help");
				System.out.println("-nocheck  Do NOT add if duplicate point exist (default:true)");
				System.out.println("-en       output English description in KML(only work for youbike opendata)");
				System.out.println("  GUI will start up if no command options.");
				return;
			} else {
				String fn = (i+1)==args.length?null:args[i+1];
				if ("-i".equals(arg)) {
					inputFile = fn;
					i++;
				} else if ("-o".equals(arg)) {
					outputFile = fn;
					i++;
				} else if ("-t".equals(arg)) {
					final Optional<KMLSource> opt = KMLSourceFactory.newKMLSource(fn);
					if (opt.isPresent()) {
						kmlSource = opt.get();
					} else {
						System.out.printf("source type [%s] does not exist", fn);
						return;
					}
					i++;
				} else if ("-en".equals(arg)) {
					options.put("en", "true");
				} else if ("-nocheck".equals(arg)) {
					checkSamePoint = false;
				}
			}
		}
		final Iterable<Placemark> list;
		final File f = new File(inputFile);
		if (f.isDirectory()) {
			final ArrayList<Placemark> alist = new ArrayList<>();
			final KMLSource ks = kmlSource;
			Stream.of(f.listFiles()).sorted(Comparator.comparing(File::lastModified)).forEach(file -> {
				try {
					alist.addAll(ks.parse(ks.read(file)));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			});
			list = alist;
		} else {
			list = kmlSource.parse(kmlSource.read(f));
		}
		new KMLWriter(kmlSource.getKMLName(), checkSamePoint).out(list).write(new File(outputFile));
	}
}
