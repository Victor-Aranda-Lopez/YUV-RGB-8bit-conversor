/**
 * Function that converts an array of bytes in yuv420 format to an array of RGB integer
 * @param argb Output RGB array
 * @param yuv Input YUV420 array
 * @param width of the picture
 * @param height of the picture
 */
public static void YUV_420_TO_RGB(int[] argb, byte[] yuv, int width, int height) {
	final int frameSize = width * height;

	final int ii = 0;
	final int ij = 0;
	final int di = +1;
	final int dj = +1;

	int a = 0;
	for (int i = 0, ci = ii; i < height; ++i, ci += di) {
		for (int j = 0, cj = ij; j < width; ++j, cj += dj) {
			//Convert pixel by pixel
			int y = (0xff & ((int) yuv[ci * width + cj]));
			int u = (0xff & ((int) yuv[frameSize + (ci >> 1 ) * width/2 + (cj >> 1 ) + 0]));
			int v = (0xff & ((int) yuv[frameSize+ frameSize/4 + (ci >> 1 ) * width/2 + (cj >> 1) + 0]));
			y = y < 16 ? 16 : y;

			int r = (int) (1.164f * (y - 16) + 1.596f * (v - 128));
			int g = (int) (1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
			int b = (int) (1.164f * (y - 16) + 2.018f * (u - 128));

			r = r < 0 ? 0 : (r > 255 ? 255 : r);
			g = g < 0 ? 0 : (g > 255 ? 255 : g);
			b = b < 0 ? 0 : (b > 255 ? 255 : b);

			argb[a++] = 0xff000000 | (r << 16) | (g << 8) | b;
		}
	}
}
