package com.samuel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ArrayCreator {
	private static BufferedWriter arrayWriter;
	private static Scanner bondReader;
	private static ArrayList<Double> vR, vL, pos, vel, ang, angVel;
	String file;
	public ArrayCreator(String file) {
		this.file = file;
		try {
			String modFile = this.file.replaceAll(".BOND", "");
			File arrayFile = new File(UI.userHomeFolder, modFile + "Array.BOND");
			arrayWriter = new BufferedWriter(new FileWriter(arrayFile));
			bondReader = new Scanner(new FileReader(UI.userHomeFolder + this.file));
			vR = new ArrayList<>();
			vL = new ArrayList<>();
			pos = new ArrayList<>();
			vel = new ArrayList<>();
			ang = new ArrayList<>();
			angVel = new ArrayList<>();
			while(bondReader.hasNextLine()) {
				String[] nums = bondReader.nextLine().split(" ");
				vR.add(Double.parseDouble(nums[0]));
				vL.add(Double.parseDouble(nums[1]));
				pos.add(Double.parseDouble(nums[2]));
				vel.add(Double.parseDouble(nums[3]));
				ang.add(Double.parseDouble(nums[4]));
				angVel.add(Double.parseDouble(nums[5]));
			}
			arrayWriter.write("ProfileDrive.voltagesRight = new ArrayList(\n\tArrays.asList(");
			for(int a = 0; a < vR.size(); a++) {
				arrayWriter.write(vR.get(a) + (a == (vR.size() - 1) ? "" : ", "));
			}
			arrayWriter.newLine();
			arrayWriter.write("\t)\n);");
			arrayWriter.newLine();
			arrayWriter.write("ProfileDrive.voltagesLeft = new ArrayList(\n\tArrays.asList(");
			for(int a = 0; a < vL.size(); a++) {
				arrayWriter.write(vL.get(a) + (a == (vL.size() - 1) ? "" : ", "));
			}
			arrayWriter.newLine();
			arrayWriter.write("\t)\n);");
			arrayWriter.newLine();
			arrayWriter.write("ProfileDrive.poses = new ArrayList(\n\tArrays.asList(");
			for(int a = 0; a < pos.size(); a++) {
				arrayWriter.write(pos.get(a) + (a == (pos.size() - 1) ? "" : ", "));
			}
			arrayWriter.newLine();
			arrayWriter.write("\t)\n);");
			arrayWriter.newLine();
			arrayWriter.write("ProfileDrive.vels = new ArrayList(\n\tArrays.asList(");
			for(int a = 0; a < vel.size(); a++) {
				arrayWriter.write(vel.get(a) + (a == (vel.size() - 1) ? "" : ", "));
			}
			arrayWriter.newLine();
			arrayWriter.write("\t)\n);");
			arrayWriter.newLine();
			arrayWriter.write("ProfileDrive.angs = new ArrayList(\n\tArrays.asList(");
			for(int a = 0; a < ang.size(); a++) {
				arrayWriter.write(ang.get(a) + (a == (ang.size() - 1) ? "" : ", "));
			}
			arrayWriter.newLine();
			arrayWriter.write("\t)\n);");
			arrayWriter.newLine();
			arrayWriter.write("ProfileDrive.angVels = new ArrayList(\n\tArrays.asList(");
			for(int a = 0; a < angVel.size(); a++) {
				arrayWriter.write(angVel.get(a) + (a == (angVel.size() - 1) ? "" : ", "));
			}
			arrayWriter.newLine();
			arrayWriter.write("\t)\n);");
			
			arrayWriter.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
