package com.hd.dao;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hd.database.JdbcUtils;

public class AdminDao {
	// �ͻ���ִ�����Ա��
		public void addEmployee(OutputStream os, String e_info) {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					// �����ݿ⽨������
					Connection conn = JdbcUtils.getConnection();
					// ����Ԥ����ִ����
					PreparedStatement pstmt = null;
					// ���������
					ResultSet rs = null;
					// �����ֶ�ֵ����Map������
					HashMap<String, String> map = spiltEmplyeeInfo(e_info);
					System.out.println(map.get(" employeeAccount"));

					// ���Ȳ�ѯid�ǲ��ظ�����¼�˺��Ƿ��ظ�
					String idSelectSql = "select * from employee where employeeId=?";
					try {
						pstmt = conn.prepareStatement(idSelectSql);
						pstmt.setString(1, map.get("employeeId"));
						rs = pstmt.executeQuery();
						// �в�ѯ���
						if (rs.next()) {
							String info = "��Ա��id�Ѿ���ʹ�ã������Ա��id!";
							os.write(info.getBytes());
						} else { // û�в�ѯ����������idû��ʹ��
							// �����ж�Ա���˺��Ƿ�ʹ��
							String accounSelecttSql = "select * from employee where employeeAccount=?";
							pstmt = conn.prepareStatement(accounSelecttSql);
							pstmt.setString(1, map.get(" employeeAccount"));
							rs = pstmt.executeQuery();
							if (rs.next()) {
								String info = "��Ա���˺��Ѿ���ʹ�ã������Ա���˺�!";
								os.write(info.getBytes());
							} else {
								// �����ͨԱ��
								if (map.get(" capacity").equals("��ͨԱ��")) {
									String insertSql = "insert into employee values(?,?,?,?,?,?,?,?)";
									pstmt = conn.prepareStatement(insertSql);
									pstmt.setString(1, map.get("employeeId"));
									pstmt.setString(2, map.get(" employeeName"));
									pstmt.setString(3, map.get(" employeeAccount"));
									pstmt.setString(4, map.get(" employeePwd"));
									pstmt.setString(5, map.get(" position"));
									pstmt.setString(6, map.get(" salary"));
									pstmt.setString(7, map.get(" workDate"));
									pstmt.setString(8, map.get(" capacity"));
									int ret = pstmt.executeUpdate();
									if (ret > 0) {
										String addInfo = "���Ա���ɹ���";
										os.write(addInfo.getBytes());
									} else {
										os.write("���ʧ��".getBytes());
									}
								} else {// ��ӹ���Ա������Ҫ��Ա����͹���Ա���ж������Ϣ
									String employeeSql = "insert into employee values(?,?,?,?,?,?,?,?)";
									pstmt = conn.prepareStatement(employeeSql);
									pstmt.setString(1, map.get("employeeId"));
									pstmt.setString(2, map.get(" employeeName"));
									pstmt.setString(3, map.get(" employeeAccount"));
									pstmt.setString(4, map.get(" employeePwd"));
									pstmt.setString(5, map.get(" position"));
									pstmt.setString(6, map.get(" salary"));
									pstmt.setString(7, map.get(" workDate"));
									pstmt.setString(8, map.get(" capacity"));
									int ret1 = pstmt.executeUpdate();
									if (ret1 > 0) {
										System.out.println("���Ա����ɹ�");
										String adminSql = "insert into admin values(?,?,?)";
										pstmt = conn.prepareStatement(adminSql);
										pstmt.setString(1, map.get("employeeId"));
										pstmt.setString(2, map.get(" employeeName"));
										pstmt.setString(3, map.get(" employeePwd"));
										int ret2 = pstmt.executeUpdate();
										if (ret2 > 0) {
											String addInfo = "��ӹ���Ա�ɹ���";
											os.write(addInfo.getBytes());
										} else {
											os.write("��ӹ���Աʧ�ܣ�".getBytes());
										}
									} else {
										os.write("���Ա��ʧ��".getBytes());
									}
								}

							}
						}

					} catch (SQLException e) {
						String errorInfo = "���ݸ�ʽ�д��󣬴�����Ϣ��" + e.getMessage();
						try {
							os.write(errorInfo.getBytes());
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						JdbcUtils.closeResultSet(rs);
						JdbcUtils.closeConnection(conn);
					}

				}
			};
			new Thread(task).start();

		}
		
		// �ͻ���ִ�в鿴����Ա������ͳ��
		public void viewAllInfo(OutputStream os,String acceptInfo) {
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
					String e_name = arr[0];
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
						String idSql = "select employeeId from employee where employeeName=?";
						try {
							pstmt = conn.prepareStatement(idSql);
							pstmt.setString(1, e_name);
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
								String allMyInfo = "��ѯ�ɹ���"
										+ "["+e_name+"]����:"+clockDay+"�� {�ٵ�:"+lateCount+" "
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
								String idSql = "select employeeId from employee where employeeName=?";
									pstmt = conn.prepareStatement(idSql);
									pstmt.setString(1, e_name);
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
											os.write("��ѯʧ�ܣ���ȡ�û���Ϣʱ����".getBytes());
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
										System.out.println(allInfo.length());
										String msg = allMyInfo+allInfo;
										
										os.write(msg.getBytes());
										
									}else {
										os.write("��ѯʧ�ܣ���Ա�������ڣ�".getBytes());
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
		
		public HashMap<String, String> spiltEmplyeeInfo(String e_info) {
			HashMap<String, String> map = new HashMap<String, String>();
			String str = null;
			// System.out.println(e_info);
			// ��1����ȡ���������ڵ�����
			Pattern pattern = Pattern.compile("(\\[[^\\]]*\\])");
			Matcher matcher = pattern.matcher(e_info);
			while (matcher.find()) {
				str = matcher.group().substring(1, matcher.group().length() - 1);
			}
			// System.out.println(str);
			// ��2��Ȼ�������ŵ����ݰ����ŷָ�
			String[] str2 = str.split(",");
			// �Լ�ֵ����ʽ�洢
			for (String string : str2) {
				// ��3��Ȼ���ա�=���ָ�
				String[] str3 = string.split("=");
				map.put(str3[0], str3[1]);
			}
			// System.out.println(map);
			return map;
		}
}
