/******************************************      imp.cpp   ***********************************************/
Image Processing  - preprocessing to remove background, illumination differences

Preprocessing : To give binary image of bill to tesseract as input
Our main challenge is to find the four corners of the bill and do the
perspective transformation.
Steps involved:
1. First converted the given colored image to a grayscale image.
2. Partial amount background in the image is removed adaptive
thresholding is done on the image
3. Morphological close operation is done to closed regions (one
of the region is the bill part )
4. Largest contour is found using the cvFIndContour()
5. Hough transform is then run on the outline of the bill for line
detection. The outline in all cases in a quadrilateral.
6. Best approximation for the four lines of the quadrilateral are
found.
7. Four corners are found using the intersection of four lines.
8. Now perspective transformation is done using the four points
9. Illumination is removed using Difference of Gaussians followed
by normalization and inversion of the image.


/******************************************     main.cpp    **********************************************/
Extract text from tesseract  and identify key text 

CHALLENGES

1) To find the text lines and text sections in the image.
	- Tesseract won't output the text in the image in order i.e. text which are inline in the image won't be inline in the tesseract output. Therefore
	  in order to associate items with prices they must be send each line at a time to tesseract.
2) Have to remove salt and pepper noise on the image.
	- Presence of noise and small blobs will impede the detection of text lines in the image.
3) Remove vertical lines in the binary image.
	- Necessary step to recognize text lines in the image.
4) Extract prices, items purchased, tool from those text lines.
	- There will be lot of errors in the text output given by tesseract because of varying intensity of characters in the image. Lot of error tolerance work
	  has to be in order to detect the total or prices or names of items.

/******************************************     Android    ************************************************/
Android Application - UI, Database storage, Integrating Native code into application

CHALLENGES
1) using OpenCV C++ code in Android
2) Sending C++ Structs to JAVA Objects and calling Java methods from C++

Spent significant amount of time understanding android API and makeing sample applications

