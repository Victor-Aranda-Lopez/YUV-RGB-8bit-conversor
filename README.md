# YUV-RGB-8bit-conversor
Function that converts YUV 4:2:0 format images into RGB with 8 bit depth in Java.

This can be helpful in Android devices since Android do not support YUV 4:2:0, but it does support RGB (among others).

Important: This function only can convert 8 bit depth colours but not 10 or any other size.

Contents

	YUV_RGB_Converter.java file contains the function that makes the conversion
	
	YUV_RGB_Converter_example.java contains an example of how to use the function: 
		It reads from a file the YUV data and returns in varable "rgbData" the converted values for each frame (in each iteration of while loop)