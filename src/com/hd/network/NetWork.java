package com.hd.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.HashMap;
import java.util.Map;

import com.hd.dao.AdminDao;
import com.hd.dao.EmlpoyeeDao;
import com.hd.dao.GenerallyDao;

import message.MsgCallBack;

public class NetWork {

	private MsgCallBack msgCallBack;

	// ��һ��Map���Ͻ���������Ϣ
	Map<String, Socket> socketMap = new HashMap<String, Socket>();
	GenerallyDao generallyDao = new GenerallyDao();
	EmlpoyeeDao emlpoyeeDao = new EmlpoyeeDao();
	AdminDao adminDao = new AdminDao();

	public NetWork(MsgCallBack msgCallBack) {
		this.msgCallBack = msgCallBack;
	}

	// ����������
	public void statrServer(int port) {
		Runnable task = new Runnable() {
			public void run() {
				ServerSocket server = null;
				Socket socket = null;
				String clientIp;
				int clientPort;
				try {
					// ����������
					server = new ServerSocket(port);
					// ���ؿ����ɹ�����Ϣ��������
					msgCallBack.onCreateServer(true);
					while (true) {
						socket = server.accept();
						clientIp = socket.getInetAddress().getHostAddress();
						clientPort = socket.getPort();
						// �ش���Ϣ�����ߵ���������Ŀͻ�����Ϣ
						msgCallBack.onAccept(clientIp, clientPort);

						// �����������ͻ��˴�����Ϣ��socket
						sproccessSocket(socket, clientIp, clientPort);

					}

				} catch (IOException e) {
					// System.out.println(e.getMessage());
					msgCallBack.onCreateServer(false);
				} finally {

				}
			}
		};
		new Thread(task).start();
	}

	// �ͻ������ӷ����� �ͻ�����������������Ӻ󣬿�ʼ����ָ����ͻ��ˣ������ܹ���Ӧ
	public void connectServer(String serverIp, int serverPort) {
		Runnable task = new Runnable() {
			public void run() {
				Socket socket = null;
				try {
					socket = new Socket(serverIp, serverPort);
					// ���ڿͻ��˵�socket���
					String SocketKey = cproccessSocket(socket, serverIp, serverPort);
					// �ͻ������ڷ���ָ��
					msgCallBack.onConnecteServer(true, SocketKey);

				} catch (IOException e) {
					msgCallBack.onConnecteServer(false, null);
				}
			}
		};
		new Thread(task).start();
	}

	// �����������socket
	public String sproccessSocket(Socket socket, String ip, int port) {
		String socketKey = ip + ":" + port;
		socketMap.put(socketKey, socket);
		Runnable task = new Runnable() {

			@Override
			public void run() {
				read(socketKey);
			}

		};
		new Thread(task).start();
		return socketKey;
	}

	// ����ͻ��˶�socket
	public String cproccessSocket(Socket socket, String ip, int port) {
		String socketKey = ip + ":" + port;
		socketMap.put(socketKey, socket);
		return socketKey;
	}

