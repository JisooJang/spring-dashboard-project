package com.littleone.littleone_web.domain;

public class JsonDashboard {
	private char attr;
	private double x;
	public JsonDashboard(char attr, double x) {
		super();
		this.attr = attr;
		this.x = x;
	}
	public JsonDashboard() {
		super();
	}
	public char getAttr() {
		return attr;
	}
	public void setAttr(char attr) {
		this.attr = attr;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
}
