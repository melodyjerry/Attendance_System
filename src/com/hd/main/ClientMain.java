package com.hd.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import com.hd.bean.Admin;
import com.hd.bean.Employee;
import com.hd.network.NetWork;
import com.hd.service.AdminService;
import com.hd.service.GeneralService;
import com.hd.service.UserService;

import message.Base;

/*
 * �ͻ���
 * 1.ʹ��network���ӷ�����(����һ��socket)
 * 2.���ӳɹ������ӷ������ϵ����ݿ�
 * 3.Ȼ��ִ��������
 * 4.ѡ�����
 * 5.��¼   ---->Generally.login()---->��������з��������������Ϣ---->����������---->�������������ݿ�ִ�в�ѯ---->���ؽ��
 * 6.��������ѡ��
 * 7.������һҳ
 * 8.�˳�
 */

//�������п���ϵͳ������
public class ClientMain extends Base {
	private NetWork netWork;
	private Boolean status = false;
	private String serverIp = "127.0.0.1";
	private int serverPort = 6666;
	private String socketKey = serverIp + ":" + serverPort;

	// ������
	public static void main(String[] args) {
		ClientMain clientMain = new ClientMain();
		clientMain.start();
	}

	// ���������ͻ��˵ķ���
	public void start() {
		// ����һ�����繤����
		netWork = new NetWork(this);
		// ���ӵ�������
		netWork.connectServer(serverIp, serverPort);

	}

	// �Զ����ӡ����
	public void print(String str) {
		System.out.println("[YTG]" + str);
	}

	@Override
	public void onConnecteServer(boolean ret, String myScoketId) {
		String info = ret ? "���ӳɹ�" : "����ʧ��";
		if (info.equals("���ӳɹ�")) {
			print("������" + info);
			this.socketKey = myScoketId;
			this.status = true;
			// ��������
			program(status);
		} else {
			print("����������ʧ�ܣ��޷�����ϵͳ��");
		}
	}

