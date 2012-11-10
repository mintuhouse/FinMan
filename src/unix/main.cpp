#include <iostream>
#include <cstring>
#include <string>
#include <cstdlib>
#include <cv.h>
//#include <ctype>
#include "cvaux.h"
#include "cxmisc.h"
#include <highgui.h>
#include <vector>
#include <algorithm>
#include <baseapi.h>
#include <allheaders.h>
using namespace cv;
using namespace std;
#define pb push_back

struct billDetail {
	char	name[20];
	int 	nItems;		//Assuming No. of Items in Bill less than 100
	char 	itemName[100][40];
	char 	itemPrice[100][12];
	char  itemQuant[100][10];
	char 	total[12];
} billDetails;

class Text {
	public:
		int firstY;
		int lastY;
		int section;
		bool line;
		Text(int fY, int lY) {
			firstY = fY;
			lastY = lY;
			section = 0; line = 0;
		}
		Text(){} 
		void setSection(int sec) {
			section = sec;
		}
		void setLine(bool l) {
			line = l;
		}
		void print(){
			cout << firstY << ","<<lastY<<","<<section<< ","<<line<<endl;
		}
};

class Section {
	public:
		int firstY;
		int lastY;
		vector<Text> textY;
		Section(int fY, int lY) {
			firstY = fY;
			lastY = lY;
		}
		Section(){} 
		void setValues(int fY, int lY) {
			firstY = fY;
			lastY = lY;
		}
		void push(Text t) {
			textY.pb(t);
		}
		void print(){
			cout << firstY << ","<<lastY<<","<<textY.size()<<endl;
		}
};
void removeVertical(IplImage* img){
	int height,width,channels,depth,step;
	height    = img->height;
  	width     = img->width;
  	step      = img->widthStep;
  	channels  = img->nChannels;
  	depth     = img->depth;
  	uchar* data 	 = (uchar *)img->imageData;
	for(int i = 0;i < width;i++){
		int st = 0;
		int cnt = 0;
		for(int j = 0;j < height;j++){
			//cout << (int)data[j*step+i*channels] << endl;
			if((int)data[j*step+i*channels] < 30){
				if(cnt == 0){
					st = j;
				}
				cnt++;
			}
			else{
				if(cnt != 0){
					//cout << i<<" CNT " << cnt << " " << height << endl;
					if(10*cnt > height){
						for(int k = st;k <= j;k++){
							data[k*step+i*channels] = 255-data[k*step+i*channels];
						}
					}
					cnt = 0;
				}
			}
		}
		if(cnt != 0){
			//cout << st << " Reached " <<cnt << endl;
			for(int k = st;k < height;k++){
				//cout << (int)data[k*step+i*channels] << "  ";
				data[k*step+i*channels] = 255-data[k*step+i*channels];
				//cout <<	(int)data[k*step+i*channels] << endl;
			}
		}
	}
	
}
void printSection(vector<Section> t) {
	for(int i=0; i<t.size();i++)
		t[i].print();
}
void printText(vector<Text> t) {
	for(int i=0; i<t.size();i++)
		t[i].print();
}
void print(vector<int> t) {
	for(int i=0; i<t.size();i++)
		cout << t[i] << "  ";
	cout << endl;
}
bool detectDigit(char t) {
	if(t == 'O' || t == ',' || t == 'D' || (int)t == -80 || (int)t == 248 || (t >= '0' && t <= '9')) return 1;
	else return 0;
}
bool detectItem1(string line) {
	int flag = 1;
	int flag1 = 0;
	for(int i = line.length()-1; i >= 0 && flag == 1; i--) {
		//the pattern is last two must be digits
		if(line[i] == ' ' || line[i] == '\n') {}
		else if (flag1 == 0 || flag1 == 1) {
			if(detectDigit(line[i])) flag1++;
			else {
				return 0;
			}
		}	
		else if(flag1 == 2) {
			if(line[i] == '.') flag1++;
			else {
				return 0;
			}
		}
		else if(flag1 == 3) {
			// sequence of digits followed by space or dollar
			for(int j = i; j >=0; j--) {
				if(detectDigit(line[j])) {}
				else if (line[j] == ' ' || line[j] == '$') return 1;
				else {
					return 0;
				}
			}
		}
	}
	return -1;
}
int detectItem(string line) {
	int flag = 1;
	int flag1 = 0;
	for(int i = line.length()-1; i >= 0 && flag == 1; i--) {
		//the pattern is last two must be digits
		if(line[i] == ' ' || line[i] == '\n') {}
		else if (flag1 == 0 || flag1 == 1) {
			if(detectDigit(line[i])) flag1++;
			else {
				return -1;
			}
		}	
		else if(flag1 == 2) {
			if(line[i] == '.' || line[i] == ',') flag1++;
			else {
				//cout << line[i] << endl;
				return -1;
			}
		}
		else if(flag1 == 3) {
			// sequence of digits followed by space or dollar
			for(int j = i; j >=0; j--) {
				if(detectDigit(line[j])) {}
				else if (line[j] == ' ') return j;
				else {
					return j-1;
				}
			}
		}
	}
	return -1;
}
bool isNum(string s){
	for(int i = 0;i < s.length();i++){
		if(detectDigit(s[i]) || s[i] == ' ' || s[i] == ',' || s[i] == '.' || s[i] == '\n')continue;
		return false;
	}
	return true;
}
string detectTotal(string s){
	for(int i = 0;i < s.length();i++)
	{
		s[i] = tolower(s[i]);
	}
	for(int i = 0;i < s.length()-4;i++)
	{
		if((i == 0 || s[i-1] == ' ') && s.substr(i,5) == "total" && isNum(s.substr(i+5))){
			return s.substr(i+5);
		}
	}
	return ""; 
}


