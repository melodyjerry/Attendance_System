package com.hd.dao;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hd.database.JdbcUtils;

public class EmlpoyeeDao {
	//��
	public void clientWorkUp(OutputStream os, String e_account) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				// �����жϽ����ǲ�����Ϣ
				if (isRest() == true) {
					try {
						// ����Ϣ������߿ͻ�������Ϣ�գ����ý��д�
						os.write("��Ϣ��".getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					Connection conn = JdbcUtils.getConnection();
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					String employeeId = null;
					String status;
					// �Ȼ�ȡ��ͨԱ����id
					String sql = "select employeeId from employee where employeeAccount=?";
					try {
						// ��һ�α���:���ڻ�ȡԱ��ID
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, e_account);
						rs = pstmt.executeQuery();
						if (rs.next()) {
							// ��ȡ��ͨԱ����ΨһID
							employeeId = rs.getString("employeeId");
							Date date = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							// ��ȡ��ǰ����
							String dateNow = dateFormat.format(date);
							// �鿴��û�д���ϰ࿨�����°࿨�������κ�һ�����������ٴ�
							String sql2 = "select workUp from clock where employeeId=? and dayDate=?";
							// �ڶ��α��룬�����ж��Ƿ�����
							pstmt = conn.prepareStatement(sql2);
							pstmt.setString(1, employeeId);
							pstmt.setString(2, dateNow);
							rs = pstmt.executeQuery();
							// �н�����Ǵ������
							if (rs.next()) {
								// ����Խ����һ���жϣ���������Ϊ�գ�˵�����������ϰ�򿨣����Ϊ�գ�˵���û�û�н����ϰ�򿨣�ֻ�������°��
								if (rs.getString("workUp") != null) {
									String info = "�Ѿ����������ʱ��Ϊ:" + rs.getString("workUp");
									os.write(info.getBytes()); // ���߿ͻ����Ѿ��������
								} else {
									System.out.println(359);
									String info = "���Ѿ������°�򿨣����ղ����ٽ����ϰ�򿨣�";
									os.write(info.getBytes()); // ���߿ͻ����Ѿ��������
								}
							} else {// û��,���д�
								dateFormat = new SimpleDateFormat("HH:mm");
								// ��ȡ��ǰ��ʱ��
								String workUpTime = dateFormat.format(date);
								// ���������ʱ��
								String normalTime = "09:00";
								// �ٵ������ʱ��
								String latestTime = "11:00";
								// ��ȡ��״̬
								status = workUpStatus(workUpTime, normalTime, latestTime);
								if (status.equals("����")) {
									// ���߿ͻ��˴�̫���ˣ����ܴ�
									os.write("����".getBytes());
								} else {
									String sql3 = "insert into clock(employeeId,dayDate,workUp,workUpStatus) values"
											+ "('" + employeeId + "','" + dateNow + "','" + workUpTime + "','" + status
											+ "')";
									pstmt = conn.prepareStatement(sql3);
									int ret = pstmt.executeUpdate();
									if (ret > 0) {
										// ����Ϣ
										String clockInfo = "�򿨳ɹ�,״̬Ϊ:" + status + ",��ʱ��Ϊ:" + workUpTime;
										// ���߿ͻ��˴򿨳ɹ�
										os.write(clockInfo.getBytes());
									} else {
										// ���߿ͻ��˴�ʧ��
										os.write("��ʧ��".getBytes());
									}
								}
							}

						} else {
							os.write("��ʧ��".getBytes());
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						JdbcUtils.closeResultSet(rs);
						JdbcUtils.closeConnection(conn);
					}
				}
			}
		};
		new Thread(task).start();

	}
	
	// �°�򿨷���
		public void clientWorkDown(OutputStream os, String e_account) {
			Runnable task = new Runnable() {
				public void run() {
					Connection conn = JdbcUtils.getConnection();
					PreparedStatement pstmt = null;
					ResultSet rs = null;
					String employeeId = null;
					// ��״̬
					String status;
					// ��ʱ��
					String workDownTime;
					// �����°�ʱ��
					String normalTime = "18:00";
					// ���˿����ֽ�ʱ���
					String notWorkTime = "16:00";
					Date date = new Date();
					// ���ڸ�ʽ
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					// ��ʱ���ʽ
					SimpleDateFormat dateFormat2 = new SimpleDateFormat("HH:mm");
					// ��ȡ��ǰ����
					String dateNow = dateFormat.format(date);
					// ��ȡ��ǰʱ��
					workDownTime = dateFormat2.format(date);
					// �����ж�������û�д򿨣����û�У�����Ҫ��������,�°��ʱ��,�°��״̬
					String sql1 = "select * from clock where "
							+ "employeeId=(select employeeId from employee where employeeAccount=?)" + "and dayDate=?";
					try {
						if (isRest() == true) {
							os.write("��������Ϣ�գ�".getBytes());
						} else {
							pstmt = conn.prepareStatement(sql1);
							pstmt.setString(1, e_account);
							pstmt.setString(2, dateNow);
							rs = pstmt.executeQuery();
							// ����н������֤�������д򿨣����ж���û�д���°࿨��Ȼ����´򿨼�¼
							if (rs.next()) {
								String sql2 = "select workDown,workDownStatus from clock where "
										+ "employeeId=(select employeeId from employee where employeeAccount=?)"
										+ "and dayDate=?";
								pstmt = conn.prepareStatement(sql2);
								pstmt.setString(1, e_account);
								pstmt.setString(2, dateNow);
								rs = pstmt.executeQuery();
								// ����н����ʾ�Ѿ����й��°��
								if (rs.next() && rs.getString("workDown") != null) {
									String info = "�Ѿ�����°࿨,��ʱ��Ϊ:" + rs.getString("workDown")+",״̬Ϊ:"+rs.getString("workDownStatus");
									// ���߿ͻ����û��Ѿ��������
									os.write(info.getBytes());
								} else { // ��û���°࿨
									// ��ȡ��״̬
									status = workDownStatus(workDownTime, normalTime, notWorkTime);
									// ���´򿨼�¼������°��ʱ���״̬
									String sql3 = "update clock set workDown=?,workDownStatus=? "
											+ "where employeeId=(select employeeId from employee where employeeAccount=?)"
											+ "and dayDate=?";
									pstmt = conn.prepareStatement(sql3);
									pstmt.setString(1, workDownTime);
									pstmt.setString(2, status);
									pstmt.setString(3, e_account);
									pstmt.setString(4, dateNow);
									int ret = pstmt.executeUpdate();
									if (ret > 0) {
										String downInfo = "�°�򿨳ɹ�,��ʱ��Ϊ:" + workDownTime;
										os.write(downInfo.getBytes());
									} else {
										os.write("��ʧ��".getBytes());
									}
								}
							} else {
								if ((workDownTime.compareTo("11:00")) >= 0) {
									// ����û�д򿨣���Ҫ����򿨼�¼
									status = workDownStatus(workDownTime, normalTime, notWorkTime);
									// �Ȼ��Ա��id
									String sql4 = "select employeeId from employee where employeeAccount=?";
									String e_id = null;
									pstmt = conn.prepareStatement(sql4);
									pstmt.setString(1, e_account);
									rs = pstmt.executeQuery();
									if (rs.next()) {
										e_id = rs.getString("employeeId");
									}
									String sql5 = "insert into clock(employeeId,dayDate,workDown,workDownStatus) values(?,?,?,?)";
									pstmt = conn.prepareStatement(sql5);
									pstmt.setString(1, e_id);
									pstmt.setString(2, dateNow);
									pstmt.setString(3, workDownTime);
									pstmt.setString(4, status);
									int ret = pstmt.executeUpdate();
									if (ret > 0) {
										String downInfo = "�°�򿨳ɹ�,��ʱ��Ϊ:" + workDownTime;
										os.write(downInfo.getBytes());
									} else {
										os.write("��ʧ��".getBytes());
									}
								} else {

									os.write("ʮһ��ǰδ�����ϰ�򿨣����Ƚ����ϰ�򿨣�".getBytes());
								}
							}
						}
					} catch (SQLException | IOException e) {
						e.printStackTrace();
					} finally {
						// �ر����ݿ���Դ
						JdbcUtils.closeResultSet(rs);
						JdbcUtils.closeConnection(conn);
					}

				}
			};
			new Thread(task).start();
		}
		
		// �鿴���˿�����Ϣ����
		public void viewMyInfo(OutputStream os, String acceptInfo) {
			Runnable task = new  Runnable() {
				public void run() {
					//�����ݿ⽨������
					Connection conn = JdbcUtils.getConnection();
					//����Ԥ����ִ����
					PreparedStatement pstmt = null;
					//���������
					ResultSet rs = null;
					//�Ƚ��õ�����Ϣ���д������ʽΪ�˺�,��ѯ�·ݣ�
					String e_id = null;
					//��������
					String clockDay = null;
					//�ٵ�����
					String lateCount = null;
					//���˴���
					String earlyOutCount = null;
					//��������
					String notWorkCount = null;
					String[] arr = acceptInfo.split(",");
					String e_account = arr[0];
					String selecCond = arr[1];
					Date date = new Date();
					// ���ڸ�ʽ
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					//��ȡ��ǰ����
					String dateNow = dateFormat.format(date);
					String _dateNow = dateNow.substring(0, 7);
					//�����жϲ�ѯ���·��Ƿ�Ϊ��ǰ�·ݣ������ѯ������ļ�¼���������һ�����µ�
					/*
					 * �����������1����ѯ�·�Ϊ��ǰ�� ���鵱ǰ�¿�ʼ������Ϊֹ
					 * 		     ��2����ѯ�·ݴ��ڵ�ǰ�£����ش�����Ϣ
					 * 		     ��3����ѯ�·�С�ڵ�ǰ�£���ѯ������Ϣ
					 */
					
					//Ϊ��ǰ��
					if(selecCond.equals(_dateNow)) {
						//���Ȳ�һ��Ա����ID
						String idSql = "select employeeId from employee where employeeAccount=?";
						try {
							pstmt = conn.prepareStatement(idSql);
							pstmt.setString(1, e_account);
							rs = pstmt.executeQuery();
							if(rs.next()) {
								e_id = rs.getString("employeeId");
								//���id��ʼ���в�ѯ
								//{1}���Ȳ�ѯ�ٵ�����
								String lateSql = "select count(workUpStatus) late "
										+ "from (select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
										+ "from (select  * from worksheet   where dayDate like ? and  "
										+ "dayDate between '' and ?) wk  "
										+ "left join (select * from clock where employeeId=?) ck  on wk.dayDate = ck.dayDate) dk  "
										+ "where workUpStatus in ('�ٵ�') and workDownStatus in('����','����')";
								pstmt = conn.prepareStatement(lateSql);
								pstmt.setString(1, _dateNow+"%");
								pstmt.setString(2, dateNow);
								pstmt.setString(3, e_id);
								rs = pstmt.executeQuery();
								if(rs.next()) {
									//��óٵ�����
									lateCount = rs.getString("late");
								}else {
									os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
								}
								//{2}��ѯ���˴���
								String earlySql = "select count(workDownStatus) early from "
										+ "(select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus from "
										+ "(select  * from worksheet   where dayDate like ? and dayDate "
										+ "between '' and ?) wk  "
										+ "left join (select * from clock where employeeId=?) ck  on wk.dayDate = ck.dayDate) dk  "
										+ "where workDownStatus in ('����') and workUpStatus in('����','�ٵ�')";
								pstmt = conn.prepareStatement(earlySql);
								pstmt.setString(1, _dateNow+"%");
								pstmt.setString(2, dateNow);
								pstmt.setString(3, e_id);
								rs = pstmt.executeQuery();
								if(rs.next()) {
									//������˴���
									earlyOutCount = rs.getString("early");
								}else {
									os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
								}
								//{3}��ѯ��������
								String notWorlSql = "select count(ek.dayDate) ow "
										+ "from (select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
										+ "from (select  * from worksheet   where dayDate like ? "
										+ "and dayDate between '' and ?) wk  "
										+ "left join (select * from clock where employeeId=?) ck  "
										+ "on wk.dayDate = ck.dayDate order by wk.dayDate) ek where ek.employeeId is null "
										+ "or (ek.workUpStatus is null or ek.workUpStatus='����') "
										+ "or (ek.workDownStatus is null or ek.workDownStatus='����') "
										+ "or (ek.workUpStatus is null and ek.workDownStatus='����') "
										+ "or (ek.workUpStatus ='����' and ek.workDownStatus is null)";
														
								pstmt = conn.prepareStatement(notWorlSql);
								pstmt.setString(1, _dateNow+"%");
								pstmt.setString(2, dateNow);
								pstmt.setString(3, e_id);
								rs = pstmt.executeQuery();
								if(rs.next()) {
									//��ÿ�������
									notWorkCount = rs.getString("ow");
								}else {
									os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
								}
								//{4}��ѯ���ڴ���
								String clockDaySql = "select count(ek.dayDate) ow from "
										+ "(select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
										+ "from (select  * from worksheet   "
										+ "where dayDate like ? and dayDate between '' and ?) wk  "
										+ "left join (select * from clock where employeeId=?) ck  "
										+ "on wk.dayDate = ck.dayDate order by wk.dayDate) ek "
										+ "where  ek.employeeId is not null;";
														
								pstmt = conn.prepareStatement(clockDaySql);
								pstmt.setString(1, _dateNow+"%");
								pstmt.setString(2, dateNow);
								pstmt.setString(3, e_id);
								rs = pstmt.executeQuery();
								if(rs.next()) {
									//������˴���
									clockDay = rs.getString("ow");
								}else {
									os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
								}
								String allMyInfo = "��ѯ�ɹ�������:"+clockDay+"�� {�ٵ�:"+lateCount+" "
														+ "����:"+earlyOutCount+" "
														+ "����:"+notWorkCount+"}";
								
								
								//��ѯ���е���Ϣ
								String allSql = "select id,wk.dayDate,employeeId,workUp,"
										+ "workUpStatus,workDown,workDownStatus from (select  * from worksheet   "
										+ "where dayDate like ? and dayDate between '' and ?) wk  "
										+ "left join (select * from clock where employeeId=?) ck  "
										+ "on wk.dayDate = ck.dayDate order by wk.dayDate";
								pstmt = conn.prepareStatement(allSql);
								pstmt.setString(1, _dateNow+"%");
								pstmt.setString(2, dateNow);
								pstmt.setString(3, e_id);
								rs = pstmt.executeQuery();
								StringBuffer allInfo = new StringBuffer();
								int id = 1;
								while(rs.next()) {
									String dayDate = rs.getString("wk.dayDate");
									String employeeId = rs.getString("employeeId");
									String workUp = rs.getString("workUp");
									String workUpStatus = rs.getString("workUpStatus");
									String workDown = rs.getString("workDown");
									String workDownStatus = rs.getString("workDownStatus");
									String line = ","+id+"\t"+dayDate+"\t"+employeeId+"\t"+workUp+"\t"+workUpStatus
											+"\t"+workDown+"\t"+workDownStatus;
									id++;
									allInfo.append(line);
								}
								System.out.println(allInfo.length());
								String msg = allMyInfo+allInfo;
								
								os.write(msg.getBytes());
								
							}else {
								os.write("��ѯʧ�ܣ���Ա�������ڣ�".getBytes());
							}
						} catch (SQLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}finally {
							JdbcUtils.closeResultSet(rs);
							JdbcUtils.closeConnection(conn);
						}	
					}
					//���ڵ�ǰ��
					else if((selecCond.compareTo(_dateNow))>0) {
						try {
							os.write("��ѯ�·ݴ��ڵ�ǰ�£���ѯʧ�ܣ�".getBytes());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//С�ڵ�ǰ��
					else {
						//�ȿ���ѯ���·��ǲ��ǹ����·ݣ��ǲ�ִ�в�ѯ�����ǵĻ�����ʾ����
						String isWorkMonth = "select * from worksheet where dayDate like ?";
						try {
							pstmt = conn.prepareStatement(isWorkMonth);
							pstmt.setString(1, selecCond+"%");
							rs = pstmt.executeQuery();
							if(rs.next()) {
								//���Ȳ�һ��Ա����ID
								String idSql = "select employeeId from employee where employeeAccount=?";
									pstmt = conn.prepareStatement(idSql);
									pstmt.setString(1, e_account);
									rs = pstmt.executeQuery();
									if(rs.next()) {
										e_id = rs.getString("employeeId");
										//���id��ʼ���в�ѯ
										//{1}���Ȳ�ѯ�ٵ�����
										String lateSql = "select count(workUpStatus) late "
												+ "from (select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
												+ "from (select  * from worksheet   where dayDate like ?"
												+ ") wk  "
												+ "left join (select * from clock where employeeId=?) ck  on wk.dayDate = ck.dayDate) dk  "
												+ "where workUpStatus in ('�ٵ�') and workDownStatus in('����','����')";
										pstmt = conn.prepareStatement(lateSql);
										pstmt.setString(1, selecCond+"%");
										pstmt.setString(2, e_id);
										rs = pstmt.executeQuery();
										if(rs.next()) {
											//��óٵ�����
											lateCount = rs.getString("late");
										}else {
											os.write("��ѯʧ�ܣ���Ա�������ڣ�".getBytes());
										}
										//{2}��ѯ���˴���
										String earlySql = "select count(workDownStatus) early from "
												+ "(select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus from "
												+ "(select  * from worksheet   where dayDate like ?"
												+ ") wk  "
												+ "left join (select * from clock where employeeId=?) ck  on wk.dayDate = ck.dayDate) dk  "
												+ "where workDownStatus in ('����') and workUpStatus in('����','�ٵ�')";
										pstmt = conn.prepareStatement(earlySql);
										pstmt.setString(1, selecCond+"%");
										pstmt.setString(2, e_id);
										rs = pstmt.executeQuery();
										if(rs.next()) {
											//������˴���
											earlyOutCount = rs.getString("early");
										}else {
											os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
										}
										//{3}��ѯ��������
										String notWorlSql = "select count(ek.dayDate) ow "
												+ "from (select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
												+ "from (select  * from worksheet   where dayDate like ? "
												+ ") wk  "
												+ "left join (select * from clock where employeeId=?) ck  "
												+ "on wk.dayDate = ck.dayDate order by wk.dayDate) ek where ek.employeeId is null "
												+ "or (ek.workUpStatus is null or ek.workUpStatus='����') "
												+ "or (ek.workDownStatus is null or ek.workDownStatus='����') "
												+ "or (ek.workUpStatus is null and ek.workDownStatus='����') "
												+ "or (ek.workUpStatus ='����' and ek.workDownStatus is null)";
																
										pstmt = conn.prepareStatement(notWorlSql);
										pstmt.setString(1, selecCond+"%");
										pstmt.setString(2, e_id);
										rs = pstmt.executeQuery();
										if(rs.next()) {
											//��ÿ�������
											notWorkCount = rs.getString("ow");
										}else {
											os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
										}
										//{4}��ѯ���ڴ���
										String clockDaySql = "select count(ek.dayDate) ow from "
												+ "(select id,wk.dayDate,employeeId,workUp,workUpStatus,workDown,workDownStatus "
												+ "from (select  * from worksheet   "
												+ "where dayDate like ?) wk  "
												+ "left join (select * from clock where employeeId=?) ck  "
												+ "on wk.dayDate = ck.dayDate order by wk.dayDate) ek "
												+ "where  ek.employeeId is not null;";
																
										pstmt = conn.prepareStatement(clockDaySql);
										pstmt.setString(1, selecCond+"%");
										pstmt.setString(2, e_id);
										rs = pstmt.executeQuery();
										if(rs.next()) {
											//������˴���
											clockDay = rs.getString("ow");
										}else {
											os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
										}
										String allMyInfo = "��ѯ�ɹ�������:"+clockDay+"�� {�ٵ�:"+lateCount+" "
																+ "����:"+earlyOutCount+" "
																+ "����:"+notWorkCount+"}";
										
										//��ѯ���е���Ϣ
										String allSql = "select id,wk.dayDate,employeeId,workUp,"
												+ "workUpStatus,workDown,workDownStatus from (select  * from worksheet   "
												+ "where dayDate like ? and dayDate) wk  "
												+ "left join (select * from clock where employeeId=?) ck  "
												+ "on wk.dayDate = ck.dayDate order by wk.dayDate";
										pstmt = conn.prepareStatement(allSql);
										pstmt.setString(1, selecCond+"%");
										pstmt.setString(2, e_id);
										rs = pstmt.executeQuery();
										StringBuffer allInfo = new StringBuffer();
										int id = 1;
										while(rs.next()) {
											String dayDate = rs.getString("wk.dayDate");
											String employeeId = rs.getString("employeeId");
											String workUp = rs.getString("workUp");
											String workUpStatus = rs.getString("workUpStatus");
											String workDown = rs.getString("workDown");
											String workDownStatus = rs.getString("workDownStatus");
											String line = ","+id+"\t"+dayDate+"\t"+employeeId+"\t"+workUp+"\t"+workUpStatus
															+"\t"+workDown+"\t"+workDownStatus;
											id++;
											allInfo.append(line);
										}
//										System.out.println(allInfo.length());
										String msg = allMyInfo+allInfo;
										
										os.write(msg.getBytes());
										
									}else {
										os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
									}
							}else {
								os.write("��ѯ�·ݲ��ǹ����£���ѯʧ�ܣ�".getBytes());
							}
						} catch (SQLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
				}
			};
			new Thread(task).start();
		}
	
		// �����ж��ϰ��״̬ 9:00
		public String workUpStatus(String workUpTime, String normalTime, String latestTime) {
			String status = null;
			// ���д�ʱ��ĶԱȣ�
			int res = workUpTime.compareTo(normalTime);
			// �������ָ����ʱ�����ǰ��Сʱ
			if (res == 0 || (res > -2 && res < 0)) {
				status = "����";
			} else if (res < -2) { // �ߵ�ǰ��̫��//�ܾ�
				status = "����";
			} else if (res > 0) {
				res = workUpTime.compareTo(latestTime);
				if (res <= 0) {
					status = "�ٵ�";
				} else {
					status = "����";
				}
			}
			return status;
		}
		
		// �����ж��°��״̬
		public String workDownStatus(String workDownTime, String normalTime, String notWorkTime) {
			String status = null;
			// ���д�ʱ��ĶԱȣ�
			int res = workDownTime.compareTo(normalTime);
			// ����°��ʱ����ڻ���������°�ʱ�䣬����Ϊ������
			if (res == 0 || res > 0) {
				status = "����";
			}
			// ���С��0����֤��Ա�������˻�������ɣ������ж�
			else if (res < 0) {
				// �����˿����ֽ����жԱ�
				res = workDownTime.compareTo(notWorkTime);
				// ���������Ϊ����
				if (res > 0) {
					status = "����";
				} else {// ���С����Ϊ����
					status = "����";
				}
			}
			return status;
		}
	
		// �ж��ǲ�����Ϣ��
		public boolean isRest() {
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String currDate = dateFormat.format(date);
			Connection conn = JdbcUtils.getConnection();
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String sql = "select * from worksheet where dayDate=?";
			try {
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, currDate);
				rs = pstmt.executeQuery();
				if (rs.next()) { // �д�������Ϣ��
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.closeResultSet(rs);
				JdbcUtils.closeConnection(conn);
			}
			return true;
		}
}