	public void program(boolean status) {
		// ���ڼ�¼����
		String _number;
		int number;
		Employee employee = null;
		Admin admin = null;
		// ����һ��ͨ��Dao����,����ִ����ͨԱ�������Ա��ͨ�÷���
		GeneralService generalDao = new GeneralService(netWork);
		// ����һ��UserDao����,����ִ����ͨԱ���ķ���
		UserService userDao = new UserService(netWork, this);
		// ����һ��AdminDao����,����ִ�й���Ա�ķ���
		AdminService adminDao = new AdminService(netWork, this);
		boolean isRun = false;
		if (status == true) {
			isRun = true;
			while (isRun) {
				// �����ǿ���̨����
				Scanner sc = new Scanner(System.in);
				String account;
				String pwd;
				System.out.println("[��ӭ����YTG���¿���ϵͳ]");
				System.out.println("---------------------------------------------------- ");
				print("��ѡ�������ݽ��е�¼:1.��ͨԱ�� 2.����Ա(�������ֽ���ѡ��)3.�˳�");
				_number = sc.nextLine();
				number = Integer.valueOf(_number);
				if(number==3) {
					break;
				}
				print("����������˺�:");
				account = sc.nextLine();
				print("�������������:");
				pwd = sc.nextLine();

				// ��ͨԱ����ݵ�¼
				if (number == 1) {
					// ����ͨ��Dao�ķ�����Ʒ������е�¼������¼�ɹ�����һ��Ա������
					employee = (Employee) generalDao.login(Employee.class, account, pwd, 1, socketKey);
					if (employee != null) {
						print("��¼�ɹ���");
						boolean isEmpLogin = true;
						System.out.println("----------------------------------------------------");
						while (isEmpLogin) {
							Date date = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							String nowDate = dateFormat.format(date);
							print("������" + nowDate);
							print("����й���ѡ��:");
							print("1.�ϰ��");
							print("2.�°��");
							print("3.�鿴�ҵĿ��ڼ�¼");
							print("4.�˳�");
							_number = sc.nextLine();
							number = Integer.valueOf(_number);
							switch (number) {
							case 1:
								// ִ��Ա���ϰ�򿨷���
								userDao.workUp(employee, socketKey);
								print("��������������ز˵���!");
								sc.nextLine();
								break;
							case 2:
								// ִ��Ա���°�򿨷���
								userDao.workDown(employee, socketKey);
								print("��������������ز˵���!");
								sc.nextLine();
								break;
							case 3:
								// ִ��Ա���鿴�Լ��Ŀ�����Ϣ�취
								print("��������Ҫ��ѯ���·�(���ո�ʽ2019-01)��");
								String selectDate = sc.nextLine();
								userDao.viewMyInfo(selectDate,employee, socketKey);
								print("��������������ز˵���!");
								sc.nextLine();
								break;
							case 4:
								// ִ��Ա���˳�ϵͳ����
//								userDao.exit();
//								print("��������������ز˵���!");
//								sc.nextLine();
								isRun = false;
								isEmpLogin = false;
								break;
							default:
								print("����Ƿ�,���������룡");
								break;
							}
						}
					} else {
						print("�˺Ż����������,��¼ʧ�ܣ�");
					}
				} else { // ����Ա��ݵ�¼
					admin = (Admin) generalDao.login(Admin.class, account, pwd, 2, socketKey);
					if (admin != null) {
						print("��¼�ɹ���");
						boolean isAmLogin = true;
						System.out.println("---------------------------------------");
						while (isAmLogin) {
							Date _nowdate = new Date();
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
							String nowDate = dateFormat.format(_nowdate);
							print("������" + nowDate);
							print("����й���ѡ��:");
							print("1.���Ա����Ϣ");
							print("2.�鿴����Ա������ͳ��");
							print("3.�˳�");
							_number = sc.nextLine();
							number = Integer.valueOf(_number);
							switch (number) {
							case 1:
								print("������Ա����:");
								String name = sc.nextLine();
								print("������Ա�����:");
								String employeeId = sc.nextLine();
								print("������Ա����¼�˺�:");
								String employeeAccount = sc.nextLine();
								print("������Ա����¼����:");
								String employeePwd = sc.nextLine();
								print("������Ա������ְλ:");
								String position = sc.nextLine();
								print("������Ա��н��:");
								String salary = sc.nextLine();
								print("������Ա����ְʱ��[����2019-01-01���ָ�ʽ]:");
								String workDate = sc.nextLine();
								print("������Ա�����[��ͨԱ��\\����Ա]:");
								String capacity = sc.nextLine();

								// ����һ����Ա��
								Employee newEmployee = new Employee(employeeId, name, employeeAccount, employeePwd,
										salary, workDate, capacity, position);
								// �������Ա������
								adminDao.addEmployee(newEmployee, socketKey);
								break;
							case 2:
								print("�������ѯԱ������:");
								String e_name = sc.nextLine();
								print("�������ѯ�·�(����2019-01��ʽ):");
								String month = sc.nextLine();
								// ���ò鿴ȫ��Ա������ͳ�Ʒ���
								adminDao.viewAllInfo(e_name,month,socketKey);
								break;
							case 3:
								// �����˳�����------>�˴���дΪͨ��Dao
								isRun = false;
								isAmLogin = false;
								break;
							default:
								System.out.println("����Ƿ�,���������룡");
								break;
							}
						}
					} else {
						print("�˺Ż����������,��¼ʧ�ܣ�");
					}
				}
			}
		}
	}

	// �򿪳ɹ�����Ϣ����
	@Override
	public void workUpSuccess(boolean ret, String info) {
		if (ret == true) {
			print(info);
		}
	}

	// ��ʧ�ܵ���Ϣ����
	@Override
	public void workUpOnFailed(String str) {
		if (str.equals("��Ϣ��")) {
			print("������" + str);
		} else if (str.contains("�Ѿ������")) {
			print(str);
		} else if (str.equals("����")) {
			print("��ʱ��" + str + ",�����ߵ�֮���!");
		} else if (str.equals("��ʧ��")) {
			print("���ִ���," + str);
		} else if (str.contains("�°��")) {
			print(str);
		}
	}

	@Override
	public void workDownSuccess(boolean ret, String info) {
		if (ret == true) {
			print(info);
		}
	}

	@Override
	public void workDownOnFailed(String str) {
		print(str);
	}

	@Override
	public void addEmployeeSuccess(String str) {
		print(str);
	}

	@Override
	public void addEmployeeFalied(String str) {
		print(str);
	}

}
