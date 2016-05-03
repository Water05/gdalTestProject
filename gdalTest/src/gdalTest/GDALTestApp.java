package gdalTest;

import org.gdal.gdal.Band;
import org.gdal.gdal.Dataset;
import org.gdal.gdal.Driver;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.osr.SpatialReference;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.util.Arrays;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class GDALTestApp {
	/**
	 * @param args
	 * @throws IOException 
	 */
	static {
		gdal.AllRegister();
		ogr.RegisterAll();//记得添加驱动注册
		// 为了支持中文路径，请添加下面这句代码  
		gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8","NO");  
		// 为了使属性表字段支持中文，请添加下面这句  
		gdal.SetConfigOption("SHAPE_ENCODING",""); 
	}
	public static void main(String[] args) throws IOException 
	{
		//第一步：     将Tiff读取到数组       
		System.out.println("1）将Tiff读取到数组");
		//String fileName_tif = "E:\\JAVA_neunn\\gdalTest\\source\\GF2_PMS2_132120030030_20150212_0000647892.TIFF";
		String fileName_tif = "E:\\JAVA_neunn\\gdalTest\\source\\GF2_PMS2_132120030030_20150212_00006478923.TIF";
	//	gdal.AllRegister();

		Dataset pInputDataset = gdal.Open(fileName_tif, gdalconstConstants.GA_ReadOnly);
		if (pInputDataset == null)
		{
			System.err.println("GDALOpen failed - " + gdal.GetLastErrorNo());
			System.err.println(gdal.GetLastErrorMsg());

			System.exit(1); 
		}
		int xSize=pInputDataset.GetRasterXSize();
		int ySize=pInputDataset.GetRasterYSize();
		int bandCount=pInputDataset.GetRasterCount();
		System.out.println("xSize  "+xSize + "  "+"ySize  "+ ySize+"   "+"bandCount  "+bandCount);
		int [] pInputBuf=new int[xSize*ySize*bandCount];
		int [] pBandMap=new int[bandCount];
		for(int i=0;i<bandCount;i++){
			pBandMap[i]=i+1;
		}
		double []gt={0,0,0,0,0,0};
		pInputDataset.GetGeoTransform(gt);
		String strProjection=pInputDataset.GetProjectionRef();
		pInputDataset.ReadRaster(0, 0, xSize, ySize, xSize, ySize, gdalconstConstants.GDT_Int32, pInputBuf, pBandMap);
	     //	pInputDataset.delete();
		System.out.println("          读取完成");
		
		
		// 可选
		//gdal.GDALDestroyDriverManager();
		
		
		
		
		
		//第二步:   加载分类模型
		System.out.println("2）读取SVM模型");
		String svmPath="E:\\JAVA_neunn\\gdalTest\\source\\22.model";
		svm_model pModel = svm.svm_load_model(svmPath);
		svm_node []sn = new svm_node[bandCount+1];
		for( int i = 0; i < bandCount; i++)
		{   sn[i]=new svm_node();
			sn[i].index = i;
		}
		sn[bandCount]=new svm_node();
		sn[bandCount].index = -1;	// 结束符，代替波段数
		System.out.println("    读取完成");
		
		
		
		
		
		
		
		
		
		//第三步：应用分类模型：
		System.out.println("3）植被检测");
		//创建分类结果数据集
		int nPixelNum = xSize*ySize;
		//double []pDst = new double[nPixelNum];
		int []pDst = new int[nPixelNum];
		//int []pDst = new int[4000000];

		//植被检测
		int totalPixelNum = 0;
		int cloudPixelNum = 0;
		for( int i = 0; i < nPixelNum; i++ )
			//for( int i = 0; i < 4000000; i++ )
		{
			//计算无效点
			int inValidNum = 0;
			for( int j = 0; j < bandCount; j++ )
			{
				sn[j].value = (double)pInputBuf[j*nPixelNum+i];
				//if ( sn[j].value < MIN_VAL)
				//{
				//	inValidNum++;
				//}
			}

			if ( inValidNum != bandCount )
			{
				totalPixelNum++;
				//svm_predict( pModel, sn );
				pDst[i]=(int)svm.svm_predict(pModel,sn);
				//pDst[i] = (BYTE)svm_predict( pModel, sn );
				if ( pDst[i] == 1 )
				{
					cloudPixelNum++;
				}
			}
			else
			{
				pDst[i] = 0;
			}
		}
		
		//计算植被含量百分比
		if( totalPixelNum > 0 )
		{
			double cloudContent = cloudPixelNum*1.0/totalPixelNum;
			System.out.println("植被含量百分比    "+cloudContent);
		}
		
		
		
		
		//第四步：保存生成的分类文件
		System.out.println("4）保存生成的分类文件 ");
		//生成矢量文件;
		Driver drivers=pInputDataset.GetDriver();
		 String strDriverName = "ESRIShapefile";
         org.gdal.ogr.Driver oDriver =ogr.GetDriverByName(strDriverName);
		String memName = "E:\\JAVA_neunn\\gdalTest\\source\\mask.tiff";
		Dataset pMaskDataSet =drivers.Create( memName, xSize,  ySize,1);
		pMaskDataSet.SetGeoTransform(gt);
		pMaskDataSet.SetProjection(strProjection);
		int [] pBandMap2=new int[1];
		for(int i=0;i<1;i++){
			pBandMap2[i]=i+1;
		}
		pMaskDataSet.WriteRaster(0, 0, xSize, ySize, xSize, ySize, gdalconstConstants.GDT_UInt32, pDst, pBandMap2);
		System.out.println("分类文件 完成   "+memName);
		
		
	/*	//第五步：生成SHP文件
		System.out.println("5）生成shp文件");
		String dstFileString="E:\\JAVA_neunn\\gdalTest\\source\\shape.shp";
		ImagePolygonize("E:\\JAVA_neunn\\gdalTest\\source\\mask.tiff",dstFileString,"ESRI Shapefile"); 
		System.out.println("生成shp文件 完成   ");*/
	}

	
	public static int ImagePolygonize(String pszSrcFile,String pszDstFile,String pszFormat){
	//	gdal.AllRegister();
	//	ogr.RegisterAll();//记得添加驱动注册
		// 为了支持中文路径，请添加下面这句代码  
	//gdal.SetConfigOption("GDAL_FILENAME_IS_UTF8","NO");  
		// 为了使属性表字段支持中文，请添加下面这句  
	//	gdal.SetConfigOption("SHAPE_ENCODING",""); 
		Dataset poSrcDS = gdal.Open(pszSrcFile, gdalconstConstants.GA_ReadOnly);
		 if(poSrcDS==null)  
		    {  
		        return 0;  
		    }  
		// 创建输出矢量文件 
		 org.gdal.ogr.Driver oDriver =ogr.GetDriverByName(pszFormat); 
		 if (oDriver == null)  
		 {  
		 System.out.println(pszSrcFile+ " 驱动不可用！\n");  
		     return 0;  
		 }  
		// 创建数据源  
		 DataSource oDS = oDriver.CreateDataSource(pszDstFile,null);  
		 if (oDS == null)  
		 {  
		 System.out.println("创建矢量文件【"+ pszDstFile +"】失败！\n" );  
		 return 0;  
		 }  
		 
		// 创建图层，创建一个多边形图层，这里没有指定空间参考，如果需要的话，需要在这里进行指定  
		 SpatialReference poSpatialRef=new SpatialReference(poSrcDS.GetProjectionRef());
		 Layer oLayer =oDS.CreateLayer("TestPolygon", poSpatialRef, ogr.wkbPolygon, null);  
		 if (oLayer == null)  
		 {   
		 System.out.println("图层创建失败！\n");  
		 return 0;  
		 }
		 
		// 下面创建属性表  
		// 先创建一个叫FieldID的整型属性 ,里面保存对应的栅格的像元值   
		FieldDefn oFieldID = new FieldDefn("FieldID", ogr.OFTInteger);  
		oLayer.CreateField(oFieldID); 
		Band hSrcBand =(Band)poSrcDS.GetRasterBand(1);//获取图像的第一个波段  
		gdal.Polygonize(hSrcBand, null, oLayer, 0);//调用栅格矢量化 
	    return 0;
	
	
}
}
