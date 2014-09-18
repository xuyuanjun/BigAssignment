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

//������ͼƬ����һ���Ĵ��壬����ͼƬ����������

public class ShapedJFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	private Point origin; //�����ƶ�����
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
		    initialize(frame, img);//�����ʼ��
		}catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	//�����ʼ��
	private void initialize(JFrame frame, Image img) throws IOException {
		//���ô����С
		this.setSize(img.getWidth(null), img.getHeight(null));
		//�趨���ô���װ��
		this.setUndecorated(true);
		//��ʼ�������ƶ������ԭ��
		origin = new Point();
		//����AWTUtilities��setWindowShape�����趨������Ϊ�ƶ���Shape��״
	    AWTUtilities.setWindowShape(this,getImageShape(img));
	    //�趨����ɼ���
	    AWTUtilities.setWindowOpacity(this, (float) 0.5);//��͸��    
	    this.setLocationRelativeTo(null);
	    
	    //�����ƶ�����ķ���
	    addMouseListener(
	    	new MouseAdapter(){
	    		public void mousePressed(MouseEvent e){
	    			origin.x = e.getX();
	                origin.y = e.getY();
	            }
	            //���ùرճ������ã��رհ�ť����
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
	
	//��Imageͼ��ת��ΪShapeͼ��
	public Shape getImageShape(Image img) {
		ArrayList<Integer> x=new ArrayList<Integer>();
	    ArrayList<Integer> y=new ArrayList<Integer>();    
	    int width=img.getWidth(null);//ͼ����
	    int height=img.getHeight(null);//ͼ��߶�

	    //ɸѡ����
	    //���Ȼ�ȡͼ�����е�������Ϣ
	    PixelGrabber pgr = new PixelGrabber(img, 0, 0, -1, -1, true);
	    try {
	    	pgr.grabPixels();
	    } catch (InterruptedException ex) {
	    	ex.getStackTrace();
	    }
	    int pixels[] = (int[]) pgr.getPixels();
	    
	    //ѭ������
	    for (int i = 0; i<pixels.length; i++) {
	    	//ɸѡ������͸�������ص�������뵽����ArrayList x��y��
	    	int alpha = getAlpha(pixels[i]);
	    	if (alpha == 0){
	    		continue;        
	    	}else{
	    		x.add(i%width>0 ? i%width-1:0);
	    		y.add(i%width==0 ? (i==0 ? 0:i/width-1):i/width);
	    	}      
	    }
	    
	    //����ͼ����󲢳�ʼ��(0Ϊ͸��,1Ϊ��͸��)
	    int[][] matrix=new int[height][width];    
	    for(int i=0;i<height;i++){
	    	for(int j=0;j<width;j++){
	    		matrix[i][j]=0;
	    	}
	    }
	    
	    //��������ArrayList�еĲ�͸��������Ϣ
	    for(int c=0;c<x.size();c++){
	    	matrix[y.get(c)][x.get(c)]=1;
	    }
	    
	    /* ����Area������ʾ������Խ��кϲ���������һˮƽ"ɨ��"ͼ������ÿһ�У�
	     * ����͸������������ΪRectangle���ٽ�ÿһ�е�Rectangleͨ��Area���rec
	     * ������кϲ�������γ�һ��������Shapeͼ��
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
