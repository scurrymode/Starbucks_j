package client;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import db.DBManager;
import dto.Member;
import dto.Product;

public class ClientMain extends JFrame implements ActionListener{
	JPanel p_center;
	JButton bt_orders, bt_myPage, bt_event, bt_card;
	Socket socket;
	int port = 7777;
	String host = "211.238.142.118";
	BufferedReader buffr;
	BufferedWriter buffw;
	//아예 처음 킬때 회원정보랑 싹 다 가져올거다!
	DBManager manager = DBManager.getInstance();
	Connection con;
	String login_id;
	Member member;
	Vector<Product> product_list = new Vector<Product>();
	
	
	public ClientMain(String login_id) {
		this.login_id=login_id;
		p_center = new  JPanel();
		bt_orders = new JButton("주문");
		bt_myPage = new JButton("마이페이지");
		bt_event = new JButton("이벤트");
		bt_card = new JButton("결제수단");
		
		p_center.setLayout(new GridLayout(2, 2));	
		p_center.add(bt_orders);
		p_center.add(bt_myPage);
		p_center.add(bt_event);
		p_center.add(bt_card);
		
		bt_orders.addActionListener(this);
		
		add(p_center);
		
		//소켓연결 및 스트림 꼽아주기!
		init();
		//각종 데이터 다 가져오기(상품, 회원)
		getData();
		
		setSize(300, 400);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
	}
	
	public void init(){
		try {
			socket = new Socket(host, port);
			buffr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
		
	public void getData(){
		con = manager.getConnection();

		getMember();
		getProduct();
	}
	
	public void getMember(){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql ="select * from member where member_login_id='"+login_id+"'";
		
		System.out.println(sql);
		
		try {
			pstmt=con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			rs.next();
			
			member = new Member();
			member.setMember_id(rs.getInt(1));
			member.setMember_login_id(rs.getString(2));
			member.setMember_login_pw(rs.getString(3));
			member.setMember_name(rs.getString(4));
			member.setMember_nickname(rs.getString(5));
			member.setMember_gender(rs.getString(6));
			member.setMember_phone(rs.getString(7));
			member.setMember_birth(rs.getString(8));
			member.setMember_coupon(rs.getString(9));
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void getProduct(){
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql ="select * from product";
		
		try {
			pstmt = con.prepareStatement(sql);
			rs=pstmt.executeQuery();
			
			while(rs.next()){
				Product dto = new Product();
				dto.setProduct_id(rs.getInt(1));
				dto.setProduct_category_id(rs.getInt(2));
				dto.setProduct_name(rs.getString(3));
				dto.setProduct_price(rs.getInt(4));
				
				product_list.add(dto);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}		
	}
		
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj==bt_orders){
			ClientOrders orders=new ClientOrders(this);
		}
	}

	public static void main(String[] args) {
		new ClientMain("hi");

	}

}
