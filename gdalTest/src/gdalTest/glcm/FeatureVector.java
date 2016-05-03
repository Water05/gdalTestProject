package gdalTest.glcm;

public class FeatureVector {
 private double entropy;
 private double energy;
 private double contrast;
 private double homogenity;
 private double relativity;
public double getRelativity() {
	return relativity;
}
public void setRelativity(double relativity) {
	this.relativity = relativity;
}
public double getEntropy() {
	return entropy;
}
public void setEntropy(double entropy) {
	this.entropy = entropy;
}
public double getEnergy() {
	return energy;
}
public void setEnergy(double energy) {
	this.energy = energy;
}
public double getContrast() {
	return contrast;
}
public void setContrast(double contrast) {
	this.contrast = contrast;
}
public double getHomogenity() {
	return homogenity;
}
public void setHomogenity(double homogenity) {
	this.homogenity = homogenity;
}
}
