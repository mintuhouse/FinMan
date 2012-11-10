#include "cv.h"
#include "highgui.h"
#include "cvaux.h"
#include "cxmisc.h"
#include <iostream>
#include <string.h>
#include <vector>
#include <cfloat>
#include <cmath>
#include <algorithm>
using namespace cv;
using namespace std;

bool cmp(pair<CvPoint, pair<double, double> > p1, pair<CvPoint, pair<double, double> >  p2){
	return p1.second.second < p2.second.second;
}

bool cmp2(CvPoint p1, CvPoint p2){
	if( p1.x == p2.x) return p1.y <p2.y;
	else return p1.x<p2.x;
}

void rotate(CvPoint* corners){
	CvPoint p[4];
	for (int i = 0; i < 3; ++i)
	{
		p[i].x = corners[i+1].x;
		p[i].y = corners[i+1].y;
	}
	p[3].x = corners[0].x;
	p[3].y = corners[0].y;
	for (int i = 0; i < 4; ++i)
	{
		corners[i].x = p[i].x;
		corners[i].y = p[i].y;
	}
}

int main(int argc, char *argv[])
{
	CvSeq* contours = 0;
	CvSeq* dps = 0;
	int i, idx;
    CvPoint p;
    CvMemStorage* storage_ct = cvCreateMemStorage(0);
    CvMemStorage* storage = cvCreateMemStorage(0);
    CvMemStorage* storage_dp = cvCreateMemStorage(0);
    IplImage *img0 = cvLoadImage(argv[1], 0);
 	// cvShowImage( "image0", img0 );
	// cvWaitKey(0);
    IplImage *img1 = cvCreateImage(cvSize(583, 777), img0->depth, img0->nChannels);
	cvResize(img0, img1);
	// cvShowImage( "image", img1 );
	// cvWaitKey(0);
	IplImage *img2 = cvCreateImage(cvGetSize(img1),IPL_DEPTH_8U,1);
	IplImage *img3 = cvCreateImage(cvGetSize(img0),img0->depth,img0->nChannels);
	int h = img2->height;
 	int w = img2->width;	
	//cvCvtColor(img1,img2,CV_RGB2GRAY);
	//cvThreshold(img2,img2,100,255,CV_THRESH_BINARY);
	cvAdaptiveThreshold(img1,img2,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,501,0);
	//cvNamedWindow( "image" ,CV_WINDOW_NORMAL);
 	//cvShowImage( "image", img2 );
    int arr[9] =  { 0, 1, 0, 1, 1, 1, 0, 1, 0 };
    cvErode(img2,img2,cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_CUSTOM,arr),10);   
    cvDilate(img2,img2,cvCreateStructuringElementEx(3,3,1,1,CV_SHAPE_CUSTOM,arr),10);
	//cvNamedWindow( "dilate" ,CV_WINDOW_NORMAL);
    //cvShowImage( "dilate", img2 );
    cvFindContours( img2, storage_ct, &contours, sizeof(CvContour), CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE );
    // cvZero( img2 );
    // if( contours ){
    //     cvDrawContours(
    //         img2,
    //         contours,
    //         cvScalarAll(255),
    //         cvScalarAll(255),
    //         100 );
    // }
    // cvShowImage( "all contours", img2 );
    CvSeq* current_contour = contours;
	double largestArea = 0;
	CvSeq* largest_contour = NULL;
	while (current_contour != NULL){
	    double area = fabs(cvContourArea(current_contour,CV_WHOLE_SEQ, 0));       
	    if(area > largestArea){
	        largestArea = area;
	        largest_contour = current_contour;
	    }
	    current_contour = current_contour->h_next;
	}
	contours = largest_contour;
	contours->h_next = NULL;
    cvZero( img2 );
    if( contours ){
        cvDrawContours(
            img2,
            contours,
            cvScalarAll(255),
            cvScalarAll(255),
            100 );
    }
    //contours = cvApproxPoly( contours, sizeof(CvContour), storage_ct, CV_POLY_APPROX_DP, 0.01, 1 );
    // loop over all contours
    //cvCanny(img2,img2,10,100,3);
    //cvShowImage( "contours", img2 );
    CvSeq* lines = 0;
    lines = cvHoughLines2( 		
    						img2,
                           storage,
                           CV_HOUGH_STANDARD,
                           1,
                           CV_PI/180,
                           80,
                           0,
                           0 );
    vector<pair<CvPoint, pair<double, double> > > hlines;

    for( i = 0; i < MIN(lines->total,100); i++ )
        {
            float* line = (float*)cvGetSeqElem(lines,i);
            float rho = line[0];
            if (abs(rho) <= 10) continue;
            float theta = line[1];
            CvPoint pt1, pt2, pt;
            double a = cos(theta), b = sin(theta);
            double x0 = a*rho, y0 = b*rho;
            pt.x = x0;
            pt.y = y0;
            pt1.x = cvRound(x0 + 1000*(-b));
            pt1.y = cvRound(y0 + 1000*(a));
            pt2.x = cvRound(x0 - 1000*(-b));
            pt2.y = cvRound(y0 - 1000*(a));
            hlines.push_back(make_pair<CvPoint,pair<double,double> >(pt, make_pair<double, double>(theta*(180.0/CV_PI),rho)));  
            // if(b!=0) hlines.push_back(make_pair<CvPoint,double>(pt, -1*(a/b)));
            // else hlines.push_back(make_pair<CvPoint,double>(pt, DBL_MAX));
            //cvLine( img1, pt1, pt2, cvScalarAll(255), 1, CV_AA );
        }
    //cvShowImage( "all lines", img1 );
    //cvWaitKey(0);
    sort(hlines.begin(),hlines.end(),cmp); 
    //cout<<hlines.size()<<endl;
    for (int i = 0; i < hlines.size(); ++i)
   	{
		//cout<<hlines[i].first.x<<" "<<hlines[i].first.y<<" ";
		//cout<<hlines[i].second.first<<" "<<hlines[i].second.second<<endl;   	
	}
	int lineno[4];
	int mask[hlines.size()];
	float avgRho[4], avgThetha[4];
	for (int i = 0; i < hlines.size(); ++i)
	{
		mask[i] = 0;
	}

	for (int i = 0; i < 4; ++i)
	{
		int flag = 0;
		float rho,theta;
		int count = 0;
		avgThetha[i] = 0;
		avgRho[i] = 0;
		for (int j = 0; j < hlines.size(); ++j)
		{
			if(!mask[j]){
				if(!flag){
					mask[j] = 1;
					flag = 1;
					rho = hlines[j].second.second;
					theta = hlines[j].second.first;
					lineno[i] = j;
					count+=1;
					if(theta > 160){
						avgThetha[i]+= (theta - 180);
						if(rho < 0) avgRho[i] += -1*rho;
						else avgRho[i] +=rho;
					} 
					else{
						avgThetha[i]+=hlines[j].second.first;
						avgRho[i]+=hlines[j].second.second;
					}
				}
				else{
					if((abs(hlines[j].second.first - theta) <= 20 || abs(hlines[j].second.first - theta) >= 170) && abs(abs(hlines[j].second.second) - abs(rho)) <= 80.0 ){
						mask[j] = 1;
						//cout<<i<<" "<<j<<endl;
						if(theta > 160){
							avgThetha[i]+= (theta - 180);
							if(rho < 0) avgRho[i] += -1*rho;
							else avgRho[i] +=rho;
						} 
						else{
							avgThetha[i]+=hlines[j].second.first;
							avgRho[i]+=hlines[j].second.second;
						}
						count++;
					}
				}
			}		
		}
		avgRho[i]/=count;
		avgThetha[i]/=count;
	}
	for (int i = 0; i < 4; ++i)
	{
		avgThetha[i] =  avgThetha[i] < 0 ? avgThetha[i] + 180 : avgThetha[i];
		float theta = avgThetha[i];
		float rho = avgRho[i];
		for (int j = 0; j < hlines.size(); ++j)
		{
			if(abs(hlines[j].second.first - theta) <= 20  &&  abs(abs(hlines[j].second.second) - abs(rho)) <= (80.0*w*h)/(773*583) ){
				if(hlines[j].second.second < 0) {
					avgRho[i]*=-1;
					break;
				}
			}
		}
	}
	//cout<<"4 fourlines:"<<endl;
	for (int i = 0; i < 4; ++i)
	{
		float theta = avgThetha[i];
		float rho = avgRho[i];
		theta *= (CV_PI/180);
		double a = cos(theta), b = sin(theta);
        double x0 = a*rho, y0 = b*rho;
        CvPoint pt1, pt2;
        pt1.x = cvRound(x0 + 1000*(-b));
        pt1.y = cvRound(y0 + 1000*(a));
        pt2.x = cvRound(x0 - 1000*(-b));
        pt2.y = cvRound(y0 - 1000*(a));
        cvLine( img1, pt1, pt2, cvScalarAll(255), 1, CV_AA );
		//cout<<avgThetha[i]<<" "<<avgRho[i]<<endl;
	}
	//cvShowImage( "lines", img1 );
	CvPoint pt[4];
	int k;
	float theta = avgThetha[0];
	float rho = avgRho[0];
	for (int i = 1; i < 4; ++i)
	{
		int j = lineno[i];
		if(abs(avgThetha[i] - theta) <= 20 || abs(avgThetha[i] - theta) >= 160) {
			k = i;
			break;	
		}	
	}
	//cout<<k<<endl;
	float p1 = avgRho[0];
	float omega1 = avgThetha[0];
	omega1 *= (CV_PI/180);
	int t = 0;
	for (int i = 1; i < 4; ++i)
	{

		float p2 = avgRho[i];
		float omega2 = avgThetha[i];
		omega2 *= (CV_PI/180);
		if(i!=k){
			float r = sqrt(p1*p1+p2*p2-2*p1*p2*cos(omega1-omega2))/sin(omega1-omega2);
			float theta = acos((p2* sin(omega1)-p1*sin(omega2))/(sqrt(p1*p1+p2*p2-2*p1*p2*cos(omega1-omega2))));
			//cout<<r<<" "<<(theta*180)/CV_PI<<endl;
			pt[t++].x = r*cos(theta);
			pt[t-1].y = r*sin(theta);
		}
	}
	p1 = avgRho[k];
	omega1 = avgThetha[k];
	omega1 *= (CV_PI/180);
	for (int i = 1; i < 4; ++i)
	{

		float p2 = avgRho[i];
		float omega2 = avgThetha[i];
		omega2 *= (CV_PI/180);
		if(i!=k){
			float r = sqrt(p1*p1+p2*p2-2*p1*p2*cos(omega1-omega2))/sin(omega1-omega2);
			float theta = acos((p2* sin(omega1)-p1*sin(omega2))/(sqrt(p1*p1+p2*p2-2*p1*p2*cos(omega1-omega2))));
			//cout<<r<<" "<<(theta*180)/CV_PI<<endl;
			pt[t++].x = r*cos(theta);
			pt[t-1].y = r*sin(theta);
		}
	}
	for (int i = 0; i < 4; ++i)
	{
		//cout<<pt[i].x<<" "<<pt[i].y<<endl;
		if(pt[i].x < 0) pt[i].x*=-1;
		if(pt[i].y < 0) pt[i].y*=-1;
		//cvDrawCircle( img1, pt[i] , 3, CV_RGB(0,255,120));
	}
	//cvShowImage( "hough", img1 );
	CvPoint imagecorners[4];
	imagecorners[0] = cvPoint(0,0);
    imagecorners[1] = cvPoint(w,0);
    imagecorners[2] = cvPoint(0,h);
    imagecorners[3] = cvPoint(w,h);

    CvPoint corners[4]; 
    for (int i = 0; i < 4; ++i)
    {
    	double maxdist = DBL_MAX;
    	for (int j = 0; j < 4; ++j)
    	{
    		int dx = imagecorners[i].x - pt[j].x;
    		int dy = imagecorners[i].y - pt[j].y;
    		double dist = sqrt(dx*dx + dy*dy);
    		if(dist < maxdist) {
    			maxdist = dist;
    			corners[i].x = pt[j].x;
    			corners[i].y = pt[j].y;
    		}	
    	}
    }
	CvMat* mmat = cvCreateMat(3,3,CV_32FC1);
    CvPoint2D32f c2[4],c1[4];
    // c2[0] = cvPoint2D32f(0,0);
    // c2[1] = cvPoint2D32f(w,0);
    // c2[2] = cvPoint2D32f(0,h);
    // c2[3] = cvPoint2D32f(w,h);
    c2[0] = cvPoint2D32f(0,0);
    c2[1] = cvPoint2D32f(img0->width,0);
    c2[2] = cvPoint2D32f(0,img0->height);
    c2[3] = cvPoint2D32f(img0->width,img0->height);
    float scalex = (img0->width*1.0)/w;
    float scaley = (img0->height*1.0)/h;
    for (int i = 0; i < 4; ++i)
    {
    	c1[i] = cvPoint2D32f(corners[i].x * scalex,corners[i].y * scaley);
    }
    
    mmat = cvGetPerspectiveTransform(c1, c2, mmat);
    cvWarpPerspective(img0, img3, mmat);
    //cvShowImage("perspective", img3 );
  	//cvWaitKey(0);

    //cout<<img3->width<<" "<<img3->height<<endl;	
	IplImage* img4=cvCreateImage(cvSize(18*img3->width/20,18*img3->height/20),img3->depth,img3->nChannels);
    cvSetImageROI( img3,  cvRect( img3->width/20,img3->height/20, 18*img3->width/20,18*img3->height/20));
    cvCopy( img3, img4); 
    cvResetImageROI( img3 );
    //cout<<img4->width<<" "<<img4->height<<endl;	
	//cvShowImage( "perspective", img4 );
 	IplImage *dog_1 = cvCreateImage(cvGetSize(img4), img4->depth, img4->nChannels);
	IplImage *dog_2 = cvCreateImage(cvGetSize(img4), img4->depth, img4->nChannels);
  	cvSmooth(img4, dog_2, CV_GAUSSIAN, 31, 31); // Gaussian blur
  	cvSmooth(img4, dog_1, CV_GAUSSIAN, 1, 1);
  	cvSub(dog_2, dog_1, img4, 0);

  	IplImage* image_32F = cvCreateImage(cvGetSize(img4),IPL_DEPTH_32F,1);
	//now convert the 8bit Image into 32bit floating points
	cvCvtScale(img4,image_32F);
	 
	// the following will give an image that is scaled to [0...1] by dividing through the highest value in the array
	cvNormalize(image_32F,image_32F,0,1,CV_MINMAX);
	//cvShowImage("image_32F",image_32F);
	//cvWaitKey(0);
	cvConvertScaleAbs(image_32F,img4,255);
	uchar* dt = (uchar*)img4->imageData;
  	int wt = img4->width;
  	int ht = img4->height;
  	int st = img4->widthStep;
  	int ch = img4->nChannels;
  	int st1 = img4->widthStep;
  	//cout << ch << " " << st << "  " << img4->widthStep << endl;
  	for(int i=0;i<wt;i++) {
  		for(int j=0;j<ht;j++) {
  			dt[j*st1+i*ch] = 255 - (int)dt[j*st+i*ch];

  		}
  	}
  	cvThreshold(img4,img4,235,255,CV_THRESH_BINARY);
  	//cvShowImage("final", img4);
  	//cvWaitKey(0);
 	char s[100] = "perspective";
 	cvSaveImage("temp.jpg",img4);
    return 0;
}
