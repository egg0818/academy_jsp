package com.koreait.pjt.user;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.koreait.pjt.MyUtils;
import com.koreait.pjt.ViewResolver;
import com.koreait.pjt.db.UserDAO;
import com.koreait.pjt.vo.UserVO;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@WebServlet("/profile")
public class ProfileSer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
   

	//프로필 화면 (나의 프로필 이미지, 이미지 변경 가능한 화면)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserVO loginUser = MyUtils.getLoginUser(request);
		request.setAttribute("data", UserDAO.selUser(loginUser.getI_user()));
		ViewResolver.foward("user/profile", request, response);
	}

	//이미지 변경처리
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String savePath = request.getSession().getServletContext().getRealPath("img");
// 		getRealPath : 절대 주솟값을 연결해준다
		
		UserVO loginUser = MyUtils.getLoginUser(request);
		
		String savePath = getServletContext().getRealPath("img") + "/user/" + loginUser.getI_user();
		System.out.println("savePath : " + savePath);
		
		File directory = new File(savePath);
		if(!directory.exists()) {
			directory.mkdirs();
			// dir 말고 dirs 를 쓰자
		}
		
		int maxFileSize = 10_485_760; // 1024 * 1024 * 10 (10mb) // 최대 파일 사이즈 크기
		// fileNm 정책에의해서 가져오는 파일명
		String fileNm = "";
		String originFileNm = "";
		
		try {
			MultipartRequest mr = new MultipartRequest(request, savePath,
					 maxFileSize, "UTF-8", new DefaultFileRenamePolicy());
			
			Enumeration files = mr.getFileNames();
			
			while(files.hasMoreElements()) {
				// 키 값이 넘어감
				String key = (String) files.nextElement();
				fileNm = mr.getFilesystemName(key);
				originFileNm = mr.getOriginalFileName(key);
				
				System.out.println("key : " + key);
				System.out.println("fileNm : " + fileNm);
				System.out.println("originFileNm : " + originFileNm);
				
				File oldFile = new File(savePath + "/" + fileNm);
				File newFile = new File(savePath + "/" + UUID.randomUUID() + ".jpg");
				
				oldFile.renameTo(newFile);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}