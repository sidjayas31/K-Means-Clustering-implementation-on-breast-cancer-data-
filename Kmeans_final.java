package kmeans;

/**
 * created by sidjayas
 */

// K - means implementation and Calculation of PPV for Breast Cancer data
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Kmeans_final {
	//parameters we need to specify
	static int rows=683; //number of samples
    static int cols=10;  //number of attributes (give one more than the number of attributes as the 1st attribute is SCN
    static int colss=cols+1;
    static int n_centroids=2; //number of clusters to be formed 
    static int iterator=7; // max iterations 
    static double threshold=0.1; // threshold to stop clustering
    static String filename = "C:/Users/siddharth/Desktop/Data Mining/bc_cleaned.csv" ; // data file path // remove 
    static int[][] clusters = new int[n_centroids][rows];
    @SuppressWarnings("unchecked")
	static ArrayList<Integer>[] result_clust=(ArrayList<Integer>[])new ArrayList[n_centroids];
    @SuppressWarnings("unchecked")
    static ArrayList<Integer>[] actual_clust=(ArrayList<Integer>[])new ArrayList[n_centroids];
    
	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException, FileNotFoundException
	{
		BufferedReader br = null;
		String[][] data = new String[rows][colss];
		int [][] data_w_class=new int[rows][colss];
		int[][] data_wo_class = new int [rows][cols];
		float tp[]=new float[n_centroids]; // tp of each cluster
		float fp[]=new float[n_centroids]; // fp of each cluster
		float tot_tp=0,tot_fp=0,ppv=0;
		float b[]=new float [n_centroids]; // benine in each cluster
		float m[]=new float [n_centroids]; // malignent in each cluster
		int[][] centroids=new int[n_centroids][cols];
		int[][] prevcentroids= new int [n_centroids][cols];
		int[] rand = new int [n_centroids];
		int dist_counter=0;
		String a ;
		double[] dist_b_c= new double [n_centroids];
		
		for (int k=0;k<n_centroids;k++)
		{
			result_clust[k]=new ArrayList<Integer>();
		}
		for (int k=0;k<2;k++)
		{
			actual_clust[k]=new ArrayList<Integer>();
		}
		
		//reading data
		br=new BufferedReader(new FileReader(filename));
		a=br.readLine();
		for (int i=0; i<rows;i++)
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
								
			for(int j=0;j<cols;j++)
			{
				data_wo_class[i][j]=Integer.parseInt(data[i][j]);
			}
		}
		
		//intial centroid assigning
		for(int i=0;i<n_centroids;i++)
		{
			Random r = new Random() ;
			rand[i]=r.nextInt(rows);
		}
		for (int i=0;i<n_centroids;i++)
		{
			for(int j=0;j<cols;j++)
			{
				centroids[i][j]=data_wo_class[rand[i]][j];
			}
		}
			
	
		for (int k=0;k<n_centroids;k++)
		{
			centroids[k][0]=999; //eleminating SCN from centroids
		}
		for (int i=0;i<iterator;i++)
		{//calling clustering func
			clustering(data_wo_class,centroids,prevcentroids);
	
			if (i==0)
			{
				for (int k=0;k<n_centroids;k++)
				{
					result_clust[k]=new ArrayList<Integer>();
				}
			}
			//checking dist change for all centroids 
			if (i>0)
			{
				for(int k=0;k<n_centroids;k++)
				{
					dist_b_c[k]=0;
				}
				
				for(int k=0;k<n_centroids;k++)
				{
					for (int j=1;j<cols;j++)
					{
						dist_b_c[k]=dist_b_c[k]+(prevcentroids[k][j]-centroids[k][j])*(data_wo_class[k][j]-centroids[k][j]);
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
				//exit if less than threshold
				if (dist_counter==n_centroids)
				{
					System.out.println("the change in distance between all centroids of previous itration is less than "+ threshold+ " .....hence exiting");
					System.out.println("Converged in " + (i+1) + " iterations");
					break;
				}
				if(i!=iterator-1)
				{
				for (int k=0;k<n_centroids;k++)
				{
					result_clust[k]=new ArrayList<Integer>();
				}
				}
				//exit if max itration reached
				if(i==iterator-1)
				{
					System.out.println("reached maximum iterations..... hence exiting");
					System.out.println("Converged in " + (i+1) + " Iterations");
				}
			}
			
		}
		
			System.out.println("final cluster result");
			for(int k=0;k<n_centroids;k++)
			{
				System.out.println(result_clust[k].toString());
				System.out.println(" size of cluster "+(k+1)+" = " + result_clust[k].size());
			}

		System.out.println("calc PPV");
		
		for(int i=0;i<rows;i++)
		{
			if (data_w_class[i][10]==2)
			{
				actual_clust[0].add(data_w_class[i][0]);
				continue;
			}
			if(data_w_class[i][10]==4);
			{
					actual_clust[1].add(data_w_class[i][0]);
			}
		}
		
		System.out.println(" number of benine patients in the given dataset  = " +actual_clust[0].size());
		System.out.println(" number of malignant patients in the given dataset = " +actual_clust[1].size());
		
		
		//calc benign and malignant for each cluster
		for(int i=0;i<n_centroids;i++)
		{
			b[i]=0;
			m[i]=0;
		}
		
		for (int k=0;k<n_centroids;k++)
		{
			for(int j=0;j<result_clust[k].size();j++)
			{
				for(int i=0;i<rows;i++)
				{
					int counter=0;
					if(result_clust[k].get(j)==data_w_class[i][0])
					{
						if(data_w_class[i][10]==2)
						{
							b[k]=b[k]+1;
							counter=+1;
							if(counter==1)
							{
								break;
							}
						}
						
					}
				}
			}
			
		}
		
		for(int k=0;k<n_centroids;k++)
		{
			m[k]=result_clust[k].size()-b[k];
		}
		
		 for(int k=0;k<n_centroids;k++)
		{
			System.out.println("");
			System.out.println(" number of benine in cluster "+(k+1) +" = "+b[k]);
			System.out.println(" number of malignent in cluster "+(k+1) +" = "+m[k]);
			
		}
		
		//calc PPV
		for (int k=0;k<n_centroids;k++)
		{
			tp[k]=0;
			fp[k]=0;
		}
		
		for(int k=0;k<n_centroids;k++)
		{
			if(b[k]>=m[k])
			{
				tp[k]=tp[k]+b[k];
				fp[k]=fp[k]+m[k];
				
			}
			else 
			{
				tp[k]=tp[k]+m[k];
				fp[k]=fp[k]+b[k];
			}
		}
		
		for(int k=0;k<n_centroids;k++)
		{
			tot_tp=tot_tp+tp[k];
			tot_fp=tot_fp+fp[k];
		}
		ppv=(tot_tp/(tot_tp+tot_fp));
		System.out.println(" True Pos =" + tot_tp );
		System.out.println(" False Pos =" + tot_fp);

		System.out.println( " PPV ="+ ppv );
		
	
}

	private static void clustering(int[][] fdata, int[][] centroids,int[][]prevcentroids) 
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
			
			// Calculation of distance
			for (int k=0;k<n_centroids;k++)
			{
				for (int j=1;j<cols;j++)//starting with 1 because 1st column is SCN...we dont need it for dist
				{   //calc dist for each row with each centroid
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
			//clustering data
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
	
	//assigning current centoid to prev_centroid b4 calculating the new centroids
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


}