pair<int,int> detectQItem(string line){
	int flag = 1;
	int flag1 = 0;
	int x = -2;
	for(int i = line.length()-1; x == -2 && i >= 0 && flag == 1; i--) {
		//the pattern is last two must be digits
		if(line[i] == ' ' || line[i] == '\n' || (flag1 == 0 && line[i] == ',')) {}
		else if (flag1 == 0 || flag1 == 1) {
			if(detectDigit(line[i])) flag1++;
			else {
				x = -5;
				break;
			}
		}	
		else if(flag1 == 2) {
			if(line[i] == '.' || line[i] == ',' || line[i] == '-' || line[i] == '\'' || line[i] == '_') flag1++;
			else {
				//cout << line[i] << " HERE!!!" << endl;
				x = -6;
				break;
			}
		}
		else if(flag1 == 3) {
			// sequence of digits followed by space or dollar
			for(int j = i; j >=0; j--) {
				if(detectDigit(line[j])) {}
				else if (line[j] == ' ') {
					x = j;
					break;
				}
				else {
					x = j-1;
					break;
				}
			}
		}
	}
	if(x < 0)return make_pair(x,-1);
	bool qch = 0;
	for(int i = x;i >= 0;i--){
		if(line[i] == ' '){
			if(!qch)continue;
			else return make_pair(x,i);
		}
		else if(detectDigit(line[i])){
			if(!qch){
				qch = 1;
			}
			continue;
		}
		else{
			return make_pair(x,-1);
		}
	}
	return make_pair(-1,-1);
} 


bool detectLine(uchar* data,Text l,int width,int height,int step,int channels){
	int avg = (l.firstY+l.lastY)/2;
	if(l.lastY - l.firstY < 20*height/2560) return 1;
	int cnt = 0;
	int tcnt = 0;
	int mx = 0;
	for(int i = 0;i < width;i++)
	{
		if(data[avg*step+i*channels] == 0){
			cnt++;
			tcnt++;
		}
		else{
			mx = max(mx,cnt);
			cnt = 0;
		}
	}
	if(100*mx > 65*width || 100*tcnt > 80*width){
		return 1;
	} 
	return 0;
}


