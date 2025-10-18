import eu.mihosoft.vrl.v3d.parametrics.*;
import java.util.stream.Collectors;
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins;
import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Cube;
import eu.mihosoft.vrl.v3d.Cylinder
CSG generate(){
	String type= "ElecFreakPin"
	if(args==null)
		args=["RoundRound"]
	// The variable that stores the current size of this vitamin
	StringParameter size = new StringParameter(	type+" Default",args.get(0),Vitamins.listVitaminSizes(type))
	HashMap<String,Object> pinMeasure = Vitamins.getConfiguration( type,size.getStrValue())
	HashMap<String,Object> measurments = Vitamins.getConfiguration( "ElecFreakShaft","40mm")

	def diameterValue = measurments.diameter
	def insetValue = measurments.inset
	def widthOfFinValue = measurments.widthOfFin
	def lengthOfShaftValue=8

	String BottomTypeValue = pinMeasure.BottomType.toString()
	String TopTypeValue = pinMeasure.TopType.toString()
	def massCentroidXValue = pinMeasure.massCentroidX
	def massCentroidYValue = pinMeasure.massCentroidY
	def massCentroidZValue = pinMeasure.massCentroidZ
	def massKgValue = pinMeasure.massKg
	def priceValue = pinMeasure.price
	def sourceValue = pinMeasure.source

	CSG shaft = new Cylinder(diameterValue/2,lengthOfShaftValue).toCSG()
	CSG cutter = new Cube(diameterValue, widthOfFinValue-0.2, lengthOfShaftValue).toCSG().toZMin()
	CSG xy = shaft.intersect(cutter);
	CSG yx = shaft.intersect(cutter.rotz(90));
	CSG shaftPart = xy.union(yx)

	double flangeRad = 6.4/2.0
	double flangeHeight = 0.8
	double pinHeight = 8;
	double pinRad = 5.0/2.0
	CSG flange = new Cylinder(flangeRad, flangeHeight).toCSG()
	CSG round = new Cylinder(pinRad, pinHeight).toCSG()
	CSG lower;
	CSG upper;
	if(BottomTypeValue.contentEquals("round")) {
		lower = round.union(flange).union(flange.movez(pinHeight-flangeHeight))
	}else {
		lower=shaftPart.union(flange.movez(pinHeight-flangeHeight))
	}
	if(TopTypeValue.contentEquals("round")) {
		upper = round.union(flange).union(flange.movez(pinHeight-flangeHeight))
	}else {
		upper=shaftPart.union(flange.movez(pinHeight-flangeHeight))
	}


	// Stub of a CAD object
	CSG part = lower.union(upper.movez(pinHeight-flangeHeight))
	return part
			.setParameter(size)
			.setRegenerate({generate()})
}
return generate()