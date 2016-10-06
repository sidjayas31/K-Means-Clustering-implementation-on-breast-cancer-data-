package ppv;
/**
 * created by sidjayas
 */
// V-fold validation of kmeans algorithm for Breast Cancer Data
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class V_fold_Kmeans {
	//parameters we need to specify
	static int train_rows=615; //number of samples in train data
    static int train_cols=10;  //number of attributes in train data (give one more than the number of attributes as the 1st attribute is SCN
    static int colss=train_cols+1;
    static int test_rows=68; // no of samples in test data
    static int test_cols=10; // no of attributes in test data
    static int n_centroids=2; //number of clusters to be formed 
    static int iterator=7; // max iterations 
    static double threshold=0.1; // threshold to stop clustering
    static String train_filename = "C:/Users/siddharth/Desktop/Data Mining/V_fold/d-d6.csv" ; //train data file name
    static String test_filename = "C:/Users/siddharth/Desktop/Data Mining/V_fold/d6.csv"; //test data file name
    static int[][] clusters = new int[n_centroids][train_rows];
    @SuppressWarnings("unchecked")
	static ArrayList<Integer>[] result_clust=(ArrayList<Integer>[])new ArrayList[n_centroids];
    @SuppressWarnings("unchecked")
    static ArrayList<Integer>[] actual_clust_train=(ArrayList<Integer>[])new ArrayList[n_centroids];
    @SuppressWarnings("unchecked")
    static ArrayList<Integer>[] actual_clust_test=(ArrayList<Integer>[])new ArrayList[n_centroids];
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, FileNotFoundException
	{
	    
		String a ;
	    BufferedReader br = null;
	    BufferedReader test_br = null;
		String[][] data = new String[train_rows][colss];
		String[][] data_1 = new String[test_rows][colss];
		int [][] data_w_class=new int[train_rows][colss];
		int [][] data_w_class_1=new int[test_rows][colss];
		int [][] train_data = new int [train_rows][train_cols];
		int [][] test_data= new int [test_rows][test_cols];
		double[] dist_b_c= new double [n_centroids]; // dist btwn current and prev clusters
		int[][] centroids=new int[n_centroids][train_cols];
		int[][] prevcentroids= new int [n_centroids][train_cols];
		int[] rand = new int [n_centroids];
			
		System.out.println(" Clustering train data");
		for (int k=0;k<n_centroids;k++)
		{
			result_clust[k]=new ArrayList<Integer>();
		}
		for (int k=0;k<2;k++)
		{
			actual_clust_train[k]=new ArrayList<Integer>();
			actual_clust_test[k]=new ArrayList<Integer>();
		}
		
		//Reading file
		br=new BufferedReader(new FileReader(train_filename));
		a = br.readLine();
		for (int i=0; i<train_rows;i++)
		{
			a = br.readLine();
			for (int j=0;j<colss;j++)
			{
				data[i][j]=a.split(",")[j];
			}
			for(int j=0;j<colss;j++)
			{
				data_w_class[i][j]=Integer.parseInt(data[i][j]);
				
			}
			
			for(int j=0;j<train_cols;j++)
			{
				train_data[i][j]=data_w_class[i][j];
			}
		}
		
		//intial centroid assigning
		for(int i=0;i<n_centroids;i++)
		{
			Random r = new Random() ;
			rand[i]=r.nextInt(train_rows);
		}
		for (int i=0;i<n_centroids;i++)
		{
			for(int j=0;j<train_cols;j++)
			{
				centroids[i][j]=train_data[rand[i]][j];
			}
		}
			
	
		for (int k=0;k<n_centroids;k++)
		{
			centroids[k][0]=999; //eleminating SCN from centroids
		}
		for (int i=0;i<iterator;i++)
		{
			clustering(train_data,centroids,prevcentroids,train_rows,train_cols);
			

			if (i==0)
			{
				for (int k=0;k<n_centroids;k++)
				{
					result_clust[k]=new ArrayList<Integer>();
				}
			}
			if (i>0)
			{
				for(int k=0;k<n_centroids;k++)
				{
					dist_b_c[k]=0;
				}
				int dist_counter=0;
				
				//checking if change in dis is less than threshold
				for(int k=0;k<n_centroids;k++)
				{
					for (int j=1;j<train_cols;j++)
					{
						dist_b_c[k]=dist_b_c[k]+(prevcentroids[k][j]-centroids[k][j])*(train_data[k][j]-centroids[k][j]);
					}
					if(dist_b_c[k]<0)
					{
						dist_b_c[k]=Math.sqrt(-dist_b_c[k]);
						
					}
					else
					{
						dist_b_c[k]=Math.sqrt(dist_b_c[k]);
					}
					if (dist_b_c[k]<threshold)
					{
						dist_counter+=1;
					}
				}
				//if all change is less than thereshold exit
				if (dist_counter==n_centroids)
				{
					System.out.println(" The change in distance between all centroids of previous itration is less than "+ threshold );
					System.out.println(" Hence exiting");
					System.out.println(" Converged in " + (i+1) + " iterations");
					break;
				}
				if(i!=iterator-1)
				{
					for (int k=0;k<n_centroids;k++)
					{
						result_clust[k]=new ArrayList<Integer>();
					}
				}
				//if max itration is reached exit
				if(i==iterator-1)
				{
					System.out.println(" Reached maximum iterations..... hence exiting");
					System.out.println(" Converged in " + (i+1) + " Iterations");
				}
			}
			
		}
		System.out.println("final cluster result");
		for(int k=0;k<n_centroids;k++)
		{
			System.out.println(result_clust[k].toString());
			System.out.println(" size of cluster "+(k+1)+" = " + result_clust[k].size());
		}

		System.out.println(" Calculating PPV for training data");
		
		for(int i=0;i<train_rows;i++)
		{
			if (data_w_class[i][10]==2)
			{
				actual_clust_train[0].add(data_w_class[i][0]);
				continue;
			}
			if(data_w_class[i][10]==4);
			{
				actual_clust_train[1].add(data_w_class[i][0]);		
			}
		}
		System.out.println(" number of benine patients in training dataset = " +actual_clust_train[0].size());
		System.out.println(" number of malignant patients in training dataset = " +actual_clust_train[1].size());
		
		//calculating number of benign and malignant in each cluster
		int b1_train=0, b2_train=0,m1_train=0,m2_train=0;
		
		for(int j=0;j<result_clust[0].size();j++)
		{
			for(int i=0;i<train_rows;i++)
			{
				int counter=0;
			
				if(result_clust[0].get(j)==data_w_class[i][0])
				{
					counter+=1;
					if(data_w_class[i][10]==2)
					{
						b1_train=b1_train+1;
							
					}
						
				}
					
				if(counter==1)
				{
					break;
				}
			}
		}
		for(int j=0;j<result_clust[1].size();j++)	
		{
			for(int i=0;i<train_rows;i++)
			{
				int counter=0;
				if(result_clust[1].get(j)==data_w_class[i][0])
				{
					counter+=1;
					if(data_w_class[i][10]==2)
					{
						b2_train=b2_train+1;
						
					}
						
				}
				if(counter==1)
				{
					break;
				}
			}
		}

		m1_train=(result_clust[0].size()-b1_train);
		m2_train=(result_clust[1].size()-b2_train);
	
	//cal PPV of train data		
		double tp_train=0,fp_train=0;
		double ppv_train=0;
		if(b1_train>=m1_train)
		{
			tp_train=tp_train+b1_train;
			fp_train=fp_train+m1_train;
			
		}
		else 
		{
			tp_train=tp_train+m1_train;
			fp_train=fp_train+b1_train;
		}
		
		if(b2_train>=m2_train)
		{
			tp_train=tp_train+b2_train;
			fp_train=fp_train+m2_train;
		}
		else
		{
			tp_train=tp_train+m2_train;
			fp_train=fp_train+b2_train;
		}
		System.out.println(" True Pos =" + tp_train );
		System.out.println(" False Pos =" + fp_train);
		ppv_train=(tp_train/(tp_train+fp_train));
		System.out.println( " PPV ="+ ppv_train );

		// START OF TEST CLUSTERING USING THE FINAL CENTROIDS OF TRAINING DATA			
		
		System.out.println("clustering test data");
		test_br=new BufferedReader(new FileReader(test_filename));
		a = test_br.readLine();
		for (int i=0; i<test_rows;i++)
		{
			a = test_br.readLine();
			for (int j=0;j<colss;j++)
			{
				data_1[i][j]=a.split(",")[j];
			}
			for(int j=0;j<colss;j++)
			{
				data_w_class_1[i][j]=Integer.parseInt(data_1[i][j]);
			}
			for(int j=0;j<test_cols;j++)
			{
				test_data[i][j]=data_w_class_1[i][j];
			}
		}
		
		for(int i=0;i<test_rows;i++)
		{
			if (data_w_class_1[i][10]==2)
			{
				actual_clust_test[0].add(data_w_class[i][0]);
				continue;
			}
			if(data_w_class_1[i][10]==4);
			{
				actual_clust_test[1].add(data_w_class[i][0]);		
			}
		}
		for (int k=0;k<n_centroids;k++)
		{
			result_clust[k]=new ArrayList<Integer>();
		}
		
		//Calling TEST Clustering function
		//not looping because test data should only assign to the train clusters 
		test_clustering(test_data,centroids,test_rows,test_cols);
		
		
		System.out.println("final test cluster result");
		for(int k=0;k<n_centroids;k++)
		{
			System.out.println(result_clust[k].toString());
			System.out.println(" size of cluster "+(k+1)+" = " + result_clust[k].size());
		}
			
		
		System.out.println(" number of benine patients in test dataset = " +actual_clust_test[0].size());
		System.out.println(" number of malignant patients in test dataset = " +actual_clust_test[1].size());
		
		//cal number of benign and malignant in the test cluster
		int b1_test=0, b2_test=0,m1_test=0,m2_test=0;
			
		for(int j=0;j<result_clust[0].size();j++)
		{
			for(int i=0;i<train_rows;i++)
			{
				int counter=0;
			
				if(result_clust[0].get(j)==data_w_class[i][0])
				{
					counter+=1;
					if(data_w_class[i][10]==2)
					{
						b1_test=b1_test+1;
							
					}
						
				}
					
				if(counter==1)
				{
					break;
				}
			}
		}
		for(int j=0;j<result_clust[1].size();j++)	
		{
			for(int i=0;i<test_rows;i++)
			{
				int counter=0;
				if(result_clust[1].get(j)==data_w_class_1[i][0])
				{
					counter+=1;
					if(data_w_class_1[i][10]==2)
					{
						b2_test=b2_test+1;
						
					}
						
				}
				if(counter==1)
				{
					break;
				}
			}
		}

		m1_test=(result_clust[0].size()-b1_test);
		m2_test=(result_clust[1].size()-b2_test);
		
		//calc PPV for test cluster	
		double tp_test=0,fp_test=0;
		double ppv_test=0;
		if(b1_test>=m1_test)
		{
			tp_test=tp_test+b1_test;
			fp_test=fp_test+m1_test;
			
		}
		else 
		{
			tp_test=tp_test+m1_test;
			fp_test=fp_test+b1_test;
		}
		
		if(b2_test>=m2_test)
		{
			tp_test=tp_test+b2_test;
			fp_test=fp_test+m2_test;
		}
		else
		{
			tp_test=tp_test+m2_test;
			fp_test=fp_test+b2_test;
		}
		System.out.println(" True Pos =" + tp_test );
		System.out.println(" False Pos =" + fp_test);
		ppv_test=(tp_test/(tp_test+fp_test));
		System.out.println( " PPV ="+ ppv_test );
}

	private static void clustering(int[][] fdata, int[][] centroids,int[][]prevcentroids, int rows, int cols) 
	{
		int [][][] clusterdata= new int[n_centroids][rows][cols];
		double[] dist = new double[n_centroids];
		//initialize cluster data to 999
		for (int k=0;k<n_centroids;k++)
		{
			for (int i=0;i<rows;i++) 
			{
				for (int j=0;j<cols;j++)
				{
					clusterdata[k][i][j]=999;
				}
			}
		}
		
		//initialize cluster to 999
		for (int k=0;k<n_centroids;k++)
		{
			for (int i=0;i<rows;i++) 
			{
				clusters[k][i]=999;
			}
		}
		for (int i=0;i<rows;i++) 
		{
			// init all dist to 0 for each data row 
			int pos=0;
			for (int k=0;k<n_centroids;k++)
			{
				dist[k]=0;
			}
			for (int k=0;k<n_centroids;k++)
			{
				for (int j=1;j<cols;j++)//starting with 1 because 1st column is SCN...we dont need it for dist
				{ 
					//calc dist for each row with each centroid
					dist[k]=dist[k]+(fdata[i][j]-centroids[k][j])*(fdata[i][j]-centroids[k][j]);
				}
				dist[k]=Math.sqrt(dist[k]);
			}
			//finding shortest dist
			double a=dist[0];
			for(int z=1;z<n_centroids;z++)
			{
				if (dist[z]<=a) // using = because 2 dist may be same 
				{
					a=dist[z];
					pos=z;
				}
			}
			//System.out.println("adding data to clusterdata");
			for(int j=0;j<cols;j++)
			{
				clusterdata[pos][i][j]= fdata[i][j];	
			}
			result_clust[pos].add(fdata[i][0]);
		}
	for (int k=0;k<n_centroids;k++)
	{
		for (int j=0;j<cols;j++)
		{
			prevcentroids[k][j]=999;
		}
	}
	//assaining current cent to prev b4 calculating the new centroids
	for (int k=0;k<n_centroids;k++)
	{
		for (int j=0;j<cols;j++)
		{
			prevcentroids[k][j]=centroids[k][j];
		}
	}
	
	//calculating new centroids
	for (int k=0;k<n_centroids;k++)
	{
		centroids[k][0]=999; // eleminating SCN from centroids
		for(int j=1;j<cols;j++)
		{
			centroids[k][j]=0; // resetting centroid
			int x=0;
			for(int i=0;i<rows;i++)
			{
				clusterdata[k][i][0]=999; // eleminating SCN from clusterdata  
				if(clusterdata[k][i][j]!=999)
				{
					centroids[k][j]=centroids[k][j]+clusterdata[k][i][j];
					x=x+1;
				}
			}
			if(x!=0)
			centroids[k][j]=centroids[k][j]/x;
		}
	}	
}		

// TEST_clustering

	private static void test_clustering(int[][] fdata, int[][] centroids, int rows, int cols) 
	{
		int [][][] clusterdata= new int[n_centroids][rows][cols];
		double[] dist = new double[n_centroids];
		
		//initialize cluster data to 999
		for (int k=0;k<n_centroids;k++)
		{
			for (int i=0;i<rows;i++) 
			{
				for (int j=0;j<cols;j++)
				{
					clusterdata[k][i][j]=999;
				}
			}
		}
		//initialize cluster to 999
		for (int k=0;k<n_centroids;k++)
		{
			for (int i=0;i<rows;i++) 
			{
				clusters[k][i]=999;
			}
		}
		for (int i=0;i<rows;i++) 
		{
			// init all dist to 0 for each data row 
			int pos=0;
			for (int k=0;k<n_centroids;k++)
			{
				dist[k]=0;
			}
			for (int k=0;k<n_centroids;k++)
			{
				
				for (int j=1;j<cols;j++)//starting with 1 because 1st column is SCN...we dont need it for dist
				{
					//calc dist for each row with each centroid
					dist[k]=dist[k]+(fdata[i][j]-centroids[k][j])*(fdata[i][j]-centroids[k][j]);
				}
				dist[k]=Math.sqrt(dist[k]);
			}
			//finding shortest dist
			double a=dist[0];
			for(int z=1;z<n_centroids;z++)
			{
				if (dist[z]<=a) // using = because 2 dist may be same 
				{
					a=dist[z];
					pos=z;
				}
			}
			for(int j=0;j<cols;j++)
			{
				clusterdata[pos][i][j]= fdata[i][j];	
			}

			result_clust[pos].add(fdata[i][0]);
		}
	}
}		