string giveLine(Text t,int width,int depth,int channels,IplImage* img1){
	IplImage* img2=cvCreateImage(cvSize(width,(t.lastY - t.firstY)),depth,channels);
  	cvSetImageROI( img1,  cvRect( 0,t.firstY, width,(t.lastY - t.firstY)));
  	cvCopy( img1, img2); 
    	cvResetImageROI( img1 );
    	
  	tesseract::TessBaseAPI tess; 
	tess.Init(NULL, "eng" ); 
	tess.SetImage((uchar *)img2->imageData, img2->width, img2->height, img2->nChannels, img2->widthStep);
	tess.Recognize(0);
	string out(tess.GetUTF8Text());
  	return out; 
 
}



int main(int argc, char *argv[]) {
	// variable img stores the main image "inputImage.png"
	IplImage* img = 0; 
  	int height,width,channels,depth,step;
  	uchar* data;
	// load an image  
  	img=cvLoadImage(argv[1], CV_LOAD_IMAGE_GRAYSCALE);
  	if(!img){
    		cout << "Could not load image file: %s\n "<<argv[1]<<endl;
    		exit(0);
  	}  
  	// get the image name just in case may be useful later on
  	string imageID = argv[1];int last = imageID.find_last_of("/");string sub = imageID.substr(last+1);int first = sub.find_first_of(".");
  	imageID = sub.substr(0,first);
    	// get the image data
  	height    = img->height;
  	width     = img->width;
  	step      = img->widthStep;
  	channels  = img->nChannels;
  	depth     = img->depth;
  	data 	 = (uchar *)img->imageData;
  	IplImage* img1 = cvCreateImage(cvGetSize(img),depth,channels);
  	//cvThreshold(img, img1, 150,255, CV_THRESH_BINARY); // Assumption background in the bill is brighter than 200
  	//cvAdaptiveThreshold(img,img1,255,ADAPTIVE_THRESH_MEAN_C,THRESH_BINARY,501,0);
  	//cvCopy(img,img1);
  	cvSmooth(img,img1,CV_MEDIAN,5);
  	removeVertical(img1);
  	//img1 = remove_small_objects(img1,200);
  	data 	 = (uchar *)img1->imageData;
  	//cvNamedWindow("mainWin", CV_WINDOW_AUTOSIZE); 
  	//cvMoveWindow("mainWin", 100, 100);
  	//cvShowImage("mainWin", img1 );
  	//cvWaitKey(0);
  	//Parse through each row..Find interLine gaps and interSection gaps
  	int rowPixels;
  	vector<Text> textY;
  	vector<int> interLine;
  	int fY=0,lY=0,flag=0;
  	for(int i=0;i<height;i++) {
  		rowPixels = 0;
  		for(int j=0;j<width;j++) {
  			if(data[i*step+j*channels] == 0)// after doing grayscale all channels will have same value..so doesn't matter which channel 
  				rowPixels++;
  		}
  		//put a threshold for no.of black pixel occurence  
  		if (rowPixels > 0 && flag==0) {
  			// concluding beginnning of text..Better find arrays of 
  			if(lY != 0) {
  				//cout << i-lY<< "  " << i << endl;
 				//interLine.pb(i-lY); 			
  			}
  			flag = 1;
  			fY = i;
  		}
  		else if(rowPixels == 0 && flag==1) {
  			flag = 0;
  			lY = i;
  			//cout << fY << "," <<lY<<endl;
  			if(lY - fY > 5*height/2560) {
  				textY.pb(Text(fY,lY)); // Inserting the beginning and ending of each text line.
  				(*textY.rbegin()).setLine(detectLine(data,*textY.rbegin(),width,height,step,channels));
  			}
  			//cout << fY <<" " <<lY << " " << detectLine(data,*textY.rbegin(),width,step,channels) << endl; 
  		}	
  	}
  	//cout << textY.size() << "  " << interLine.size() << endl;
  	for(int i=1; i < textY.size(); i++)
  		interLine.pb(textY[i].firstY-textY[i-1].lastY); 
 	vector<int> interLineCopy = interLine; 	//copying the interLine to preserve original order
 	
  	sort(interLineCopy.begin(),interLineCopy.end());  	
  	//print(interLineCopy);
  	//printText(textY);
  	int sectionThresh;
  	int sections = 1;
  	
  	for(sectionThresh = interLineCopy.size()-1;sectionThresh > 0;sectionThresh--){
  		if(2*interLineCopy[sectionThresh] >= 3*interLineCopy[sectionThresh-1])break;
  		else sections++;
  	}
  	//cout << interLineCopy[sectionThresh] << endl;
  	//cout << sectionThresh << " " << interLineCopy.size() << endl;
  	if(sectionThresh < interLineCopy.size()-1)
  		sectionThresh = interLineCopy[sectionThresh];
  	else {
  		sectionThresh = interLineCopy.size() - 1; 
  		//cout << interLineCopy.size() << " " << interLineCopy[sectionThresh] << endl;
  		sectionThresh = interLineCopy[interLineCopy.size()-1] + 1;
  	}
  	//Adding sections to textY class
  	int currentSection =  1;
  	vector<Section> sectionY;
  	sectionY.pb(Section());
  	fY = 0; lY = 0;
  	textY[0].setSection(currentSection);
  	(*sectionY.rbegin()).push(textY[0]);
  	fY = textY[0].firstY;
  	for(int i=0;i<interLine.size();i++) {
  		if (interLine[i] < sectionThresh) {
  			if(!textY[i+1].line) {
  				textY[i+1].setSection(currentSection);
  				(*sectionY.rbegin()).push(textY[i+1]);
  			}
  			else {
  				//just increase section number
  				currentSection++;
  				lY = textY[i].lastY;
  				(*sectionY.rbegin()).setValues(fY,lY);
  				if(i+2 <= interLine.size()) {
  					sectionY.pb(Section());
  					fY = textY[i+2].firstY;
  				}
  				else
  					fY = textY[i+1].lastY;
  			}
  		}
  		else {
  			currentSection++;
  			lY = textY[i].lastY;
  			(*sectionY.rbegin()).setValues(fY,lY);
  			sectionY.pb(Section());
  			textY[i+1].setSection(currentSection);
  			(*sectionY.rbegin()).push(textY[i+1]);
  			fY = textY[i+1].firstY;
  		}  			
  	}
  	lY = textY[interLine.size()].lastY;
  	//cout << lY << "  " << fY << endl;
  	if(fY != lY)
  		(*sectionY.rbegin()).setValues(fY,lY);
  	
  	//printText(textY);
  	//printSection(sectionY);
  	// Taking an element from section
  	
  	map<int,vector<string> >Items;
  	map<int,vector<string> >Prices;
  	map<int,vector<string> >quants;
  	/*
  	for(int i = 0;i < sectionY.size();i++){
  		for(int j = 0;j < sectionY[i].textY.size();j++){ 
  			
  		}
  	}
  	*/
  	string Tot = "ARBIT",Totemp;
  	int checkTotal = 0;
  	char ab = '0';
  	ab = (char) (-80);
  	//cout << ab << " sss" << endl;
  	int itemSec = 0;
  	bool check = 0;
  	for(int i = 0;i < sectionY.size();i++){
  		for(int j = 0;j < sectionY[i].textY.size();j++){
  			if(sectionY[i].textY[j].line)break;
  			//cout << i << "  " << j << "  seec" << endl;
  			string s = giveLine(sectionY[i].textY[j],width,depth,channels,img1);
  			//cout << s <<  "  ss"<< endl;
  			if(!checkTotal && s != "\n" && s != " "){
  				//cout << 1 << endl;
  				Totemp = detectTotal(s);
  				if(Totemp != "")
  				{
  					checkTotal = 1;
  					Tot = Totemp;
  				}
  			}
  			//cout << "Total " << Tot << endl;
  			pair<int,int> spos = detectQItem(s);
  			//cout << s << " " << spos.first << " " << spos.second << " " << s[s.length()-3]<<" " <<sectionY[i].textY[j].section << endl;
  			bool ch = 1;
  			if(spos.first < 0){
  				continue;
  			}
  			else{
  				
  				if((j == 0 || j == 1) && !check){
  					check = 1;
  					//cout << i << "  seec" << endl;
  					itemSec = i;
  				}
  				Prices[i].pb(s.substr(spos.first));
  				if(spos.second == -1){
  					quants[i].pb("");
  					Items[i].pb(s.substr(0,spos.first));
  				}
  				else{
  					quants[i].pb(s.substr(spos.second,spos.first-spos.second+1));
  					Items[i].pb(s.substr(0,spos.second));
  				}
  			}
  		}
  	}
  	//cout << 1 << "  seec" << endl;
  	for(int i = 0;i < sectionY.size();i++){
  		if(Items[i].size() > 0) //cout << i << endl;
  		for(int j = 0;j < Items[i].size();j++)
  		{
  			//cout << "    "<<Items[i][j] << " : " << quants[i][j] << " : " << Prices[i][j] << endl;
  		}
  	}
  	
  	struct billDetail billInfo;
  	strcpy(billInfo.name, argv[1]);
  	billInfo.nItems = Items[itemSec].size();
  	//billInfo.total = Tot;
  	for(int j=0;j<Tot.length();j++)
  		if(Tot[j] != '\n')
  			billInfo.total[j] = Tot[j];
  		else break;
  	billInfo.total[Tot.length()] = '\0';
  	//for(int j=Tot.length();j<12;j++) billInfo.total[j] = ' ';
  	for(int i = 0;i < Items[itemSec].size();i++)
  	{
  		for(int j=0;j<Items[itemSec][i].length();j++)
  			if(Items[itemSec][i][j] != '\n')
  				billInfo.itemName[i][j] = Items[itemSec][i][j];
  			else break;
  		billInfo.itemName[i][Items[itemSec][i].length()] = '\0';
  		//for(int j=Items[itemSec][i].length();j<40;j++) billInfo.itemName[i][j] = ' ';
  		
  		for(int j=0;j<Prices[itemSec][i].length();j++)
  			if(Prices[itemSec][i][j] != '\n')
  				billInfo.itemPrice[i][j] = Prices[itemSec][i][j];
  			else break;
  		billInfo.itemPrice[i][Prices[itemSec][i].length()] = '\0';
  		//for(int j=Prices[itemSec][i].length();j<12;j++) billInfo.itemPrice[i][j] = ' ';
  		
  		for(int j=0;j<quants[itemSec][i].length();j++)
  			if(quants[itemSec][i][j] != '\n')
  				billInfo.itemQuant[i][j] = quants[itemSec][i][j];
  			else break;
  		billInfo.itemQuant[i][quants[itemSec][i].length()] = '\0';
  		//for(int j=quants[itemSec][i].length();j<10;j++) billInfo.itemQuant[i][j] = ' ';
  		//billInfo.itemName[i] = Items[itemSec][i];
  		//billInfo.itemPrice[i] = Prices[itemSec][i];
  		//billInfo.itemQuant[i] = quants[itemSec][i];
  		cout  << billInfo.itemName[i] << " " << billInfo.itemPrice[i] << " " << billInfo.itemQuant[i] << " " << endl;
  	}
  	cout << endl;
  	cout << "Total items " << billInfo.nItems << endl;
  	cout << "Total cost" << billInfo.total << endl;
  	//cvNamedWindow("mainWin", CV_WINDOW_AUTOSIZE); 
  	//cvMoveWindow("mainWin", 100, 100);
  	//cvShowImage("mainWin", img1 );
  	//cvWaitKey(0);
  	return 0;
}
