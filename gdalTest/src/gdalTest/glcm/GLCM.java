package gdalTest.glcm;

public class GLCM {

	public static int GLCM_DIS =3;//灰度共生矩阵的统计距离
	public static int GLCM_CLASS  =16;//计算灰度共生矩阵的图像灰度值等级化
	public static int GLCM_ANGLE_HORIZATION  =0;//水平
	public static int GLCM_ANGLE_VERTICAL    =1;//垂直
	public static int GLCM_ANGLE_DIGONAL_LEFT     =2; //对角 135度
	public static int GLCM_ANGLE_DUGIBAL_RIGHT     =3; //对角 45度
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public FeatureVector calGLCM(int width,int height,int[]data,int angleDirection)  
	{  
	    int i,j;   
	    int [] glcm = new int[GLCM_CLASS * GLCM_CLASS];  
	    int []histImage = new int[width * height];   
	    if(null == glcm || null == histImage) {
	    	  return null;  
	    } 
	    //灰度等级化---分GLCM_CLASS个等级   
	    for(i = 0;i < height;i++){  
	        for(j = 0;j < width;j++){  
	            histImage[i * width + j] = (int)(data[width * i + j] * GLCM_CLASS / 256);  
	        }  
	    }  
	  
	    //初始化共生矩阵  
	    for (i = 0;i < GLCM_CLASS;i++)  
	        for (j = 0;j < GLCM_CLASS;j++)  {
	        	glcm[i * GLCM_CLASS + j] = 0;
	        	}  
	  
	    //计算灰度共生矩阵  
	    int w,k,l;  
	    //水平方向  
	    if(angleDirection == GLCM_ANGLE_HORIZATION)  
	    {  
	        for (i = 0;i < height;i++)  
	        {  
	            for (j = 0;j < width;j++)  
	            {  
	                l = histImage[i * width + j];  
	                if(j + GLCM_DIS >= 0 && j + GLCM_DIS < width)  
	                {  
	                    k = histImage[i * width + j + GLCM_DIS];  
	                    glcm[l * GLCM_CLASS + k]++;  
	                }  
	                if(j - GLCM_DIS >= 0 && j - GLCM_DIS < width)  
	                {  
	                    k = histImage[i * width + j - GLCM_DIS];  
	                    glcm[l * GLCM_CLASS + k]++;  
	                }  
	            }  
	        }  
	    }  
	    //垂直方向  
	    else if(angleDirection == GLCM_ANGLE_VERTICAL)  
	    {  
	        for (i = 0;i < height;i++)  
	        {  
	            for (j = 0;j < width;j++)  
	            {  
	                l = histImage[i * width + j];  
	                if(i + GLCM_DIS >= 0 && i + GLCM_DIS < height)   
	                {  
	                    k = histImage[(i + GLCM_DIS) * width + j];  
	                    glcm[l * GLCM_CLASS + k]++;  
	                }  
	                if(i - GLCM_DIS >= 0 && i - GLCM_DIS < height)   
	                {  
	                    k = histImage[(i - GLCM_DIS) * width + j];  
	                    glcm[l * GLCM_CLASS + k]++;  
	                }  
	            }  
	        }  
	    }  
	    //对角方向  135度
	    else if(angleDirection == GLCM_ANGLE_DIGONAL_LEFT)  
	    {  
	        for (i = 0;i < height;i++)  
	        {  
	            for (j = 0;j < width;j++)  
	            {  
	                l = histImage[i * width + j];  
	  
	                if(j + GLCM_DIS >= 0 && j + GLCM_DIS < width && i + GLCM_DIS >= 0 && i + GLCM_DIS < height)  
	                {  
	                    k = histImage[(i + GLCM_DIS) * width + j + GLCM_DIS];  
	                    glcm[l * GLCM_CLASS + k]++;  
	                }  
	                if(j - GLCM_DIS >= 0 && j - GLCM_DIS < width && i - GLCM_DIS >= 0 && i - GLCM_DIS < height)  
	                {  
	                    k = histImage[(i - GLCM_DIS) * width + j - GLCM_DIS];  
	                    glcm[l * GLCM_CLASS + k]++;  
	                }  
	            }  
	        }  
	    }  //对角方向  45度
	    else if(angleDirection == GLCM_ANGLE_DIGONAL_LEFT)  
	    {  
	        for (i = 0;i < height;i++)  
	        {  
	            for (j = 0;j < width;j++)  
	            {  
	                l = histImage[i * width + j];  
	  
	                if(j + GLCM_DIS >= 0 && j + GLCM_DIS < width && i - GLCM_DIS >= 0 && i - GLCM_DIS < height)  
	                {  
	                    k = histImage[(i - GLCM_DIS) * width + j + GLCM_DIS];  
	                    glcm[l * GLCM_CLASS + k]++;  
	                }  
	                if(j - GLCM_DIS >= 0 && j - GLCM_DIS < width && i + GLCM_DIS >= 0 && i + GLCM_DIS < height)  
	                {  
	                    k = histImage[(i + GLCM_DIS) * width + j - GLCM_DIS];  
	                    glcm[l * GLCM_CLASS + k]++;  
	                }  
	            }  
	        }  
	    }   
	  
	    //计算特征值  
	    double meanI=0 , meanJ = 0;//均值
	    double entropy = 0,energy = 0,contrast = 0,homogenity = 0;  
	    int index=0;
	    for (i = 0;i < GLCM_CLASS;i++)  
	    {  
	        for (j = 0;j < GLCM_CLASS;j++)  
	        {  
	        	index=i * GLCM_CLASS + j;
	            if(glcm[index] > 0) { 
	            //熵  
	             entropy -= glcm[index] * Math.log((glcm[index]));  
	            //能量  
	            energy += glcm[index] * glcm[index];  
	            //对比度  
	            contrast += (i - j) * (i - j) * glcm[index];  
	            //一致性   逆差值（剧本稳定）
	            homogenity += 1.0 / (1 + (i - j) * (i - j)) * glcm[index]; 
	            meanI += i*glcm[index];
	            meanJ += j*glcm[index];
	            }     
	        }  
	    } 
	    double deltaI=0,deltaJ=0;//方差
	    double doubleIJ=0;
	    //计算相关性
	    for (i = 0;i < GLCM_CLASS;i++)  
	    {  
	        for (j = 0;j < GLCM_CLASS;j++)  
	        {  
	        	index=i * GLCM_CLASS + j;
	            if(glcm[index] > 0) { 
	             deltaI += (i-meanI)*(i-meanI)*glcm[index];
	             deltaJ += (j-meanJ)*(j-meanJ)*glcm[index];
	             doubleIJ += i*j*glcm[index];
	            }     
	        }  
	    } 
	    double relativity=0;
        relativity=(doubleIJ-meanJ*meanI)/(deltaI*deltaJ);//相关性
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    //返回特征值
	    FeatureVector featureVector=new FeatureVector();
	    featureVector.setEntropy(entropy);  
	    featureVector.setEnergy(energy);  
	    featureVector.setContrast(contrast); 
	    featureVector.setHomogenity(homogenity);  
	     featureVector.setRelativity(relativity);
	    return featureVector;  
	}  
}