	// ����˽���ָ��
	public void read(String socketKey) {
		Socket socket = socketMap.get(socketKey);
		OutputStream os = null;
		InputStream is = null;
		int count = 0;
		byte[] buff = new byte[1024];
		String content = null;
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
			while (true) {
				count = is.read(buff);
				if (count > 0) {
					content = new String(buff, 0, count, "UTF-8");
					switch (content) {
					case "��¼":
						print(socketKey+"ִ�е�¼����");
						// ���������ͻ���һ����Ӧ����ֹճ��
						os.write("��¼".getBytes());
						// ���Ŷ�ȡ�˺���Ϣ
						count = is.read(buff);
						// ������յ�-1
						if (count > 0) {
							// ��ȡ�ַ������˺���Ϣ
							String accountInfo = new String(buff, 0, count, "UTF-8");
							// ��������˵Ŀͻ��˵�¼��������
							generallyDao.clientLogin(os, accountInfo);
						} else { // ���߿ͻ�������¼ʧ��
							os.write("��¼ʧ��...".getBytes());
						}

						break;
					case "�ϰ��":
						print(socketKey+"ִ���ϰ�򿨲���");
						// ���������ͻ���һ����Ӧ����ֹճ��
						os.write("�ϰ��".getBytes());
						// ���Ŷ�ȡ�ϰ�򿨵��˺���
						count = is.read(buff);
						// ������յ�-1
						if (count > 0) {
							String e_account = new String(buff, 0, count, "UTF-8");
							// ִ���ϰ�򿨷���
							emlpoyeeDao.clientWorkUp(os, e_account);
						} else {
							os.write("��ʧ��".getBytes());
						}
						break;
					case "�°��":
						print(socketKey+"ִ���°�򿨲���");
						// ���������ͻ���һ����Ӧ����ֹճ��
						os.write("�°��".getBytes());
						// ���Ŷ�ȡ�ϰ�򿨵��˺���
						count = is.read(buff);
						// ������յ�-1
						if (count > 0) {
							String e_account = new String(buff, 0, count, "UTF-8");
							// ִ���°�򿨷���
							emlpoyeeDao.clientWorkDown(os, e_account);
						} else {
							os.write("��ʧ��".getBytes());
						}
						break;
					case "�鿴�ҵĿ��ڼ�¼":
						print(socketKey+"ִ�в鿴������Ϣ����");
						// ���������ͻ���һ����Ӧ����ʾ���յ�����
						os.write("�鿴�ҵĿ��ڼ�¼".getBytes());
						// ���Ŷ�ȡ�˺��Լ���ѯ����
						count = is.read(buff);
						// ������յ�-1
						if (count > 0) {
							String acceptInfo = new String(buff, 0, count, "UTF-8");
							// ִ�в�ѯ����
							emlpoyeeDao.viewMyInfo(os, acceptInfo);
						} else {
							os.write("��ѯʧ��".getBytes());
						}
						break;
					case "���Ա����Ϣ":
						print(socketKey+"ִ�����Ա������");
						// ���߿ͻ����յ�����
						os.write("���Ա����Ϣ".getBytes());
						// ���Ŷ�ȡԱ����Ϣ
						count = is.read(buff);
						// ������յ�-1
						if (count > 0) {
							String e_info = new String(buff, 0, count, "UTF-8");
							adminDao.addEmployee(os, e_info);
						} else {
							os.write("���ʧ��".getBytes());
						}
						break;
					case "�鿴Ա������ͳ��":
						print(socketKey+"ִ�в鿴Ա������ͳ�Ʋ���");
						// ���������ͻ���һ����Ӧ����ʾ���յ�����
						os.write("�鿴Ա������ͳ��".getBytes());
						// ���Ŷ�ȡ�˺��Լ���ѯ����
						count = is.read(buff);
						// ������յ�-1
						if (count > 0) {
							String acceptInfo = new String(buff, 0, count, "UTF-8");
							// ִ�в�ѯ����
							adminDao.viewAllInfo(os, acceptInfo);
						} else {
							os.write("��ѯʧ��".getBytes());
						}
						break;
					}
				}
			}
		} catch (IOException e) {
			print(socketKey + "�Ͽ����ӣ�������Ϣ��" + e.getMessage());
		}

	}

	public void print(String str) {
		System.out.println("[YTG������]"+str);
		
	}

	// �ͻ��������������ָ��
	public void send(String str, String socketKey) {
		Socket socket = socketMap.get(socketKey);
		OutputStream os = null;
		try {
			os = socket.getOutputStream();
			// �ͻ������������������
			os.write(str.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// �ͻ��˶�ȡ������������Ϣ
	public String clientRead(String socketKey) {
		Socket socket = socketMap.get(socketKey);
		InputStream is = null;
		int count;
		byte[] buff = new byte[1024];
		try {
			is = socket.getInputStream();
			count = is.read(buff);
			if (count > 0) {
				String response = new String(buff, 0, count);
				return response;
			} else {
				return "����������Ӧ";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	
	
	
	
	
	
	
//	// ��¼����
//	public void clientLogin(OutputStream os, String accountInfo) {
//		Runnable task = new Runnable() {
//			@Override
//			public void run() {
//				// �������ݿ�
//				Connection conn = JdbcUtils.getConnection();
//				// ����Ԥ������
//				PreparedStatement pstmt = null;
//				// ���������
//				ResultSet rs = null;
//
//				// ʹ���ַ����ָ��õ�¼�û�����ݡ��˺š�����
//				String[] arr1 = accountInfo.split("]");
//				String[] arr2 = arr1[0].split("\\[");
//				String[] arr3 = arr1[1].split(",");
//				// �û����
//				String capacity = arr2[1];
//				// �û��˺�
//				String account = arr3[0];
//				// �û�����
//				String pwd = arr3[1];
//				// ����sql����Ӧ��ͬ����û�ִ�е�¼����
//				String[] sqls = { "select * from employee where employeeAccount=? and employeePwd=?",
//						"select * from admin where adminId=? and adminPwd=?" };
//
//				// ��ͨԱ��ִ��sqls[0]
//				if (capacity.equals("��ͨԱ��")) {
//					try {
//						// ʵ��Ԥ������
//						pstmt = conn.prepareStatement(sqls[0]);
//						// ͨ�����ֵ
//						pstmt.setString(1, account);
//						pstmt.setString(2, pwd);
//						// ��ȡ�����
//						rs = pstmt.executeQuery();
//						// ����н���������˺�������ȷ
//						if (rs.next()) {
//							// ���߿ͻ������¼�ɹ�
//							os.write("��¼�ɹ�".getBytes());
//						} else {
//							// ���߿ͻ������¼ʧ��
//							os.write("��¼ʧ��".getBytes());
//						}
//					} catch (SQLException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					} finally {
//						JdbcUtils.closeResultSet(rs);
//						JdbcUtils.closeConnection(conn);
//					}
//
//				} else {
//					try {
//						// ʵ��Ԥ������
//						pstmt = conn.prepareStatement(sqls[1]);
//						// ͨ�����ֵ
//						pstmt.setString(1, account);
//						pstmt.setString(2, pwd);
//						// ��ȡ�����
//						rs = pstmt.executeQuery();
//						// ����н���������˺�������ȷ
//						if (rs.next()) {
//							// ���߿ͻ������¼�ɹ�
//							os.write("��¼�ɹ�".getBytes());
//						} else {
//							// ���߿ͻ������¼ʧ��
//							os.write("��¼ʧ��".getBytes());
//						}
//					} catch (SQLException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					} finally {
//						JdbcUtils.closeResultSet(rs);
//						JdbcUtils.closeConnection(conn);
//					}
//				}
//			}
//		};
//		new Thread(task).start();
//
//	}
//
//	// �ͻ���ִ���ϰ�򿨷���
//	public void clientWorkUp(OutputStream os, String e_account) {
//		Runnable task = new Runnable() {
//			@Override
//			public void run() {
//				// �����жϽ����ǲ�����Ϣ
//				if (isRest() == true) {
//					try {
//						// ����Ϣ������߿ͻ�������Ϣ�գ����ý��д�
//						os.write("��Ϣ��".getBytes());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				} else {
//					Connection conn = JdbcUtils.getConnection();
//					PreparedStatement pstmt = null;
//					ResultSet rs = null;
//					String employeeId = null;
//					String status;
//					// �Ȼ�ȡ��ͨԱ����id
//					String sql = "select employeeId from employee where employeeAccount=?";
//					try {
//						// ��һ�α���:���ڻ�ȡԱ��ID
//						pstmt = conn.prepareStatement(sql);
//						pstmt.setString(1, e_account);
//						rs = pstmt.executeQuery();
//						if (rs.next()) {
//							// ��ȡ��ͨԱ����ΨһID
//							employeeId = rs.getString("employeeId");
//							Date date = new Date();
//							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//							// ��ȡ��ǰ����
//							String dateNow = dateFormat.format(date);
//							// �鿴��û�д���ϰ࿨�����°࿨�������κ�һ�����������ٴ�
//							String sql2 = "select workUp from clock where employeeId=? and dayDate=?";
//							// �ڶ��α��룬�����ж��Ƿ�����
//							pstmt = conn.prepareStatement(sql2);
//							pstmt.setString(1, employeeId);
//							pstmt.setString(2, dateNow);
//							rs = pstmt.executeQuery();
//							// �н�����Ǵ������
//							if (rs.next()) {
//								// ����Խ����һ���жϣ���������Ϊ�գ�˵�����������ϰ�򿨣����Ϊ�գ�˵���û�û�н����ϰ�򿨣�ֻ�������°��
//								if (rs.getString("workUp") != null) {
//									String info = "�Ѿ����������ʱ��Ϊ:" + rs.getString("workUp");
//									os.write(info.getBytes()); // ���߿ͻ����Ѿ��������
//								} else {
//									System.out.println(359);
//									String info = "���Ѿ������°�򿨣����ղ����ٽ����ϰ�򿨣�";
//									os.write(info.getBytes()); // ���߿ͻ����Ѿ��������
//								}
//							} else {// û��,���д�
//								dateFormat = new SimpleDateFormat("HH:mm");
//								// ��ȡ��ǰ��ʱ��
//								String workUpTime = dateFormat.format(date);
//								// ���������ʱ��
//								String normalTime = "09:00";
//								// �ٵ������ʱ��
//								String latestTime = "11:00";
//								// ��ȡ��״̬
//								status = workUpStatus(workUpTime, normalTime, latestTime);
//								if (status.equals("����")) {
//									// ���߿ͻ��˴�̫���ˣ����ܴ�
//									os.write("����".getBytes());
//								} else {
//									String sql3 = "insert into clock(employeeId,dayDate,workUp,workUpStatus) values"
//											+ "('" + employeeId + "','" + dateNow + "','" + workUpTime + "','" + status
//											+ "')";
//									pstmt = conn.prepareStatement(sql3);
//									int ret = pstmt.executeUpdate();
//									if (ret > 0) {
//										// ����Ϣ
//										String clockInfo = "�򿨳ɹ�,״̬Ϊ:" + status + ",��ʱ��Ϊ:" + workUpTime;
//										// ���߿ͻ��˴򿨳ɹ�
//										os.write(clockInfo.getBytes());
//									} else {
//										// ���߿ͻ��˴�ʧ��
//										os.write("��ʧ��".getBytes());
//									}
//								}
//							}
//
//						} else {
//							os.write("��ʧ��".getBytes());
//						}
//					} catch (SQLException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					} finally {
//						JdbcUtils.closeResultSet(rs);
//						JdbcUtils.closeConnection(conn);
//					}
//				}
//			}
//		};
//		new Thread(task).start();
//
//	}
//
//	// �ͻ���ִ���°�򿨷���
//	public void clientWorkDown(OutputStream os, String e_account) {
//		Runnable task = new Runnable() {
//			public void run() {
//				Connection conn = JdbcUtils.getConnection();
//				PreparedStatement pstmt = null;
//				ResultSet rs = null;
//				String employeeId = null;
//				// ��״̬
//				String status;
//				// ��ʱ��
//				String workDownTime;
//				// �����°�ʱ��
//				String normalTime = "18:00";
//				// ���˿����ֽ�ʱ���
//				String notWorkTime = "16:00";
//				Date date = new Date();
//				// ���ڸ�ʽ
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//				// ��ʱ���ʽ
//				SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
//				// ��ȡ��ǰ����
//				String dateNow = dateFormat.format(date);
//				// ��ȡ��ǰʱ��
//				workDownTime = dateFormat2.format(date);
//				// �����ж�������û�д򿨣����û�У�����Ҫ��������,�°��ʱ��,�°��״̬
//				String sql1 = "select * from clock where "
//						+ "employeeId=(select employeeId from employee where employeeAccount=?)" + "and dayDate=?";
//				try {
//					if (isRest() == true) {
//						os.write("��������Ϣ�գ�".getBytes());
//					} else {
//						pstmt = conn.prepareStatement(sql1);
//						pstmt.setString(1, e_account);
//						pstmt.setString(2, dateNow);
//						rs = pstmt.executeQuery();
//						// ����н������֤�������д򿨣����ж���û�д���°࿨��Ȼ����´򿨼�¼
//						if (rs.next()) {
//							String sql2 = "select workDown from clock where "
//									+ "employeeId=(select employeeId from employee where employeeAccount=?)"
//									+ "and dayDate=?";
//							pstmt = conn.prepareStatement(sql2);
//							pstmt.setString(1, e_account);
//							pstmt.setString(2, dateNow);
//							rs = pstmt.executeQuery();
//							// ����н����ʾ�Ѿ����й��°��
//							if (rs.next() && rs.getString("workDown") != null) {
//								String info = "�Ѿ��°��,��ʱ��Ϊ:" + rs.getString("workDown");
//								// ���߿ͻ����û��Ѿ��������
//								os.write(info.getBytes());
//							} else { // ��û���°࿨
//								// ��ȡ��״̬
//								status = workDownStatus(workDownTime, normalTime, notWorkTime);
//								// ���´򿨼�¼������°��ʱ���״̬
//								String sql3 = "update clock set workDown=?,workDownStatus=? "
//										+ "where employeeId=(select employeeId from employee where employeeAccount=?)"
//										+ "and dayDate=?";
//								pstmt = conn.prepareStatement(sql3);
//								pstmt.setString(1, workDownTime);
//								pstmt.setString(2, status);
//								pstmt.setString(3, e_account);
//								pstmt.setString(4, dateNow);
//								int ret = pstmt.executeUpdate();
//								if (ret > 0) {
//									String downInfo = "�°�򿨳ɹ�,��ʱ��Ϊ:" + workDownTime;
//									os.write(downInfo.getBytes());
//								} else {
//									os.write("��ʧ��".getBytes());
//								}
//							}
//						} else {
//							if ((workDownTime.compareTo("11:00")) >= 0) {
//								// ����û�д򿨣���Ҫ����򿨼�¼
//								status = workDownStatus(workDownTime, normalTime, notWorkTime);
//								// �Ȼ��Ա��id
//								String sql4 = "select employeeId from employee where employeeAccount=?";
//								String e_id = null;
//								pstmt = conn.prepareStatement(sql4);
//								pstmt.setString(1, e_account);
//								rs = pstmt.executeQuery();
//								if (rs.next()) {
//									e_id = rs.getString("employeeId");
//								}
//								String sql5 = "insert into clock(employeeId,dayDate,workDown,workDownStatus) values(?,?,?,?)";
//								pstmt = conn.prepareStatement(sql5);
//								pstmt.setString(1, e_id);
//								pstmt.setString(2, dateNow);
//								pstmt.setString(3, workDownTime);
//								pstmt.setString(4, status);
//								int ret = pstmt.executeUpdate();
//								if (ret > 0) {
//									String downInfo = "�°�򿨳ɹ�,��ʱ��Ϊ:" + workDownTime;
//									os.write(downInfo.getBytes());
//								} else {
//									os.write("��ʧ��".getBytes());
//								}
//							} else {
//
//								os.write("ʮһ��ǰδ�����ϰ�򿨣����Ƚ����ϰ�򿨣�".getBytes());
//							}
//						}
//					}
//				} catch (SQLException | IOException e) {
//					e.printStackTrace();
//				} finally {
//					// �ر����ݿ���Դ
//					JdbcUtils.closeResultSet(rs);
//					JdbcUtils.closeConnection(conn);
//				}
//
//			}
//		};
//		new Thread(task).start();
//	}
//
//	// �ͻ���ִ�в鿴���˿�����Ϣ����
//	public void viewMyInfo(OutputStream os, String acceptInfo) {
//		Runnable task = new  Runnable() {
//			public void run() {
//				//�����ݿ⽨������
//				Connection conn = JdbcUtils.getConnection();
//				//����Ԥ����ִ����
//				PreparedStatement pstmt = null;
//				//���������
//				ResultSet rs = null;
//				//�Ƚ��õ�����Ϣ���д������ʽΪ�˺�,��ѯ�·ݣ�
//				String e_id = null;
//				//��������
//				String clockDay = null;
//				//�ٵ�����
//				String lateCount = null;
//				//���˴���
//				String earlyOutCount = null;
//				//��������
//				String notWorkCount = null;
//				String[] arr = acceptInfo.split(",");
//				String e_account = arr[0];
//				String selecCond = arr[1];
//				Date date = new Date();
//				// ���ڸ�ʽ
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//				//��ȡ��ǰ����
//				String dateNow = dateFormat.format(date);
//				String _dateNow = dateNow.substring(0, 7);
//				//�����жϲ�ѯ���·��Ƿ�Ϊ��ǰ�·ݣ������ѯ������ļ�¼���������һ�����µ�
//				/*
//				 * �����������1����ѯ�·�Ϊ��ǰ�� ���鵱ǰ�¿�ʼ������Ϊֹ
//				 * 		     ��2����ѯ�·ݴ��ڵ�ǰ�£����ش�����Ϣ
//				 * 		     ��3����ѯ�·�С�ڵ�ǰ�£���ѯ������Ϣ
//				 */
//				
//				//Ϊ��ǰ��
//				if(selecCond.equals(_dateNow)) {
//					//���Ȳ�һ��Ա����ID
//					String idSql = "select employeeId from employee where employeeAccount=?";
//					try {
//						pstmt = conn.prepareStatement(idSql);
//						pstmt.setString(1, e_account);
//						rs = pstmt.executeQuery();
//						if(rs.next()) {
//							e_id = rs.getString("employeeId");
//							//���id��ʼ���в�ѯ
//							//{1}���Ȳ�ѯ�ٵ�����
//							String lateSql = "select count(workUpStatus) late "
//									+ "from (select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
//									+ "from (select  * from worksheet   where dayDate like ? and  "
//									+ "dayDate between '' and ?) wk  "
//									+ "left join (select * from clock where employeeId=?) ck  on wk.dayDate = ck.dayDate) dk  "
//									+ "where workUpStatus in ('�ٵ�') and workDownStatus in('����','����')";
//							pstmt = conn.prepareStatement(lateSql);
//							pstmt.setString(1, _dateNow+"%");
//							pstmt.setString(2, dateNow);
//							pstmt.setString(3, e_id);
//							rs = pstmt.executeQuery();
//							if(rs.next()) {
//								//��óٵ�����
//								lateCount = rs.getString("late");
//							}else {
//								os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//							}
//							//{2}��ѯ���˴���
//							String earlySql = "select count(workDownStatus) early from "
//									+ "(select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus from "
//									+ "(select  * from worksheet   where dayDate like ? and dayDate "
//									+ "between '' and ?) wk  "
//									+ "left join (select * from clock where employeeId=?) ck  on wk.dayDate = ck.dayDate) dk  "
//									+ "where workDownStatus in ('����') and workUpStatus in('����','�ٵ�')";
//							pstmt = conn.prepareStatement(earlySql);
//							pstmt.setString(1, _dateNow+"%");
//							pstmt.setString(2, dateNow);
//							pstmt.setString(3, e_id);
//							rs = pstmt.executeQuery();
//							if(rs.next()) {
//								//������˴���
//								earlyOutCount = rs.getString("early");
//							}else {
//								os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//							}
//							//{3}��ѯ��������
//							String notWorlSql = "select count(ek.dayDate) ow "
//									+ "from (select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
//									+ "from (select  * from worksheet   where dayDate like ? "
//									+ "and dayDate between '' and ?) wk  "
//									+ "left join (select * from clock where employeeId=?) ck  "
//									+ "on wk.dayDate = ck.dayDate order by wk.dayDate) ek where ek.employeeId is null "
//									+ "or (ek.workUpStatus is null or ek.workUpStatus='����') "
//									+ "or (ek.workDownStatus is null or ek.workDownStatus='����') "
//									+ "or (ek.workUpStatus is null and ek.workDownStatus='����') "
//									+ "or (ek.workUpStatus ='����' and ek.workDownStatus is null)";
//													
//							pstmt = conn.prepareStatement(notWorlSql);
//							pstmt.setString(1, _dateNow+"%");
//							pstmt.setString(2, dateNow);
//							pstmt.setString(3, e_id);
//							rs = pstmt.executeQuery();
//							if(rs.next()) {
//								//��ÿ�������
//								notWorkCount = rs.getString("ow");
//							}else {
//								os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//							}
//							//{4}��ѯ���ڴ���
//							String clockDaySql = "select count(ek.dayDate) ow from "
//									+ "(select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
//									+ "from (select  * from worksheet   "
//									+ "where dayDate like ? and dayDate between '' and ?) wk  "
//									+ "left join (select * from clock where employeeId=?) ck  "
//									+ "on wk.dayDate = ck.dayDate order by wk.dayDate) ek "
//									+ "where  ek.employeeId is not null;";
//													
//							pstmt = conn.prepareStatement(clockDaySql);
//							pstmt.setString(1, _dateNow+"%");
//							pstmt.setString(2, dateNow);
//							pstmt.setString(3, e_id);
//							rs = pstmt.executeQuery();
//							if(rs.next()) {
//								//������˴���
//								clockDay = rs.getString("ow");
//							}else {
//								os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//							}
//							String allMyInfo = "��ѯ�ɹ�������:"+clockDay+"�� {�ٵ�:"+lateCount+","
//													+ "����:"+earlyOutCount+","
//													+ "����:"+notWorkCount+"}";
//							os.write(allMyInfo.getBytes());
//							
//						}else {
//							os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//						}
//					} catch (SQLException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					
//					
//				}
//				//���ڵ�ǰ��
//				else if((selecCond.compareTo(_dateNow))>0) {
//					try {
//						os.write("��ѯ�·ݴ��ڵ�ǰ�£���ѯʧ�ܣ�".getBytes());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				//С�ڵ�ǰ��
//				else {
//					
//				}
//			}
//		};
//		new Thread(task).start();
//	}
//
//	// �ͻ���ִ�����Ա��
//	public void addEmployee(OutputStream os, String e_info) {
//		Runnable task = new Runnable() {
//			@Override
//			public void run() {
//				// �����ݿ⽨������
//				Connection conn = JdbcUtils.getConnection();
//				// ����Ԥ����ִ����
//				PreparedStatement pstmt = null;
//				// ���������
//				ResultSet rs = null;
//				// �����ֶ�ֵ����Map������
//				HashMap<String, String> map = spiltEmplyeeInfo(e_info);
//				System.out.println(map.get(" employeeAccount"));
//
//				// ���Ȳ�ѯid�ǲ��ظ�����¼�˺��Ƿ��ظ�
//				String idSelectSql = "select * from employee where employeeId=?";
//				try {
//					pstmt = conn.prepareStatement(idSelectSql);
//					pstmt.setString(1, map.get("employeeId"));
//					rs = pstmt.executeQuery();
//					// �в�ѯ���
//					if (rs.next()) {
//						String info = "��Ա��id�Ѿ���ʹ�ã������Ա��id!";
//						os.write(info.getBytes());
//					} else { // û�в�ѯ����������idû��ʹ��
//						// �����ж�Ա���˺��Ƿ�ʹ��
//						String accounSelecttSql = "select * from employee where employeeAccount=?";
//						pstmt = conn.prepareStatement(accounSelecttSql);
//						pstmt.setString(1, map.get(" employeeAccount"));
//						rs = pstmt.executeQuery();
//						if (rs.next()) {
//							String info = "��Ա���˺��Ѿ���ʹ�ã������Ա���˺�!";
//							os.write(info.getBytes());
//						} else {
//							// �����ͨԱ��
//							if (map.get(" capacity").equals("��ͨԱ��")) {
//								String insertSql = "insert into employee values(?,?,?,?,?,?,?,?)";
//								pstmt = conn.prepareStatement(insertSql);
//								pstmt.setString(1, map.get("employeeId"));
//								pstmt.setString(2, map.get(" employeeName"));
//								pstmt.setString(3, map.get(" employeeAccount"));
//								pstmt.setString(4, map.get(" employeePwd"));
//								pstmt.setString(5, map.get(" position"));
//								pstmt.setString(6, map.get(" salary"));
//								pstmt.setString(7, map.get(" workDate"));
//								pstmt.setString(8, map.get(" capacity"));
//								int ret = pstmt.executeUpdate();
//								if (ret > 0) {
//									String addInfo = "���Ա���ɹ���";
//									os.write(addInfo.getBytes());
//								} else {
//									os.write("���ʧ��".getBytes());
//								}
//							} else {// ��ӹ���Ա������Ҫ��Ա����͹���Ա���ж������Ϣ
//								String employeeSql = "insert into employee values(?,?,?,?,?,?,?,?)";
//								pstmt = conn.prepareStatement(employeeSql);
//								pstmt.setString(1, map.get("employeeId"));
//								pstmt.setString(2, map.get(" employeeName"));
//								pstmt.setString(3, map.get(" employeeAccount"));
//								pstmt.setString(4, map.get(" employeePwd"));
//								pstmt.setString(5, map.get(" position"));
//								pstmt.setString(6, map.get(" salary"));
//								pstmt.setString(7, map.get(" workDate"));
//								pstmt.setString(8, map.get(" capacity"));
//								int ret1 = pstmt.executeUpdate();
//								if (ret1 > 0) {
//									System.out.println("���Ա����ɹ�");
//									String adminSql = "insert into admin values(?,?,?)";
//									pstmt = conn.prepareStatement(adminSql);
//									pstmt.setString(1, map.get("employeeId"));
//									pstmt.setString(2, map.get(" employeeName"));
//									pstmt.setString(3, map.get(" employeePwd"));
//									int ret2 = pstmt.executeUpdate();
//									if (ret2 > 0) {
//										String addInfo = "��ӹ���Ա�ɹ���";
//										os.write(addInfo.getBytes());
//									} else {
//										os.write("��ӹ���Աʧ�ܣ�".getBytes());
//									}
//								} else {
//									os.write("���Ա��ʧ��".getBytes());
//								}
//							}
//
//						}
//					}
//
//				} catch (SQLException e) {
//					String errorInfo = "���ݸ�ʽ�д��󣬴�����Ϣ��" + e.getMessage();
//					try {
//						os.write(errorInfo.getBytes());
//					} catch (IOException e1) {
//						e1.printStackTrace();
//					}
//				} catch (IOException e) {
//					e.printStackTrace();
//				} finally {
//					JdbcUtils.closeResultSet(rs);
//					JdbcUtils.closeConnection(conn);
//				}
//
//			}
//		};
//		new Thread(task).start();
//
//	}
//
//	// �ͻ���ִ�в鿴����Ա������ͳ��
//	public void viewAllInfo(OutputStream os,String acceptInfo) {
//		Runnable task = new  Runnable() {
//			public void run() {
//				//�����ݿ⽨������
//				Connection conn = JdbcUtils.getConnection();
//				//����Ԥ����ִ����
//				PreparedStatement pstmt = null;
//				//���������
//				ResultSet rs = null;
//				//�Ƚ��õ�����Ϣ���д������ʽΪ�˺�,��ѯ�·ݣ�
//				String e_id = null;
//				//��������
//				String clockDay = null;
//				//�ٵ�����
//				String lateCount = null;
//				//���˴���
//				String earlyOutCount = null;
//				//��������
//				String notWorkCount = null;
//				String[] arr = acceptInfo.split(",");
//				String e_name = arr[0];
//				String selecCond = arr[1];
//				Date date = new Date();
//				// ���ڸ�ʽ
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//				//��ȡ��ǰ����
//				String dateNow = dateFormat.format(date);
//				String _dateNow = dateNow.substring(0, 7);
//				//�����жϲ�ѯ���·��Ƿ�Ϊ��ǰ�·ݣ������ѯ������ļ�¼���������һ�����µ�
//				/*
//				 * �����������1����ѯ�·�Ϊ��ǰ�� ���鵱ǰ�¿�ʼ������Ϊֹ
//				 * 		     ��2����ѯ�·ݴ��ڵ�ǰ�£����ش�����Ϣ
//				 * 		     ��3����ѯ�·�С�ڵ�ǰ�£���ѯ������Ϣ
//				 */
//				
//				//Ϊ��ǰ��
//				if(selecCond.equals(_dateNow)) {
//					//���Ȳ�һ��Ա����ID
//					String idSql = "select employeeId from employee where employeeName=?";
//					try {
//						pstmt = conn.prepareStatement(idSql);
//						pstmt.setString(1, e_name);
//						rs = pstmt.executeQuery();
//						if(rs.next()) {
//							e_id = rs.getString("employeeId");
//							//���id��ʼ���в�ѯ
//							//{1}���Ȳ�ѯ�ٵ�����
//							String lateSql = "select count(workUpStatus) late "
//									+ "from (select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
//									+ "from (select  * from worksheet   where dayDate like ? and  "
//									+ "dayDate between '' and ?) wk  "
//									+ "left join (select * from clock where employeeId=?) ck  on wk.dayDate = ck.dayDate) dk  "
//									+ "where workUpStatus in ('�ٵ�') and workDownStatus in('����','����')";
//							pstmt = conn.prepareStatement(lateSql);
//							pstmt.setString(1, _dateNow+"%");
//							pstmt.setString(2, dateNow);
//							pstmt.setString(3, e_id);
//							rs = pstmt.executeQuery();
//							if(rs.next()) {
//								//��óٵ�����
//								lateCount = rs.getString("late");
//							}else {
//								os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//							}
//							//{2}��ѯ���˴���
//							String earlySql = "select count(workDownStatus) early from "
//									+ "(select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus from "
//									+ "(select  * from worksheet   where dayDate like ? and dayDate "
//									+ "between '' and ?) wk  "
//									+ "left join (select * from clock where employeeId=?) ck  on wk.dayDate = ck.dayDate) dk  "
//									+ "where workDownStatus in ('����') and workUpStatus in('����','�ٵ�')";
//							pstmt = conn.prepareStatement(earlySql);
//							pstmt.setString(1, _dateNow+"%");
//							pstmt.setString(2, dateNow);
//							pstmt.setString(3, e_id);
//							rs = pstmt.executeQuery();
//							if(rs.next()) {
//								//������˴���
//								earlyOutCount = rs.getString("early");
//							}else {
//								os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//							}
//							//{3}��ѯ��������
//							String notWorlSql = "select count(ek.dayDate) ow "
//									+ "from (select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
//									+ "from (select  * from worksheet   where dayDate like ? "
//									+ "and dayDate between '' and ?) wk  "
//									+ "left join (select * from clock where employeeId=?) ck  "
//									+ "on wk.dayDate = ck.dayDate order by wk.dayDate) ek where ek.employeeId is null "
//									+ "or (ek.workUpStatus is null or ek.workUpStatus='����') "
//									+ "or (ek.workDownStatus is null or ek.workDownStatus='����') "
//									+ "or (ek.workUpStatus is null and ek.workDownStatus='����') "
//									+ "or (ek.workUpStatus ='����' and ek.workDownStatus is null)";
//													
//							pstmt = conn.prepareStatement(notWorlSql);
//							pstmt.setString(1, _dateNow+"%");
//							pstmt.setString(2, dateNow);
//							pstmt.setString(3, e_id);
//							rs = pstmt.executeQuery();
//							if(rs.next()) {
//								//��ÿ�������
//								notWorkCount = rs.getString("ow");
//							}else {
//								os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//							}
//							//{4}��ѯ���ڴ���
//							String clockDaySql = "select count(ek.dayDate) ow from "
//									+ "(select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
//									+ "from (select  * from worksheet   "
//									+ "where dayDate like ? and dayDate between '' and ?) wk  "
//									+ "left join (select * from clock where employeeId=?) ck  "
//									+ "on wk.dayDate = ck.dayDate order by wk.dayDate) ek "
//									+ "where  ek.employeeId is not null;";
//													
//							pstmt = conn.prepareStatement(clockDaySql);
//							pstmt.setString(1, _dateNow+"%");
//							pstmt.setString(2, dateNow);
//							pstmt.setString(3, e_id);
//							rs = pstmt.executeQuery();
//							if(rs.next()) {
//								//������˴���
//								clockDay = rs.getString("ow");
//							}else {
//								os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//							}
//							String allMyInfo = "��ѯ�ɹ���"
//									+ "["+e_name+"]����:"+clockDay+"�� {�ٵ�:"+lateCount+","
//													+ "����:"+earlyOutCount+","
//													+ "����:"+notWorkCount+"}";
//							os.write(allMyInfo.getBytes());
//							
//						}else {
//							os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
//						}
//					} catch (SQLException e) {
//						e.printStackTrace();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					
//					
//				}
//				//���ڵ�ǰ��
//				else if((selecCond.compareTo(_dateNow))>0) {
//					try {
//						os.write("��ѯ�·ݴ��ڵ�ǰ�£���ѯʧ�ܣ�".getBytes());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				//С�ڵ�ǰ��
//				else {
//					
//				}
//			}
//		};
//		new Thread(task).start();
//	}
//
//	// �ж��ǲ�����Ϣ��
//	public boolean isRest() {
//		Date date = new Date();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		String currDate = dateFormat.format(date);
//		Connection conn = JdbcUtils.getConnection();
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		String sql = "select * from worksheet where dayDate=?";
//		try {
//			pstmt = conn.prepareStatement(sql);
//			pstmt.setString(1, currDate);
//			rs = pstmt.executeQuery();
//			if (rs.next()) { // �д�������Ϣ��
//				return false;
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			JdbcUtils.closeResultSet(rs);
//			JdbcUtils.closeConnection(conn);
//		}
//		return true;
//	}
//
//	// �����ж��ϰ��״̬ 9:00
//	public String workUpStatus(String workUpTime, String normalTime, String latestTime) {
//		String status = null;
//		// ���д�ʱ��ĶԱȣ�
//		int res = workUpTime.compareTo(normalTime);
//		// �������ָ����ʱ�����ǰ��Сʱ
//		if (res == 0 || (res > -2 && res < 0)) {
//			status = "����";
//		} else if (res < -2) { // �ߵ�ǰ��̫��//�ܾ�
//			status = "����";
//		} else if (res > 0) {
//			res = workUpTime.compareTo(latestTime);
//			if (res <= 0) {
//				status = "�ٵ�";
//			} else {
//				status = "����";
//			}
//		}
//		return status;
//	}
//
//	// �����ж��°��״̬
//	public String workDownStatus(String workDownTime, String normalTime, String notWorkTime) {
//		String status = null;
//		// ���д�ʱ��ĶԱȣ�
//		int res = workDownTime.compareTo(normalTime);
//		// ����°��ʱ����ڻ���������°�ʱ�䣬����Ϊ������
//		if (res == 0 || res > 0) {
//			status = "����";
//		}
//		// ���С��0����֤��Ա�������˻�������ɣ������ж�
//		else if (res < 0) {
//			// �����˿����ֽ����жԱ�
//			res = workDownTime.compareTo(notWorkTime);
//			// ���������Ϊ����
//			if (res > 0) {
//				status = "����";
//			} else {// ���С����Ϊ����
//				status = "����";
//			}
//		}
//		return status;
//	}
//
//	public HashMap<String, String> spiltEmplyeeInfo(String e_info) {
//		HashMap<String, String> map = new HashMap<String, String>();
//		String str = null;
//		// System.out.println(e_info);
//		// ��1����ȡ���������ڵ�����
//		Pattern pattern = Pattern.compile("(\\[[^\\]]*\\])");
//		Matcher matcher = pattern.matcher(e_info);
//		while (matcher.find()) {
//			str = matcher.group().substring(1, matcher.group().length() - 1);
//		}
//		// System.out.println(str);
//		// ��2��Ȼ�������ŵ����ݰ����ŷָ�
//		String[] str2 = str.split(",");
//		// �Լ�ֵ����ʽ�洢
//		for (String string : str2) {
//			// ��3��Ȼ���ա�=���ָ�
//			String[] str3 = string.split("=");
//			map.put(str3[0], str3[1]);
//		}
//		// System.out.println(map);
//		return map;
//	}
}
