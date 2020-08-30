package saolei;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.Timer;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class SaoLei extends MouseAdapter implements ActionListener {

	JFrame frame = new JFrame();
	ImageIcon bannerIcon = new ImageIcon("resource\\img\\banner.png");
	ImageIcon guessIcon = new ImageIcon("resource\\img\\guess.png");
	ImageIcon bombIcon = new ImageIcon("resource\\img\\bomb.png");
	ImageIcon failIcon = new ImageIcon("resource\\img\\fail.png");
	ImageIcon winIcon = new ImageIcon("resource\\img\\win.png");
	ImageIcon win_flagIcon = new ImageIcon("resource\\img\\win_flag.png");
	JButton bannerBtn = new JButton(bannerIcon);
		
	// ���ݽṹ
	int ROW = 20; // ��
	int COL = 20; // ��
	int[][] data = new int[ROW][COL]; // ��������
	JButton[][] btns = new JButton[ROW][COL]; // ���а�ť
	int LEICOUNT = 15; // ������
	int FLAGCOUNT = LEICOUNT; // ��������
	int LEICODE = -1; // �״���
	int unopen = ROW * COL; // δ��
	int opened = 0; // �ѿ�
	int seconds = 0; // ʱ��
	int gameStatus = -1; // ��Ϸ״̬��0:��ͣ��1:��Ϸ�У�-1:��Ϸ����
	boolean bgmIsOpen = true; // �������֣�false:�����ţ�true:���ţ�Ĭ�ϣ�
	Player player; // ����������

	JLabel labelUnopen = new JLabel("δ����" + unopen);
	JLabel labelOpened = new JLabel("�ѿ���" + opened);
	JLabel labelLeiCount = new JLabel("������" + LEICOUNT);
	JLabel labelFlagCount = new JLabel("������" + FLAGCOUNT);
	JLabel labelSeconds = new JLabel("��ʱ��" + seconds + "s");

	Timer timer = new Timer(1000, this);

	public SaoLei() throws FileNotFoundException, JavaLayerException {
		frame.setTitle("ɨ����Ϸ   By������İ��߹"); // ���ñ���
		frame.setSize(600, 700); // ���ô�С
		frame.setResizable(false); // ���ò��ɵ�����С
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // ���ùرհ�ť
		frame.setLayout(new BorderLayout()); // ���ò���
		frame.setLocationRelativeTo(frame.getOwner());

		// ���ò˵�
		setMenu();
		
		// ����ͷ��
		setHeader();

		// ����
		addLei();

		// ������Ϸ��
		setButtons();
		new Thread() {
			public void run() {
				try {
					onBgm();
				} catch (FileNotFoundException | JavaLayerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
		
		frame.setVisible(true);
	}

	JMenu menuMain = new JMenu("��Ϸ�˵�");
	JMenuItem itemStart = new JMenuItem("��ʼ��Ϸ");
	JMenuItem itemPause = new JMenuItem("��ͣ��Ϸ");
	JMenuItem itemSetting = new JMenuItem("��Ϸ����");
	JMenuItem itemExit = new JMenuItem("�˳���Ϸ");
	JMenu menuAbout = new JMenu("������Ϸ");
	JMenuItem itemAbout = new JMenuItem("������Ϸ");
	JMenuItem itemLink = new JMenuItem("��ϵ����");
	
	private void setMenu() {
		JMenuBar menuBar = new JMenuBar();
		itemStart.addActionListener(this);
		itemPause.addActionListener(this);
		itemPause.setEnabled(false);
		itemSetting.addActionListener(this);
		itemExit.addActionListener(this);
		itemAbout.addActionListener(this);
		itemLink.addActionListener(this);
		menuMain.add(itemStart);
		menuMain.add(itemPause);
		menuMain.add(itemSetting);
		menuMain.add(itemExit);
		menuBar.add(menuMain);
		menuAbout.add(itemAbout);
		menuAbout.add(itemLink);
		menuBar.add(menuAbout);
		frame.setJMenuBar(menuBar);
	}

	private void addLei() {
		Random rand = new Random();
		for (int i = 0; i < LEICOUNT;) {
			int r = rand.nextInt(ROW);
			int c = rand.nextInt(COL);
			if (data[r][c] != LEICODE) {
				data[r][c] = LEICODE;
				i++;
			}
		}

		// �����ܱߵ��׵�����
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				if (data[i][j] == LEICODE)
					continue;
				int tempCount = 0;
				if (i > 0 && j > 0 && data[i - 1][j - 1] == LEICODE)
					tempCount++;
				if (i > 0 && data[i - 1][j] == LEICODE)
					tempCount++;
				if (i > 0 && j < 19 && data[i - 1][j + 1] == LEICODE)
					tempCount++;
				if (j > 0 && data[i][j - 1] == LEICODE)
					tempCount++;
				if (j < 19 && data[i][j + 1] == LEICODE)
					tempCount++;
				if (i < 19 && j > 0 && data[i + 1][j - 1] == LEICODE)
					tempCount++;
				if (i < 19 && data[i + 1][j] == LEICODE)
					tempCount++;
				if (i < 19 && j < 19 && data[i + 1][j + 1] == LEICODE)
					tempCount++;
				data[i][j] = tempCount;
			}
		}
	}

	private void setButtons() {
		Container con = new Container();
		con.setLayout(new GridLayout(ROW, COL));
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				JButton btn = new JButton(guessIcon);
				btn.setOpaque(true);
				btn.setBackground(new Color(244, 183, 113));
				btn.addActionListener(this);
				btn.addMouseListener(this);
				btn.setMargin(new Insets(0, 0, 0, 0));
				Font f = new Font("����",Font.BOLD,15);
				btn.setFont(f);
				btn.setFocusable(false);
				con.add(btn);
				btns[i][j] = btn;
			}
		}
		frame.add(con, BorderLayout.CENTER);
	}

	private void setHeader() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints c1 = new GridBagConstraints(0, 0, 5, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		panel.add(bannerBtn, c1);

		labelUnopen.setOpaque(true);
		labelUnopen.setBackground(Color.white);
		labelUnopen.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		labelOpened.setOpaque(true);
		labelOpened.setBackground(Color.white);
		labelOpened.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		labelLeiCount.setOpaque(true);
		labelLeiCount.setBackground(Color.white);
		labelLeiCount.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		
		labelFlagCount.setOpaque(true);
		labelFlagCount.setBackground(Color.white);
		labelFlagCount.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

		labelSeconds.setOpaque(true);
		labelSeconds.setBackground(Color.white);
		labelSeconds.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		
		bannerBtn.setOpaque(true);
		bannerBtn.setBackground(Color.white);
		bannerBtn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		bannerBtn.setFocusable(false);

		GridBagConstraints c2 = new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		panel.add(labelUnopen, c2);
		GridBagConstraints c3 = new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		panel.add(labelOpened, c3);
		GridBagConstraints c4 = new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		panel.add(labelLeiCount, c4);
		GridBagConstraints c5 = new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		panel.add(labelFlagCount, c5);
		GridBagConstraints c6 = new GridBagConstraints(4, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		panel.add(labelSeconds, c6);

		frame.add(panel, BorderLayout.NORTH);
	}

	public static void main(String[] args) throws FileNotFoundException, JavaLayerException {
		new SaoLei();
	}
	
	// �ر�����
	protected void offBgm() {
		player.close();
	}

	// ��������
	protected void onBgm() throws JavaLayerException, FileNotFoundException {
		Random rand = new Random();
		int id = rand.nextInt(3) + 1;
		File file = new File("resource\\audio\\main_" + id + ".mp3");
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream stream = new BufferedInputStream(fis);
		player = new Player(stream);
		player.play();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() instanceof Timer) {
			seconds++;
			labelSeconds.setText("��ʱ��" + seconds + "s");
			timer.start();
			return;
		}
		
		String cmd = e.getActionCommand();
		if(cmd == "��ʼ��Ϸ" && (gameStatus == 0 || gameStatus == -1)) {
			play("menu");
			gameStatus = 1;
			itemStart.setText("���¿�ʼ");
			itemPause.setEnabled(true);
			restart();
			return;
		} else if (cmd == "��ͣ��Ϸ" && gameStatus == 1) {
			play("menu");
			gameStatus = 0;
			timer.stop();
			itemPause.setEnabled(false);
			itemStart.setText("������Ϸ");
			return;
		} else if (cmd == "������Ϸ" && gameStatus == 0) {
			play("menu");
			gameStatus = 1;
			timer.start();
			itemStart.setText("���¿�ʼ");
			itemPause.setEnabled(true);
			return;
		} else if (cmd == "���¿�ʼ") {
			play("menu");
			if(gameStatus == 1) {
				play("tips");
				int option = JOptionPane.showConfirmDialog(null,"��Ϸ�ѿ�ʼ���Ƿ����¿�ʼ��", "�Ƿ����¿�ʼ��   By������İ��߹", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE, null);
				if (option == JOptionPane.YES_NO_OPTION) {
					gameStatus = 1;
					itemPause.setEnabled(true);
					restart();
				}
			} else {
				gameStatus = 1;
				itemPause.setEnabled(true);
				restart();
			}
			return;
		} else if (cmd == "��Ϸ����") {
			play("menu");
			JDialog jd = new JDialog(frame,"ɨ�� -�� ��Ϸ����   By������İ��߹");
			jd.setSize(320,190);
			jd.setResizable(false);
			jd.setLocationRelativeTo(jd.getOwner());
			jd.setLayout(new BorderLayout());
			jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); 
			
			JPanel panelLevel = new JPanel();
			JPanel panelSetting = new JPanel();
			
			// �Ѷ�ѡ��
			JComboBox level = new JComboBox();			
			level.addItem("�ܼ򵥣�15���ף�"); // 0
			level.addItem("�򵥵ģ�30���ף�"); // 1
			level.addItem("�еȵģ�50���ף�"); // 2
			level.addItem("���ѵģ�80���ף�"); // 3
			level.addItem("�����ѣ�110���ף�"); // 4
			level.addItem("���ģ�150���ף�"); // 5
			level.addItem("�Զ��壨������15-150��"); // 6
			
			JLabel labelLevel = new JLabel();
			labelLevel.setText("��Ϸ�Ѷȣ�");
			labelLevel.setFont(new Font("����",Font.BOLD,20));
			
			panelLevel.add(labelLevel);
			panelLevel.add(level);
			JLabel leiCount = new JLabel();
			JLabel tips = new JLabel();
			tips.setFont(new Font("����",Font.BOLD,20));
			leiCount.setText("�Զ���������");
			leiCount.setFont(new Font("����",Font.BOLD,20));
			JTextField count = new JTextField(14);
			count.setFont(new Font("����",Font.BOLD,20));
			count.setHorizontalAlignment(JTextField.CENTER);
			count.setEnabled(false);
			count.setText("��ѡ�Զ����Ѷ�");
			count.setToolTipText("��������������Χ15-150");
			
			// ��ȡ��������ȡ�Ѷ�
			switch(LEICOUNT) {
			case 15:
				level.setSelectedIndex(0);
				break;
			case 30:
				level.setSelectedIndex(1);
				break;
			case 50:
				level.setSelectedIndex(2);
				break;
			case 80:
				level.setSelectedIndex(3);
				break;
			case 110:
				level.setSelectedIndex(4);
				break;
			case 150:
				level.setSelectedIndex(5);
				break;
			default:
				count.setText(LEICOUNT + "");
				count.setEnabled(true);
				level.setSelectedIndex(6);
			}
			
			level.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					play("level");
				}
			});
			
			level.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if (e.getItem().toString() == "�Զ��壨������15-150��") {						
						if(!count.isEnabled()) {
							count.setText("");
							count.setEnabled(true);
						} else {
							count.setText("��ѡ�Զ����Ѷ�");
							count.setEnabled(false);
						}
					}
				}
			});
			
			JRadioButton rOn = new JRadioButton("������Ϸ����",true);//ֻ������������
			JRadioButton rOff = new JRadioButton("�ر���Ϸ����");
			rOn.setFont(new Font("����",Font.BOLD,20));
			rOff.setFont(new Font("����",Font.BOLD,20));
			
			ButtonGroup group = new ButtonGroup();   //����һ����ť��
			group.add(rOn);
			group.add(rOff);
			
			rOn.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					play("bgmswh");
				}
			});
			
			rOff.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					play("bgmswh");
				}
			});
			
			// ��ȡbgm��״̬
			rOn.setSelected(bgmIsOpen);
			rOff.setSelected(!bgmIsOpen);
			
			panelSetting.add(leiCount);
			panelSetting.add(count);
			panelSetting.add(rOn);
			panelSetting.add(rOff);
			panelSetting.setLayout(new FlowLayout());
			
			JButton save = new JButton();
			save.setText("�������ò��ر�");
			save.setFont(new Font("����",Font.BOLD,20));
			save.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					play("ok");
					// �ж��Ѷȼ���ĸı�
					switch (level.getSelectedIndex()) {
					case 0:
						LEICOUNT = 15;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("��ʼ��Ϸ");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 1:
						LEICOUNT = 30;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("��ʼ��Ϸ");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 2:
						LEICOUNT = 50;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("��ʼ��Ϸ");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 3:
						LEICOUNT = 80;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("��ʼ��Ϸ");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 4:
						LEICOUNT = 110;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("��ʼ��Ϸ");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 5:
						LEICOUNT = 150;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("��ʼ��Ϸ");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 6:
						String s = count.getText();
						Pattern pattern = Pattern.compile("^[\\d]*$");
						if(s.isEmpty()) {
							play("err");
							JOptionPane.showMessageDialog(jd, "��������Ϊ�գ�����������15-150��", "���棡   By������İ��߹", JOptionPane.PLAIN_MESSAGE);
						} else if (!pattern.matcher(count.getText().trim()).matches()) {
							play("err");
							JOptionPane.showMessageDialog(jd, "�����������֣�����������15-150��", "���棡   By������İ��߹", JOptionPane.PLAIN_MESSAGE);
							count.setText("");
						} else if (Integer.parseInt(s) >= 15 && Integer.parseInt(s) <= 150) { // �ڷ�Χ��
							LEICOUNT = Integer.parseInt(s);
							gameStatus = -1;
							timer.stop();
							start();
							itemStart.setText("��ʼ��Ϸ");
							itemPause.setEnabled(false);
							jd.setVisible(false);
						} else if (Integer.parseInt(s) < 15 || Integer.parseInt(s) > 150){
							play("err");
							JOptionPane.showMessageDialog(jd, "�������ڷ�Χ�ڣ�����������15-150��", "���棡   By������İ��߹", JOptionPane.PLAIN_MESSAGE);
						}
						break;
					}
					
					if(rOn.isSelected()) { // ����������ִ�
						if(!bgmIsOpen) {
							bgmIsOpen = true;
							new Thread() {
								public void run() {
									try {
										onBgm();
									} catch (FileNotFoundException | JavaLayerException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}.start();
						}
					} else { // ����������ֹر�
						if(bgmIsOpen) {
							bgmIsOpen = false;
							offBgm();
						}
					}
				}
			});
			
			jd.add(panelLevel,BorderLayout.NORTH);
			
			jd.add(panelSetting,BorderLayout.CENTER);
			
			jd.add(save,BorderLayout.SOUTH);
			
			jd.pack();
			
			jd.setVisible(true);
			return;
		} else if (cmd == "�˳���Ϸ") {
			play("menu");
			if(gameStatus != -1) {
				play("tips");
				int option = JOptionPane.showConfirmDialog(null,"��Ϸ��û�������Ƿ��˳���Ϸ��", "�Ƿ��˳���Ϸ��   By������İ��߹", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE, null);
				if(option == JOptionPane.YES_NO_OPTION) {
					System.exit(0);
				}
			} else {
				System.exit(0);
			}
		} else if (cmd == "������Ϸ") {
			play("menu");
			JDialog jd = new JDialog(frame,"ɨ�� -�� ������Ϸ   By������İ��߹");
			jd.setSize(300,400);
			jd.setResizable(false);
			jd.setLocationRelativeTo(jd.getOwner());
			jd.setLayout(new BorderLayout());
			jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			JLabel title = new JLabel("������Ϸ",JLabel.CENTER);
			title.setFont(new Font("����",Font.BOLD,50));
			title.setText("<html><span color=red>������Ϸ</span></html>");
			jd.add(title,BorderLayout.NORTH);
			JLabel content = new JLabel("<html><body>��Ϸ���֣�İ��ɨ��<br>��Ϸ�汾��v 1.0.3<br>��Ϸ���ߣ�����İ��߹<br>����QQ�ţ�2289864265<br>��Ϸ���ܣ���ϷĿ��������̵�ʱ���ڸ��ݵ�����ӳ��ֵ������ҳ����з��׸��ӣ�ͬʱ������ף��ȵ�һ���׼�ȫ�̽��䡣</body></html>",JLabel.CENTER);
			content.setFont(new Font("����",Font.BOLD,20));
			jd.add(content,BorderLayout.CENTER);
			JButton btn = new JButton("ȷ��");
			btn.setFont(new Font("����",Font.BOLD,20));
			btn.setText("<html><span color=blue>ȷ     ��</span></html>");
			btn.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					play("ok");
					jd.setVisible(false);
				}
			});
			btn.setFocusable(false);
			jd.add(btn,BorderLayout.SOUTH);
			jd.setVisible(true);
			return;
		} else if (cmd == "��ϵ����") {
			play("menu");
			java.net.URI uri = null;
			try {
				uri = new java.net.URI("http://wpa.qq.com/msgrd?v=3&uin=2289864265&site=qq&menu=yes");
			} catch (URISyntaxException e1) {
				e1.printStackTrace();
			}
			try {
				java.awt.Desktop.getDesktop().browse(uri);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			return;
		}

		JButton btn = (JButton) e.getSource();
		if (gameStatus == 1) {
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COL; j++) {
					if (btn.equals(btns[i][j])) {
						if (data[i][j] == LEICODE) {
							lose();
						} else {
							play("click");
							if (btns[i][j].getText() == "<html><span color='#68ee68'>?</span></html>") {
								FLAGCOUNT++;
								labelFlagCount.setText("������" + FLAGCOUNT);
							}
							openCell(i, j);
							checkWin();
						}
						return;
					}
				}
			}
		} else if (gameStatus == -1) {
			play("tips");
			JOptionPane.showMessageDialog(frame, "��Ϸ��δ��ʼ���������Ϸ�����Ϸ�˵���-��[��ʼ��Ϸ]����ʼɨ����Ϸ��", "��ʾ��   By������İ��߹", JOptionPane.PLAIN_MESSAGE);
		} else if (gameStatus == 0) {
			play("tips");
			JOptionPane.showMessageDialog(frame, "��Ϸ������ͣ�У��������Ͻǡ���Ϸ�˵���-��[������Ϸ]�������������Ϸ��", "��ʾ��   By������İ��߹",
					JOptionPane.PLAIN_MESSAGE);
		}
	}

	protected void start() {
		FLAGCOUNT = LEICOUNT;
		// �ָ����ݰ�ť
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				data[i][j] = 0;
				btns[i][j].setBackground(new Color(244, 183, 113));
				btns[i][j].setEnabled(true);
				btns[i][j].setText("");
				btns[i][j].setIcon(guessIcon);
			}
		}
		// �ָ�״̬��
		unopen = ROW * COL;
		opened = 0;
		seconds = 0;
		labelUnopen.setText("δ����" + unopen);
		labelOpened.setText("�ѿ���" + opened);
		labelLeiCount.setText("������" + LEICOUNT);
		labelFlagCount.setText("������" + FLAGCOUNT);
		labelSeconds.setText("��ʱ��" + seconds + "s");
		addLei();
	}

	/*
	 * 1.�������� 2.����ť�ظ�״̬ 3.ʱ����������
	 */
	private void restart() {
		FLAGCOUNT = LEICOUNT;
		// �ָ����ݰ�ť
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				data[i][j] = 0;
				btns[i][j].setBackground(new Color(244, 183, 113));
				btns[i][j].setEnabled(true);
				btns[i][j].setText("");
				btns[i][j].setIcon(guessIcon);
			}
		}
		// �ָ�״̬��
		unopen = ROW * COL;
		opened = 0;
		seconds = 0;
		labelUnopen.setText("δ����" + unopen);
		labelOpened.setText("�ѿ���" + opened);
		labelLeiCount.setText("������" + LEICOUNT);
		labelFlagCount.setText("������" + FLAGCOUNT);
		labelSeconds.setText("��ʱ��" + seconds + "s");
		// ʱ����������
		timer.restart();
		addLei();
	}

	private void checkWin() {
		int count = 0;
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				if (btns[i][j].isEnabled())
					count++;
			}
		}
		if (count == LEICOUNT) {
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COL; j++) {
					if (btns[i][j].isEnabled()) {
						btns[i][j].setIcon(bombIcon);
						btns[i][j].setEnabled(false);
					}
				}
			}
			gameStatus = -1;
			itemPause.setEnabled(false);
			itemStart.setText("��ʼ��Ϸ");
			timer.stop();
			bannerBtn.setIcon(winIcon);
			play("victory");
			JOptionPane.showMessageDialog(frame, "��ϲ����Ӯ�ˣ���ʱ" + seconds + "s\n������Ͻǡ���Ϸ�˵���-��[��ʼ��Ϸ]����ʼ��һ����Ϸ��", "Ӯ��   By������İ��߹", JOptionPane.PLAIN_MESSAGE);
		}
	}

	private void lose() {
		gameStatus = -1;
		itemPause.setEnabled(false);
		itemStart.setText("��ʼ��Ϸ");
		bannerBtn.setIcon(failIcon); // banner ����Ϊ����
		timer.stop();
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				if (btns[i][j].isEnabled()) {
					if (data[i][j] == LEICODE) {
						btns[i][j].setText(null);
						btns[i][j].setEnabled(false);
						if(btns[i][j].getIcon() == win_flagIcon) {
							btns[i][j].setBackground(Color.pink);
							btns[i][j].setIcon(bombIcon);
							btns[i][j].setDisabledIcon(bombIcon);
						} else {
							btns[i][j].setIcon(bombIcon);
							btns[i][j].setDisabledIcon(bombIcon);
							btns[i][j].setBackground(Color.red);
						}
					} else {
						btns[i][j].setIcon(null);
						btns[i][j].setEnabled(false);
						btns[i][j].setOpaque(true);
						btns[i][j].setText(data[i][j] + "");
						Font f = new Font("����",Font.BOLD,15);
						btns[i][j].setFont(f);
					}
				}
			}
		}
		play("bomb");
		play("defeat");
		JOptionPane.showMessageDialog(frame, "��ϧ�㱩���ˣ���ʱ" + seconds + "s\n������Ͻǡ���Ϸ�˵���-��[��ʼ��Ϸ]����ʼ��һ����Ϸ��", "�㱩���ˣ�   By������İ��߹",
				JOptionPane.PLAIN_MESSAGE);
	}

	private void openCell(int i, int j) {

		JButton btn = btns[i][j];
		
		if (!btn.isEnabled())
			return; // ������ˣ��ͷ���
		if (btn.getIcon() == win_flagIcon)
			return; // ���CellΪ�����򷵻�

		btn.setIcon(null);
		btn.setEnabled(false);
		btn.setOpaque(true);
		btn.setBackground(new Color(180,238,180));
		btn.setText(data[i][j] + "");
		Font f = new Font("����",Font.BOLD,15);
		btn.setFont(f);

		addOpenCount();

		if (data[i][j] == 0) {
			if (i > 0 && j > 0 && data[i - 1][j - 1] != -1) openCell(i - 1, j - 1); // ����
			if (i > 0 && data[i - 1][j] != -1) openCell(i - 1, j); // ��
			if (i > 0 && j < 19 && data[i - 1][j + 1] != -1) openCell(i - 1, j + 1); // ����
			if (j > 0 && data[i][j - 1] != -1) openCell(i, j - 1); // ��
			if (j < 19 && data[i][j + 1] != -1) openCell(i, j + 1); // ��
			if (i < 19 && j > 0 && data[i + 1][j - 1] != -1) openCell(i + 1, j - 1); // ����
			if (i < 19 && data[i + 1][j] != -1) openCell(i + 1, j); // ��
			if (i < 19 && j < 19 && data[i + 1][j + 1] != -1) openCell(i + 1, j + 1); // ����
		}
	}

	private void addOpenCount() {
		opened++;
		unopen--;
		labelUnopen.setText("δ����" + unopen);
		labelOpened.setText("�ѿ���" + opened);
	}
	
	// ��������
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == e.BUTTON3 && gameStatus == 1) { // �������Ҽ�    ��    ��Ϸ������
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COL; j++) {
					if(e.getSource() == btns[i][j]) {
						if(btns[i][j].getIcon() == guessIcon) { // ��Cell����Ϊ����
							play("flag");
							if(FLAGCOUNT > 0) {								
								btns[i][j].setIcon(win_flagIcon);
								btns[i][j].setBackground(Color.orange);
								FLAGCOUNT--;
							} else {
								play("tips");
								JOptionPane.showMessageDialog(frame, "�Ѿ�û�������ˣ�", "��ʾ��   By������İ��߹", JOptionPane.PLAIN_MESSAGE);
							}
						} else if(btns[i][j].getIcon() == win_flagIcon) { // ��Cell����Ϊ��
							play("mark");
							btns[i][j].setIcon(null);
							Font f=new Font("��Ϊ����",Font.BOLD,20);
							btns[i][j].setFont(f);
							btns[i][j].setText("<html><span color='#68ee68'>?</span></html>");
							btns[i][j].setBackground(Color.blue);
						} else if(btns[i][j].getText() == "<html><span color='#68ee68'>?</span></html>") { // ��Cell��ԭ
							play("re");
							btns[i][j].setFont(null);
							btns[i][j].setText("");
							btns[i][j].setIcon(guessIcon);
							btns[i][j].setBackground(new Color(244, 183, 113));
							FLAGCOUNT++;
						}
						labelFlagCount.setText("������" + FLAGCOUNT);
					}
				}
			}
		} else if (e.getButton() == e.BUTTON3 && gameStatus == 0) { // �������Ҽ�     ��     ��Ϸ��ͣ��
			play("tips");
			JOptionPane.showMessageDialog(frame, "��Ϸ������ͣ�У��������Ͻǡ���Ϸ�˵���-��[������Ϸ]�������������Ϸ��", "��ʾ��   By������İ��߹", JOptionPane.PLAIN_MESSAGE);
		} else if (e.getButton() == e.BUTTON3 && gameStatus == -1) { // �������Ҽ�     ��     ��Ϸδ��ʼ
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COL; j++) {
					if(e.getSource() == btns[i][j] && (btns[i][j].getIcon() == guessIcon || btns[i][j].getIcon() == win_flagIcon || btns[i][j].getText() == "<html><span color='#68ee68'>?</span></html>")) {
						play("tips");
						JOptionPane.showMessageDialog(frame, "��Ϸ��δ��ʼ���������Ͻǡ���Ϸ�˵���-��[��ʼ��Ϸ]����ʼɨ����Ϸ��", "��ʾ��   By������İ��߹", JOptionPane.PLAIN_MESSAGE);
					}
				}
			}
		}
	}
	
	private void play(String name) {
		if(bgmIsOpen) {
			new Thread() {
				public void run() {
					File file = new File("resource\\audio\\" + name + ".mp3");
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(file);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					BufferedInputStream stream = new BufferedInputStream(fis);
					Player p = null;
					try {
						p = new Player(stream);
					} catch (JavaLayerException e) {
						e.printStackTrace();
					}
					try {
						p.play();
					} catch (JavaLayerException e) {
						e.printStackTrace();
					}
				}
			}.start();
		}
	}
}