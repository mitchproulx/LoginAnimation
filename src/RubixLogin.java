
import java.awt.Frame;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.*;

public class RubixLogin implements GLEventListener, KeyListener {
	public static final boolean TRACE = false;

	public static final String WINDOW_TITLE = "Rubix Cube Timer";
	public static final int INITIAL_WIDTH = 640;
	public static final int INITIAL_HEIGHT = 640;

	public static boolean rotateOn = false;

	public static void main(String[] args) {
		final Frame frame = new Frame(WINDOW_TITLE);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		final GLProfile profile = GLProfile.get(GLProfile.GL2);
		final GLCapabilities capabilities = new GLCapabilities(profile);
		final GLCanvas canvas = new GLCanvas(capabilities);
		try {
			Object self = self().getConstructor().newInstance();
			self.getClass().getMethod("setup", new Class[] { GLCanvas.class }).invoke(self, canvas);
			canvas.addGLEventListener((GLEventListener)self);
			canvas.addKeyListener((KeyListener)self);
		} catch (Exception e) {
			assert(false);
		}
		canvas.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);

		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);

		System.out.println("\nEnd of processing.");
	}

	private static Class<?> self() {
		// This gives us the containing class of a static method 
		return new Object() { }.getClass().getEnclosingClass();
	}

	private float angle = 0.0f;
	private float ar;
	private int projection = 1;
	private int cameraAngle = 0;
	private boolean viewChanged = true;

	public void setup(final GLCanvas canvas) {
		if (TRACE)
			System.out.println("-> executing setup()");

		new Timer().scheduleAtFixedRate(new TimerTask() {
			public void run() {
				angle += 0.5f;
				canvas.repaint();
			}
		}, 1000, 1000/60);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// Called when the canvas is (re-)created - use it for initial GL setup
		if (TRACE)
			System.out.println("-> executing init()");

		final GL2 gl = drawable.getGL().getGL2();
		gl.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glDepthFunc(GL2.GL_LEQUAL);

		//gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE); do not front back facing
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// Draws the display
		if (TRACE)
			System.out.println("-> executing display()");

		final GL2 gl = drawable.getGL().getGL2();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();

		/*
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if (0 == projection) {
			gl.glOrthof(ar < 1 ? -1.0f : -ar, ar < 1 ? 1.0f : ar, ar > 1 ? -1.0f : -1/ar, ar > 1 ? 1.0f : 1/ar, 1.0f, 4.0f);
		} else {
			gl.glFrustumf(ar < 1 ? -1.0f : -ar, ar < 1 ? 1.0f : ar, ar > 1 ? -1.0f : -1/ar, ar > 1 ? 1.0f : 1/ar, 1.0f, 4.0f);
		}
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		if (cameraAngle == 0 && projection == 1) {
			gl.glTranslatef(0.0f, -0.5f, -2f - 0.3f * cameraAngle);
			gl.glRotatef(15.0f * cameraAngle, 0.0f, 0.0f, 1.0f);
			gl.glRotatef(60.0f * cameraAngle, 1.0f, 0.0f, 0.0f);
			gl.glScalef(0.5f, 0.5f, 0.5f);
		}
		 */

		// SET UP THE CAMERA
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-1,1,-1,1,-1,1);
		//gl.glOrthof(-1,1,-1,1,-1,1);
		//gl.glOrthof(-0.75f,0.75f,-0.75f,0.75f,-0.75f,0.75f);

		if (viewChanged) {
			if (projection == 1) {
				/*
			gl.glTranslatef(0.0f, -0.5f, -2f - 0.3f * cameraAngle);
			gl.glRotatef(15.0f * cameraAngle, 0.0f, 0.0f, 1.0f);
			gl.glRotatef(60.0f * cameraAngle, 1.0f, 0.0f, 0.0f);
			gl.glScalef(0.5f, 0.5f, 0.5f);
				 */
				gl.glScalef(0.75f, 0.75f, 0.75f);
				gl.glRotatef(10.0f*cameraAngle, 1.0f, 0.0f, 0.0f);
			}
			viewChanged = false;
		}


		// DRAW THE CUBE
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		drawLayers(gl);
	}

	private void draw(GL2 gl) {
		gl.glBegin(GL2.GL_TRIANGLES);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glVertex2d(0, Math.sqrt(3) / 16);
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		gl.glVertex2d(-Math.sqrt(3) / 16, -Math.sqrt(3) / 16);
		gl.glVertex2d(Math.sqrt(3) / 16, -Math.sqrt(3) / 16);
		gl.glEnd();
	}

	private void drawFace(GL2 gl) {
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glEnd();
	}

	private void drawCube(GL2 gl) {

		// side 1
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(1,0,0);
		gl.glVertex3f(-0.1f,-0.1f,-0.1f);
		gl.glVertex3f(-0.1f,0.1f,-0.1f);
		gl.glVertex3f(0.1f,0.1f,-0.1f);
		gl.glVertex3f(0.1f,-0.1f,-0.1f);
		gl.glEnd();

		// side 2		
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(0,1,0);
		gl.glVertex3f(-0.1f,-0.1f,0.1f);
		gl.glVertex3f(-0.1f,0.1f,0.1f);
		gl.glVertex3f(0.1f,0.1f,0.1f);
		gl.glVertex3f(0.1f,-0.1f,0.1f);
		gl.glEnd();

		// side 3
		gl.glPushMatrix();
		gl.glRotatef(90,0,1,0);
		gl.glTranslatef(0, 0, -0.1f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(0,0,1);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glEnd();
		gl.glPopMatrix();

		// side 4
		gl.glPushMatrix();
		gl.glRotatef(90,0,1,0);
		gl.glTranslatef(0, 0, 0.1f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(0,1,1);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glEnd();
		gl.glPopMatrix();

		// side 5
		gl.glPushMatrix();
		gl.glRotatef(90,0,1,0);
		gl.glRotatef(90,1,0,0);
		gl.glTranslatef(0, 0, 0.1f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(1,0,1);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glEnd();
		gl.glPopMatrix();

		// side 6
		gl.glPushMatrix();
		gl.glRotatef(90,0,1,0);
		gl.glRotatef(90,1,0,0);
		gl.glTranslatef(0, 0, -0.1f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3f(1,0.271f,0f);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glEnd();
		gl.glPopMatrix();
		
		drawOutline(gl);
	}

	private void drawOutline(GL2 gl) {
		// side 1 - black border
		gl.glLineWidth(5);
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0,0,0);
		gl.glVertex3f(-0.1f,-0.1f,-0.1f);
		gl.glVertex3f(-0.1f,0.1f,-0.1f);
		gl.glVertex3f(-0.1f,0.1f,-0.1f);
		gl.glVertex3f(0.1f,0.1f,-0.1f);
		gl.glVertex3f(0.1f,0.1f,-0.1f);
		gl.glVertex3f(0.1f,-0.1f,-0.1f);		
		gl.glVertex3f(0.1f,-0.1f,-0.1f);
		gl.glVertex3f(-0.1f,-0.1f,-0.1f);
		gl.glEnd();

		// side 2 - black border
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0,0,0);
		gl.glVertex3f(-0.1f,-0.1f,0.1f);
		gl.glVertex3f(-0.1f,0.1f,0.1f);
		gl.glVertex3f(-0.1f,0.1f,0.1f);
		gl.glVertex3f(0.1f,0.1f,0.1f);
		gl.glVertex3f(0.1f,0.1f,0.1f);
		gl.glVertex3f(0.1f,-0.1f,0.1f);		
		gl.glVertex3f(0.1f,-0.1f,0.1f);
		gl.glVertex3f(-0.1f,-0.1f,0.1f);
		gl.glEnd();

		// side 3 - black borders
		gl.glPushMatrix();
		gl.glRotatef(90,0,1,0);
		gl.glTranslatef(0, 0, -0.1f);
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0,0,0);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glEnd();
		gl.glPopMatrix();

		// side 4 - black borders
		gl.glPushMatrix();
		gl.glRotatef(90,0,1,0);
		gl.glTranslatef(0, 0, 0.1f);
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0,0,0);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glEnd();
		gl.glPopMatrix();

		// side 5 - black borders
		gl.glPushMatrix();
		gl.glRotatef(90,0,1,0);
		gl.glRotatef(90,1,0,0);
		gl.glTranslatef(0, 0, 0.1f);
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0,0,0);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glEnd();
		gl.glPopMatrix();

		// side 6 - black borders
		gl.glPushMatrix();
		gl.glRotatef(90,0,1,0);
		gl.glRotatef(90,1,0,0);
		gl.glTranslatef(0, 0, -0.1f);
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0,0,0);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(-0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glVertex3f(0.1f,-0.1f,0);
		gl.glVertex3f(-0.1f,-0.1f,0);
		gl.glEnd();
		gl.glPopMatrix();
	}

	private void drawRow(GL2 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(0,0,0);

		drawCube(gl);
		gl.glTranslatef(0.2f,0,0);
		drawCube(gl);
		gl.glTranslatef(-0.4f,0,0);
		drawCube(gl);
		gl.glPopMatrix();
	}
	
	private void drawLayer(GL2 gl) {
		gl.glPushMatrix();
		drawRow(gl);
		gl.glTranslatef(0,0.2f,0);
		drawRow(gl);
		gl.glTranslatef(0,-0.4f,0);
		drawRow(gl);
		gl.glPopMatrix();
	}
	
	private void drawLayers(GL2 gl) {
		gl.glPushMatrix();
		if (rotateOn) {
			gl.glRotatef(angle-15,0,0,1);
			gl.glRotatef(angle,0,1,0);
			gl.glRotatef(angle+50,1,0,0);
		}
		gl.glPushMatrix();
		gl.glRotatef(angle*3,0,0,1);
		drawLayer(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(-angle*2,0,0,1);
		gl.glTranslatef(0,0,0.21f);
		drawLayer(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(angle/2,0,0,1);
		gl.glTranslatef(0,0,-0.21f);
		drawLayer(gl);
		gl.glPopMatrix();
		
		gl.glPopMatrix();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// Called when the canvas is destroyed (reverse anything from init) 
		if (TRACE)
			System.out.println("-> executing dispose()");
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		// Called when the canvas has been resized
		// Note: glViewport(x, y, width, height) has already been called so don't bother if that's what you want
		if (TRACE)
			System.out.println("-> executing reshape(" + x + ", " + y + ", " + width + ", " + height + ")");

		final GL2 gl = drawable.getGL().getGL2();

		final float ar = (float)width / (height == 0 ? 1 : height);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(ar < 1 ? -1.0f : -ar, ar < 1 ? 1.0f : ar, ar > 1 ? -1.0f : -1/ar, ar > 1 ? 1.0f : 1/ar, -1.0f, 1.0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == ' ') {
			cameraAngle++;
			
			if (cameraAngle > 9) {
				cameraAngle = 0;
			}
			System.out.println("Camera Angle =" + cameraAngle*10 + "\n");
			viewChanged = true;
			((GLCanvas)e.getSource()).repaint();
		}else if (e.getKeyChar() == 'r') {
			
			if (rotateOn) {
				rotateOn = false;
				System.out.println("Rotate OFF");
			}else{
				rotateOn = true;
				System.out.println("Rotate ON");
			}
			
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}
