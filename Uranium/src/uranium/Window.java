package uranium;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

abstract class Window extends JFrame implements KeyListener {
	private static final long serialVersionUID = -5125368081992354692L;
	private ImagePanel panel;
	
	private double dist[][];
	private int mapp[][];
	
	public Window(double dist[][], int mapp[][]) {
		super("uranium - particle filters");
		
		panel = new ImagePanel();
		panel.addKeyListener(this);
		
		this.dist = dist;
		this.mapp = mapp;
		updateImageAndRepaint();
		
		add(panel);
		pack();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void updateImageAndRepaint() {
		panel.updateImage(dist, mapp);
		panel.repaint();
	}
	
	public abstract void move(int direction);
	
	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		switch (keyCode) {
		case KeyEvent.VK_UP:
			move(Main.U);
			break;
		case KeyEvent.VK_DOWN:
			move(Main.D);
			break;
		case KeyEvent.VK_LEFT:
			move(Main.L);
			break;
		case KeyEvent.VK_RIGHT:
			move(Main.R);
			break;
		default:
			return;
		}
		updateImageAndRepaint();
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyTyped(KeyEvent e) {}
	
}