package com.hd.dao;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hd.database.JdbcUtils;

public class GenerallyDao {
	// ��¼����
		public void clientLogin(OutputStream os, String accountInfo) {
			Runnable task = new Runnable() {
				@Override
				public void run() {
					// �������ݿ�
					Connection conn = JdbcUtils.getConnection();
					// ����Ԥ������
					PreparedStatement pstmt = null;
					// ���������
					ResultSet rs = null;

					// ʹ���ַ����ָ��õ�¼�û�����ݡ��˺š�����
					String[] arr1 = accountInfo.split("]");
					String[] arr2 = arr1[0].split("\\[");
					String[] arr3 = arr1[1].split(",");
					// �û����
					String capacity = arr2[1];
					// �û��˺�
					String account = arr3[0];
					// �û�����
					String pwd = arr3[1];
					// ����sql����Ӧ��ͬ����û�ִ�е�¼����
					String[] sqls = { "select * from employee where employeeAccount=? and employeePwd=?",
							"select * from admin where adminId=? and adminPwd=?" };

					// ��ͨԱ��ִ��sqls[0]
					if (capacity.equals("��ͨԱ��")) {
						try {
							// ʵ��Ԥ������
							pstmt = conn.prepareStatement(sqls[0]);
							// ͨ�����ֵ
							pstmt.setString(1, account);
							pstmt.setString(2, pwd);
							// ��ȡ�����
							rs = pstmt.executeQuery();
							// ����н���������˺�������ȷ
							if (rs.next()) {
								// ���߿ͻ������¼�ɹ�
								os.write("��¼�ɹ�".getBytes());
							} else {
								// ���߿ͻ������¼ʧ��
								os.write("��¼ʧ��".getBytes());
							}
						} catch (SQLException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							JdbcUtils.closeResultSet(rs);
							JdbcUtils.closeConnection(conn);
						}

					} else {
						try {
							// ʵ��Ԥ������
							pstmt = conn.prepareStatement(sqls[1]);
							// ͨ�����ֵ
							pstmt.setString(1, account);
							pstmt.setString(2, pwd);
							// ��ȡ�����
							rs = pstmt.executeQuery();
							// ����н���������˺�������ȷ
							if (rs.next()) {
								// ���߿ͻ������¼�ɹ�
								os.write("��¼�ɹ�".getBytes());
							} else {
								// ���߿ͻ������¼ʧ��
								os.write("��¼ʧ��".getBytes());
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
}
