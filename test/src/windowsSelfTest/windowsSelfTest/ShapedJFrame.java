package windowsSelfTest.windowsSelfTest;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.sun.awt.AWTUtilities;

//生成与图片样子一样的窗体，并将图片画到窗体上

public class ShapedJFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	private Point origin; //用于移动窗体
	private Image img;
	
	public ShapedJFrame(JFrame frame, Image image){
		super();
		
		img = image;
		MediaTracker mt=new MediaTracker(this);
		mt.addImage(img, 0);
		try {
			mt.waitForAll();
		}catch (InterruptedException e) {
		    e.printStackTrace();
		}
		    
		try {
		    initialize(frame, img);//窗体初始化
		}catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	//窗体初始化
	private void initialize(JFrame frame, Image img) throws IOException {
		//设置窗体大小
		this.setSize(img.getWidth(null), img.getHeight(null));
		//设定禁用窗体装饰
		this.setUndecorated(true);
		//初始化用于移动窗体的原点
		origin = new Point();
		//调用AWTUtilities的setWindowShape方法设定本窗体为制定的Shape形状
	    AWTUtilities.setWindowShape(this,getImageShape(img));
	    //设定窗体可见度
	    AWTUtilities.setWindowOpacity(this, (float) 0.5);//无透明    
	    this.setLocationRelativeTo(null);
	    
	    //设置移动窗体的方法
	    addMouseListener(
	    	new MouseAdapter(){
	    		public void mousePressed(MouseEvent e){
	    			origin.x = e.getX();
	                origin.y = e.getY();
	            }
	            //设置关闭程序所用，关闭按钮设置
	            public void mouseClicked(MouseEvent e) {
	            	//TODO
	            }
	            public void mouseReleased(MouseEvent e) {
	            	super.mouseReleased(e);
	            }
	            @Override
	            public void mouseEntered(MouseEvent e) {
	            	repaint();
	            }
	         });

	        addMouseMotionListener(
	            new MouseMotionAdapter(){
	              public void mouseDragged(MouseEvent e){
	                Point p =    getLocation();
	                setLocation(p.x + e.getX() - origin.x, p.y + e.getY() - origin.y );
	              }          
	            }
	        );
	}
	
	//将Image图像转换为Shape图形
	public Shape getImageShape(Image img) {
		ArrayList<Integer> x=new ArrayList<Integer>();
	    ArrayList<Integer> y=new ArrayList<Integer>();    
	    int width=img.getWidth(null);//图像宽度
	    int height=img.getHeight(null);//图像高度

	    //筛选像素
	    //首先获取图像所有的像素信息
	    PixelGrabber pgr = new PixelGrabber(img, 0, 0, -1, -1, true);
	    try {
	    	pgr.grabPixels();
	    } catch (InterruptedException ex) {
	    	ex.getStackTrace();
	    }
	    int pixels[] = (int[]) pgr.getPixels();
	    
	    //循环像素
	    for (int i = 0; i<pixels.length; i++) {
	    	//筛选，将不透明的像素的坐标加入到坐标ArrayList x和y中
	    	int alpha = getAlpha(pixels[i]);
	    	if (alpha == 0){
	    		continue;        
	    	}else{
	    		x.add(i%width>0 ? i%width-1:0);
	    		y.add(i%width==0 ? (i==0 ? 0:i/width-1):i/width);
	    	}      
	    }
	    
	    //建立图像矩阵并初始化(0为透明,1为不透明)
	    int[][] matrix=new int[height][width];    
	    for(int i=0;i<height;i++){
	    	for(int j=0;j<width;j++){
	    		matrix[i][j]=0;
	    	}
	    }
	    
	    //导入坐标ArrayList中的不透明坐标信息
	    for(int c=0;c<x.size();c++){
	    	matrix[y.get(c)][x.get(c)]=1;
	    }
	    
	    /* 由于Area类所表示区域可以进行合并，我们逐一水平"扫描"图像矩阵的每一行，
	     * 将不透明的像素生成为Rectangle，再将每一行的Rectangle通过Area类的rec
	     * 对象进行合并，最后形成一个完整的Shape图形
	     */
	    Area rec=new Area();
	    int temp=0;
	    
	    for(int i=0;i<height;i++){
	    	for(int j=0;j<width;j++){
	    		if(matrix[i][j]==1){
	    			if(temp==0)
	    				temp=j;  
	    			else if(j==width){
	    				if(temp==0){
	    					Rectangle rectemp=new Rectangle(j,i,1,1);
	    					rec.add(new Area(rectemp));
	    				}else{
	    					Rectangle rectemp=new Rectangle(temp,i,j-temp,1);
	    					rec.add(new Area(rectemp));
	    					temp=0;
	    				}
	    			}
	    		}else{
	    			if(temp!=0){
	    				Rectangle rectemp=new Rectangle(temp,i,j-temp,1);
	    				rec.add(new Area(rectemp));
	    				temp=0;
	    			}
	    		}
	    	}
	    	temp=0;
	    }
	    return rec;
	}
	
	private int getAlpha(int pixel) {
		//System.out.println("pixel:"+pixel);
		//System.out.println("alpha:"+((pixel >> 24) & 0xff));
	    return (pixel >> 24) & 0xff;
	}
	
	
	
}
