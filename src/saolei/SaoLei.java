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
		
	// 数据结构
	int ROW = 20; // 行
	int COL = 20; // 列
	int[][] data = new int[ROW][COL]; // 所有数据
	JButton[][] btns = new JButton[ROW][COL]; // 所有按钮
	int LEICOUNT = 15; // 雷数量
	int FLAGCOUNT = LEICOUNT; // 旗子数量
	int LEICODE = -1; // 雷代码
	int unopen = ROW * COL; // 未开
	int opened = 0; // 已开
	int seconds = 0; // 时间
	int gameStatus = -1; // 游戏状态：0:暂停；1:游戏中；-1:游戏结束
	boolean bgmIsOpen = true; // 背景音乐：false:不播放；true:播放（默认）
	Player player; // 播放器对象

	JLabel labelUnopen = new JLabel("未开：" + unopen);
	JLabel labelOpened = new JLabel("已开：" + opened);
	JLabel labelLeiCount = new JLabel("雷数：" + LEICOUNT);
	JLabel labelFlagCount = new JLabel("旗数：" + FLAGCOUNT);
	JLabel labelSeconds = new JLabel("用时：" + seconds + "s");

	Timer timer = new Timer(1000, this);

	public SaoLei() throws FileNotFoundException, JavaLayerException {
		frame.setTitle("扫雷游戏   By：我是陌宇吖"); // 设置标题
		frame.setSize(600, 700); // 设置大小
		frame.setResizable(false); // 设置不可调整大小
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置关闭按钮
		frame.setLayout(new BorderLayout()); // 设置布局
		frame.setLocationRelativeTo(frame.getOwner());

		// 设置菜单
		setMenu();
		
		// 设置头部
		setHeader();

		// 埋雷
		addLei();

		// 设置游戏区
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

	JMenu menuMain = new JMenu("游戏菜单");
	JMenuItem itemStart = new JMenuItem("开始游戏");
	JMenuItem itemPause = new JMenuItem("暂停游戏");
	JMenuItem itemSetting = new JMenuItem("游戏设置");
	JMenuItem itemExit = new JMenuItem("退出游戏");
	JMenu menuAbout = new JMenu("关于游戏");
	JMenuItem itemAbout = new JMenuItem("关于游戏");
	JMenuItem itemLink = new JMenuItem("联系作者");
	
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

		// 计算周边的雷的数量
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
				Font f = new Font("黑体",Font.BOLD,15);
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
	
	// 关闭音乐
	protected void offBgm() {
		player.close();
	}

	// 开启音乐
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
			labelSeconds.setText("用时：" + seconds + "s");
			timer.start();
			return;
		}
		
		String cmd = e.getActionCommand();
		if(cmd == "开始游戏" && (gameStatus == 0 || gameStatus == -1)) {
			play("menu");
			gameStatus = 1;
			itemStart.setText("重新开始");
			itemPause.setEnabled(true);
			restart();
			return;
		} else if (cmd == "暂停游戏" && gameStatus == 1) {
			play("menu");
			gameStatus = 0;
			timer.stop();
			itemPause.setEnabled(false);
			itemStart.setText("继续游戏");
			return;
		} else if (cmd == "继续游戏" && gameStatus == 0) {
			play("menu");
			gameStatus = 1;
			timer.start();
			itemStart.setText("重新开始");
			itemPause.setEnabled(true);
			return;
		} else if (cmd == "重新开始") {
			play("menu");
			if(gameStatus == 1) {
				play("tips");
				int option = JOptionPane.showConfirmDialog(null,"游戏已开始，是否重新开始？", "是否重新开始？   By：我是陌宇吖", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE, null);
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
		} else if (cmd == "游戏设置") {
			play("menu");
			JDialog jd = new JDialog(frame,"扫雷 -＞ 游戏设置   By：我是陌宇吖");
			jd.setSize(320,190);
			jd.setResizable(false);
			jd.setLocationRelativeTo(jd.getOwner());
			jd.setLayout(new BorderLayout());
			jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); 
			
			JPanel panelLevel = new JPanel();
			JPanel panelSetting = new JPanel();
			
			// 难度选择
			JComboBox level = new JComboBox();			
			level.addItem("很简单（15颗雷）"); // 0
			level.addItem("简单的（30颗雷）"); // 1
			level.addItem("中等的（50颗雷）"); // 2
			level.addItem("困难的（80颗雷）"); // 3
			level.addItem("较困难（110颗雷）"); // 4
			level.addItem("疯狂的（150颗雷）"); // 5
			level.addItem("自定义（雷数：15-150）"); // 6
			
			JLabel labelLevel = new JLabel();
			labelLevel.setText("游戏难度：");
			labelLevel.setFont(new Font("黑体",Font.BOLD,20));
			
			panelLevel.add(labelLevel);
			panelLevel.add(level);
			JLabel leiCount = new JLabel();
			JLabel tips = new JLabel();
			tips.setFont(new Font("黑体",Font.BOLD,20));
			leiCount.setText("自定义雷数：");
			leiCount.setFont(new Font("黑体",Font.BOLD,20));
			JTextField count = new JTextField(14);
			count.setFont(new Font("黑体",Font.BOLD,20));
			count.setHorizontalAlignment(JTextField.CENTER);
			count.setEnabled(false);
			count.setText("请选自定义难度");
			count.setToolTipText("请输入雷数，范围15-150");
			
			// 获取雷数来读取难度
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
					if (e.getItem().toString() == "自定义（雷数：15-150）") {						
						if(!count.isEnabled()) {
							count.setText("");
							count.setEnabled(true);
						} else {
							count.setText("请选自定义难度");
							count.setEnabled(false);
						}
					}
				}
			});
			
			JRadioButton rOn = new JRadioButton("开启游戏音乐",true);//只传了两个参数
			JRadioButton rOff = new JRadioButton("关闭游戏音乐");
			rOn.setFont(new Font("黑体",Font.BOLD,20));
			rOff.setFont(new Font("黑体",Font.BOLD,20));
			
			ButtonGroup group = new ButtonGroup();   //创建一个按钮组
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
			
			// 读取bgm打开状态
			rOn.setSelected(bgmIsOpen);
			rOff.setSelected(!bgmIsOpen);
			
			panelSetting.add(leiCount);
			panelSetting.add(count);
			panelSetting.add(rOn);
			panelSetting.add(rOff);
			panelSetting.setLayout(new FlowLayout());
			
			JButton save = new JButton();
			save.setText("保存设置并关闭");
			save.setFont(new Font("黑体",Font.BOLD,20));
			save.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					play("ok");
					// 判断难度级别的改变
					switch (level.getSelectedIndex()) {
					case 0:
						LEICOUNT = 15;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("开始游戏");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 1:
						LEICOUNT = 30;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("开始游戏");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 2:
						LEICOUNT = 50;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("开始游戏");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 3:
						LEICOUNT = 80;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("开始游戏");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 4:
						LEICOUNT = 110;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("开始游戏");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 5:
						LEICOUNT = 150;
						gameStatus = -1;
						timer.stop();
						start();
						itemStart.setText("开始游戏");
						itemPause.setEnabled(false);
						jd.setVisible(false);
						break;
					case 6:
						String s = count.getText();
						Pattern pattern = Pattern.compile("^[\\d]*$");
						if(s.isEmpty()) {
							play("err");
							JOptionPane.showMessageDialog(jd, "雷数不能为空，请输入数字15-150。", "警告！   By：我是陌宇吖", JOptionPane.PLAIN_MESSAGE);
						} else if (!pattern.matcher(count.getText().trim()).matches()) {
							play("err");
							JOptionPane.showMessageDialog(jd, "必须填入数字，请输入数字15-150。", "警告！   By：我是陌宇吖", JOptionPane.PLAIN_MESSAGE);
							count.setText("");
						} else if (Integer.parseInt(s) >= 15 && Integer.parseInt(s) <= 150) { // 在范围内
							LEICOUNT = Integer.parseInt(s);
							gameStatus = -1;
							timer.stop();
							start();
							itemStart.setText("开始游戏");
							itemPause.setEnabled(false);
							jd.setVisible(false);
						} else if (Integer.parseInt(s) < 15 || Integer.parseInt(s) > 150){
							play("err");
							JOptionPane.showMessageDialog(jd, "雷数不在范围内，请输入数字15-150。", "警告！   By：我是陌宇吖", JOptionPane.PLAIN_MESSAGE);
						}
						break;
					}
					
					if(rOn.isSelected()) { // 如果背景音乐打开
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
					} else { // 如果背景音乐关闭
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
		} else if (cmd == "退出游戏") {
			play("menu");
			if(gameStatus != -1) {
				play("tips");
				int option = JOptionPane.showConfirmDialog(null,"游戏还没结束，是否退出游戏？", "是否退出游戏？   By：我是陌宇吖", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE, null);
				if(option == JOptionPane.YES_NO_OPTION) {
					System.exit(0);
				}
			} else {
				System.exit(0);
			}
		} else if (cmd == "关于游戏") {
			play("menu");
			JDialog jd = new JDialog(frame,"扫雷 -＞ 关于游戏   By：我是陌宇吖");
			jd.setSize(300,400);
			jd.setResizable(false);
			jd.setLocationRelativeTo(jd.getOwner());
			jd.setLayout(new BorderLayout());
			jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			JLabel title = new JLabel("关于游戏",JLabel.CENTER);
			title.setFont(new Font("楷体",Font.BOLD,50));
			title.setText("<html><span color=red>关于游戏</span></html>");
			jd.add(title,BorderLayout.NORTH);
			JLabel content = new JLabel("<html><body>游戏名字：陌宇扫雷<br>游戏版本：v 1.0.3<br>游戏作者：我是陌宇吖<br>作者QQ号：2289864265<br>游戏介绍：游戏目标是在最短的时间内根据点击格子出现的数字找出所有非雷格子，同时避免踩雷，踩到一个雷即全盘皆输。</body></html>",JLabel.CENTER);
			content.setFont(new Font("宋体",Font.BOLD,20));
			jd.add(content,BorderLayout.CENTER);
			JButton btn = new JButton("确定");
			btn.setFont(new Font("黑体",Font.BOLD,20));
			btn.setText("<html><span color=blue>确     定</span></html>");
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
		} else if (cmd == "联系作者") {
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
								labelFlagCount.setText("旗数：" + FLAGCOUNT);
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
			JOptionPane.showMessageDialog(frame, "游戏还未开始，请点击左上方【游戏菜单】-＞[开始游戏]，开始扫雷游戏。", "提示！   By：我是陌宇吖", JOptionPane.PLAIN_MESSAGE);
		} else if (gameStatus == 0) {
			play("tips");
			JOptionPane.showMessageDialog(frame, "游戏正在暂停中，请点击左上角【游戏菜单】-＞[继续游戏]，来继续你的游戏。", "提示！   By：我是陌宇吖",
					JOptionPane.PLAIN_MESSAGE);
		}
	}

	protected void start() {
		FLAGCOUNT = LEICOUNT;
		// 恢复数据按钮
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				data[i][j] = 0;
				btns[i][j].setBackground(new Color(244, 183, 113));
				btns[i][j].setEnabled(true);
				btns[i][j].setText("");
				btns[i][j].setIcon(guessIcon);
			}
		}
		// 恢复状态栏
		unopen = ROW * COL;
		opened = 0;
		seconds = 0;
		labelUnopen.setText("未开：" + unopen);
		labelOpened.setText("已开：" + opened);
		labelLeiCount.setText("雷数：" + LEICOUNT);
		labelFlagCount.setText("旗数：" + FLAGCOUNT);
		labelSeconds.setText("用时：" + seconds + "s");
		addLei();
	}

	/*
	 * 1.数据清零 2.给按钮回复状态 3.时钟重新启动
	 */
	private void restart() {
		FLAGCOUNT = LEICOUNT;
		// 恢复数据按钮
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COL; j++) {
				data[i][j] = 0;
				btns[i][j].setBackground(new Color(244, 183, 113));
				btns[i][j].setEnabled(true);
				btns[i][j].setText("");
				btns[i][j].setIcon(guessIcon);
			}
		}
		// 恢复状态栏
		unopen = ROW * COL;
		opened = 0;
		seconds = 0;
		labelUnopen.setText("未开：" + unopen);
		labelOpened.setText("已开：" + opened);
		labelLeiCount.setText("雷数：" + LEICOUNT);
		labelFlagCount.setText("旗数：" + FLAGCOUNT);
		labelSeconds.setText("用时：" + seconds + "s");
		// 时钟重新启动
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
			itemStart.setText("开始游戏");
			timer.stop();
			bannerBtn.setIcon(winIcon);
			play("victory");
			JOptionPane.showMessageDialog(frame, "恭喜，你赢了！用时" + seconds + "s\n请点左上角【游戏菜单】-＞[开始游戏]，开始下一局游戏。", "赢了   By：我是陌宇吖", JOptionPane.PLAIN_MESSAGE);
		}
	}

	private void lose() {
		gameStatus = -1;
		itemPause.setEnabled(false);
		itemStart.setText("开始游戏");
		bannerBtn.setIcon(failIcon); // banner 设置为暴雷
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
						Font f = new Font("黑体",Font.BOLD,15);
						btns[i][j].setFont(f);
					}
				}
			}
		}
		play("bomb");
		play("defeat");
		JOptionPane.showMessageDialog(frame, "可惜你暴雷了，用时" + seconds + "s\n请点左上角【游戏菜单】-＞[开始游戏]，开始下一局游戏。", "你暴雷了！   By：我是陌宇吖",
				JOptionPane.PLAIN_MESSAGE);
	}

	private void openCell(int i, int j) {

		JButton btn = btns[i][j];
		
		if (!btn.isEnabled())
			return; // 如果打开了，就返回
		if (btn.getIcon() == win_flagIcon)
			return; // 如果Cell为旗子则返回

		btn.setIcon(null);
		btn.setEnabled(false);
		btn.setOpaque(true);
		btn.setBackground(new Color(180,238,180));
		btn.setText(data[i][j] + "");
		Font f = new Font("黑体",Font.BOLD,15);
		btn.setFont(f);

		addOpenCount();

		if (data[i][j] == 0) {
			if (i > 0 && j > 0 && data[i - 1][j - 1] != -1) openCell(i - 1, j - 1); // 左上
			if (i > 0 && data[i - 1][j] != -1) openCell(i - 1, j); // 上
			if (i > 0 && j < 19 && data[i - 1][j + 1] != -1) openCell(i - 1, j + 1); // 右上
			if (j > 0 && data[i][j - 1] != -1) openCell(i, j - 1); // 左
			if (j < 19 && data[i][j + 1] != -1) openCell(i, j + 1); // 右
			if (i < 19 && j > 0 && data[i + 1][j - 1] != -1) openCell(i + 1, j - 1); // 左下
			if (i < 19 && data[i + 1][j] != -1) openCell(i + 1, j); // 下
			if (i < 19 && j < 19 && data[i + 1][j + 1] != -1) openCell(i + 1, j + 1); // 右下
		}
	}

	private void addOpenCount() {
		opened++;
		unopen--;
		labelUnopen.setText("未开：" + unopen);
		labelOpened.setText("已开：" + opened);
	}
	
	// 设置旗子
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == e.BUTTON3 && gameStatus == 1) { // 点击鼠标右键    且    游戏运行中
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COL; j++) {
					if(e.getSource() == btns[i][j]) {
						if(btns[i][j].getIcon() == guessIcon) { // 将Cell设置为旗子
							play("flag");
							if(FLAGCOUNT > 0) {								
								btns[i][j].setIcon(win_flagIcon);
								btns[i][j].setBackground(Color.orange);
								FLAGCOUNT--;
							} else {
								play("tips");
								JOptionPane.showMessageDialog(frame, "已经没有旗子了！", "提示！   By：我是陌宇吖", JOptionPane.PLAIN_MESSAGE);
							}
						} else if(btns[i][j].getIcon() == win_flagIcon) { // 将Cell设置为？
							play("mark");
							btns[i][j].setIcon(null);
							Font f=new Font("华为琥珀",Font.BOLD,20);
							btns[i][j].setFont(f);
							btns[i][j].setText("<html><span color='#68ee68'>?</span></html>");
							btns[i][j].setBackground(Color.blue);
						} else if(btns[i][j].getText() == "<html><span color='#68ee68'>?</span></html>") { // 将Cell还原
							play("re");
							btns[i][j].setFont(null);
							btns[i][j].setText("");
							btns[i][j].setIcon(guessIcon);
							btns[i][j].setBackground(new Color(244, 183, 113));
							FLAGCOUNT++;
						}
						labelFlagCount.setText("旗数：" + FLAGCOUNT);
					}
				}
			}
		} else if (e.getButton() == e.BUTTON3 && gameStatus == 0) { // 点击鼠标右键     且     游戏暂停中
			play("tips");
			JOptionPane.showMessageDialog(frame, "游戏正在暂停中，请点击左上角【游戏菜单】-＞[继续游戏]，来继续你的游戏。", "提示！   By：我是陌宇吖", JOptionPane.PLAIN_MESSAGE);
		} else if (e.getButton() == e.BUTTON3 && gameStatus == -1) { // 点击鼠标右键     且     游戏未开始
			for (int i = 0; i < ROW; i++) {
				for (int j = 0; j < COL; j++) {
					if(e.getSource() == btns[i][j] && (btns[i][j].getIcon() == guessIcon || btns[i][j].getIcon() == win_flagIcon || btns[i][j].getText() == "<html><span color='#68ee68'>?</span></html>")) {
						play("tips");
						JOptionPane.showMessageDialog(frame, "游戏还未开始，请点击左上角【游戏菜单】-＞[开始游戏]，开始扫雷游戏。", "提示！   By：我是陌宇吖", JOptionPane.PLAIN_MESSAGE);
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