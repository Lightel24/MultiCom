package main;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPopupMenu;

public class JDropDownButton extends JButton implements MouseListener{
	
  private JPopupMenu popup;
  private String name;

  public JDropDownButton(JPopupMenu popup){
	  super("JDropDownButton");
	  this.name = "JDropDownButton";
	  this.popup = popup;
	  this.addMouseListener(this);

  }
	
  public JDropDownButton(String str,JPopupMenu popup){
	  super(str);
	  this.name = str;
	  this.popup = popup;  
	  this.addMouseListener(this);

  }
  
  public JDropDownButton(String str){
	  super(str);
	  this.name = str;
	  this.popup = new JPopupMenu();
	  this.addMouseListener(this);
  }
  
  public JDropDownButton(){
	  super("JDropDownButton");
	  this.name = "JDropDownButton";
	  this.popup = new JPopupMenu();
	  this.addMouseListener(this);

  }
  
  public void setJPopupMenu(JPopupMenu popup) {
	  this.popup = popup;
  }
  
  
  @Override
  public void paintComponent(Graphics g){
	    g.drawString(this.name, this.getWidth() / 2 - (this.getWidth() / 2 /4), (this.getHeight() / 2) + 5);
  }
  		@Override
	    public void mousePressed(MouseEvent e) {
	      checkPopup(e);
	    }
  		
  		@Override
	    public void mouseClicked(MouseEvent e) {
	      checkPopup(e);
	    }
  		
  		@Override
	    public void mouseReleased(MouseEvent e) {
	      checkPopup(e);
	    }
  		
	    private void checkPopup(MouseEvent e) {
	        popup.show(JDropDownButton.this, e.getX(), e.getY());
	    }

	    @Override
		public void mouseEntered(MouseEvent arg0) {		
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			
		}
  }
