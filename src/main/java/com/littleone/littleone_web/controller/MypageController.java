package com.littleone.littleone_web.controller;


import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.repository.query.parser.Part.Type;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.littleone.littleone_web.domain.Corporation;
import com.littleone.littleone_web.domain.Member;
import com.littleone.littleone_web.domain.Member_group;
import com.littleone.littleone_web.domain.Member_mng;
import com.littleone.littleone_web.domain.Notify_email;
import com.littleone.littleone_web.domain.Shipping_address;
import com.littleone.littleone_web.domain.Thumbnail;
import com.littleone.littleone_web.service.AmazonS3Service;
import com.littleone.littleone_web.service.CorporationService;
import com.littleone.littleone_web.service.GroupService;
import com.littleone.littleone_web.service.MailSenderService;
import com.littleone.littleone_web.service.MemberMngService;
import com.littleone.littleone_web.service.MemberService;
import com.littleone.littleone_web.service.RedisSessionService;
import com.littleone.littleone_web.service.ShippingAddressService;
import com.littleone.littleone_web.service.ThumbnailService;

import net.coobird.thumbnailator.Thumbnails;
import software.amazon.ion.IonException;

@Controller
public class MypageController {

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private MemberMngService mngService;

	@Autowired
	private CorporationService corpService;

	@Autowired
	private ThumbnailService thumbnailService;

	@Autowired
	private RedisSessionService sessionService;

	@Autowired
	private AmazonS3Service amazonS3Service;

	@Autowired
	private ShippingAddressService addressService;

	@Autowired
	private MailSenderService mailService;

	@Autowired
	private GroupService groupService;

	private int upload_count = 0;

	String pw_renew_date;

	/*
	@Autowired
	private StorageService storageService;
	 */

	@GetMapping("/mypage")	// 기본 마이페이지 뷰로 이동
	public String myPage(HttpSession session, Model model) {
		int idx = (int) session.getAttribute("idx");
		String session_type = (String) session.getAttribute("session_type");
		if(session_type != null) {
			Member member = memberService.findByIdx(idx);
			Member_mng mng = mngService.findOne(idx);
			if(member == null) {
				return "sign/sign_in";	// 로그인페이지로 이동
			}
			model.addAttribute("name", member.getName());
			model.addAttribute("email", member.getEmail());
			model.addAttribute("point", mng.getPoint());
			model.addAttribute("grade", mng.getGrade());

			if(session_type.equals("corp")) {
				char approval = corpService.findByIdx((int)session.getAttribute("idx")).getApproval();
				if(approval == 'n') {
					model.addAttribute("approval", 'n');
				}	
			}

			String thumbnail_url = member.getThumbnail();
			if(thumbnail_url != null && thumbnail_url.length() > 0) {
				model.addAttribute("thumbnail", member.getThumbnail());
			}

		} else {
			return "sign/sign_in";	// 로그인페이지로 이동
		}
		return "mypage/mypage_view";
	}

	@GetMapping("/mypage/modify_info")
	public String get_modify_info_auth(HttpSession session, Model model) {	// 1. 마이페이지에서 회원정보 수정을 클릭했을때, 비밀번호 인증페이지로 단순 이동
		if(session.getAttribute("session_type") != null) {
			String session_type = (String) session.getAttribute("session_type");
			int idx = (int) session.getAttribute("idx");
			if(session_type.equals("personal_s")) {	// 소셜회원이면 비밀번호 인증페이지 생략
				Member member = memberService.findByIdx(idx);
				model.addAttribute("member", member);
				// firebase locale setting
				model.addAttribute("locale", member.getCountry_code());
				return "mypage/mypage_modify";
			} else {
				return "mypage/mypage_pw_check";	// 비밀번호 인증페이지로 이동
			}
		} else {
			return "error";
		}	
	}

	// 소셜 회원은 통하지 않는 경로
	@PostMapping("/mypage/modify_info/auth")
	public String get_modify_info(@RequestParam("password") String password, HttpSession session, Model model, HttpServletRequest request) {	// 2. 비밀번호 인증 후, 인증 성공시 회원정보 수정 폼으로 이동
		// 1.세션에 저장된 회원의 이메일을 찾은후, 비밀번호가 맞는지 인증처리 후 회원정보수정폼으로 이동
		String type = (String) session.getAttribute("session_type");
		String email = (String) session.getAttribute("session_email");
		int idx = (int) session.getAttribute("idx");
		Member member = memberService.auth(email, password);
		if(member != null) {
			if(type.equals("personal")) {
				Thumbnail thumb = thumbnailService.findByCategory(idx, '1');
				model.addAttribute("member", member);
				model.addAttribute("original_filename", thumb.getOriginal_filename());
				
				// firebase locale setting
				model.addAttribute("locale", member.getCountry_code());
				
				return "mypage/mypage_modify";	
			} else if(type.equals("corp")) {
				Corporation corp = corpService.findByIdx(idx);
				Thumbnail thumb = thumbnailService.findByCategory(idx, '1');
				model.addAttribute("member", member);
				model.addAttribute("corp", corp);
				model.addAttribute("original_filename", thumb.getOriginal_filename());
				return "mypage/mypage_enterprise_modify";		// 기업회원의 정보수정 뷰로 추후 변경
			} else if(type.equals("admin")){
				// 관리자 회원 타입
				return "error";
			} else {
				return "error";
			}

		} else {
			// 인증 실패
			model.addAttribute("password", password);
			model.addAttribute("auth_failed", "true");
			return "mypage/mypage_pw_check";	
		}

	}

	@ResponseBody
	@GetMapping("/mypage/nickname_check/{nickname}")
	public String mypage_nickname_check(@PathVariable("nickname") String nickname, HttpSession session) {
		int member_idx = (int)session.getAttribute("idx");
		int nickname_valid_result = memberService.checkNickname_mypage(nickname, member_idx);
		if(nickname_valid_result != 0) {
			if(nickname_valid_result == 1) {
				return "nickname_dup"; 
			} else if(nickname_valid_result == 2) {
				return "nickname_size"; 
			} else if(nickname_valid_result == 3) {
				return "nickname_special"; 
			}
			return "error";
		} else {
			// unique값
			return "success";
		}
	}
	// 개인회원 정보 수정
	@ResponseBody
	@PostMapping("/mypage/modify_info")
	public String modify_info(@RequestParam("nickname") String nickname, @RequestParam("name") String name, @RequestParam("birth") String birth,
			@RequestParam("gender") char gender, @RequestParam(value="phone") String phone,
			@RequestParam(value="thumbnail", required=false) MultipartFile thumbnail, @RequestParam(name="originName", required=false) String original_file_name,
			HttpSession session) throws IOException, SQLException { // 3. 폼에서 수정할 정보 입력후 제출했을 때

		int member_idx = (int)session.getAttribute("idx");
		String thumbnail_url = "";
		String original_thumbnail = memberService.findByIdx(member_idx).getThumbnail();

		// 입력값 검증

		// 닉네임 한글 1~8자, 영문 4~12자 띄어쓰기 불가, _를 제외한 특수문자 불가, 비속어 필터링
		int nickname_valid_result = memberService.checkNickname_mypage(nickname, member_idx);
		if(nickname_valid_result != 0) {
			if(nickname_valid_result == 1) {
				return "nickname_dup"; 
			} else if(nickname_valid_result == 2) { 
				return "nickname_size"; 
			} else if(nickname_valid_result == 3) {
				return "nickname_special"; 
			}
		}

		if(thumbnail != null && original_file_name != null && original_file_name.length() > 0) {	// 썸네일이 첨부되었으면
			String extension = original_file_name.substring(original_file_name.indexOf('.') + 1).toLowerCase();
			String file_name = memberService.getUniqFileName(original_file_name);

			int file_size = (int) thumbnail.getSize();	// type casting 더 알아볼것. long으로 할지?

			if(memberService.checkFileName(original_file_name) == false) {	// 파일명 체크
				return "file_type_error";
			}
			if(file_size > 5000000) {
				return "file_size_error";
			}
			if(original_file_name.getBytes().length > 50) {
				return "file_name_size_error";
			}

			InputStream input = null;
			BufferedImage bufferedImage = null;
			try {
				input = thumbnail.getInputStream();
				bufferedImage = ImageIO.read(input);
				//bufferedImage = Thumbnails.of(image).size(200, 200).outputQuality(1.0f).asBufferedImage();	// 업로드된 썸네일 이미지 크롭

				// aws s3에 upload전 기존 파일 삭제
				if(original_thumbnail != null && original_thumbnail.length() > 0) {	// 기존에 썸네일이 설정된 유저이면
					String filename = thumbnailService.find(thumbnailService.getIdx(member_idx, '1')).getServer_filename();	// DB에서 서버에 저장된 파일명을 가져옴
					amazonS3Service.deleteThumbnail("littleone", filename);		// 찾은 파일명으로 aws s3에서 해당 파일을 지움
				}
				// aws s3에 thumbnail object upload.
				amazonS3Service.putThumbnailBI("thumbnail", file_name, extension, bufferedImage);	// put object to amazon s3
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "file_exception_error";
			} finally {
				if(input != null) {
					input.close();
					input = null;
				}
				if(bufferedImage != null) {
					bufferedImage.flush();
					bufferedImage = null;
				}
			}

			// s3에 정상적으로 업로드된 경우에만 db update 할것
			Thumbnail tn = thumbnailService.findByCategory(member_idx, '1');
			if(tn != null) {
				thumbnailService.update(tn.getIdx(), original_file_name, file_name, file_size);		// thumbnail db_update
			} else {
				tn = new Thumbnail();
				tn.setCategory('1');
				tn.setFile_size(file_size);
				tn.setMember_idx(member_idx);
				tn.setOriginal_filename(original_file_name);
				tn.setServer_filename(file_name);
				thumbnailService.insert(tn);
			}

			thumbnail_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/thumbnail/" + file_name;
			session.setAttribute("session_thumbnail", thumbnail_url);
		} else {
			thumbnail_url = original_thumbnail;
		}

		// 4. 첫단계에서 비밀번호 입력으로 인증이 성공한 상태이므로, DB에 회원정보 수정
		int result = memberService.update_info(member_idx, name, nickname, birth, gender, phone, thumbnail_url);
		
		if(result == 1) {
			// 주소정보가 null이 아니라 update 되었다면, Shipping_address에 기본 주소로 컬럼 추가.
			Member member = memberService.findByIdx(member_idx);
			session.setAttribute("nickname", member.getNickname());
			
			if(member.getAddress1() != null && member.getAddress1().length() > 0 && member.getZipcode() != null && member.getZipcode().length() > 0) {
				// 조건 추가. 만약 shipping_address 컬럼에 해당 회원에 대한 행이 없으면
				if(addressService.findByMember_idx(member_idx).size() == 0) {
					Shipping_address address = new Shipping_address();
					address.setMember_idx(member_idx);
					address.setAddress1(member.getAddress1());
					address.setAddress2(member.getAddress2());
					address.setZipcode(member.getZipcode());
					address.setRecipient_name(member.getName());
					address.setRecipient_phone(member.getPhone());
					address.setAddress_name("기본");
					address.setDefault_check('y');
					address.setMember_info('y');
					addressService.insert(address);
				} else {
					Shipping_address address = addressService.findMember_info_true(member_idx);
					if(address != null) {
						int result2 = addressService.updateAddress(address.getIdx(), member.getName(), member.getPhone(), "", "기본", member.getAddress1(), member.getAddress2(), member.getZipcode());
						if(result2 != 1) {
							// DB rollback
							return "db_error";
						}
					}
				}

			}
			return "success";
		} else {
			// DB rollback
			return "db_error";
		}
	}


	/*// 사업자회원 개인정보 수정
	@PostMapping("/mypage/modify_info_corp")
	public String modify_info_corp(@RequestParam("name") String name, @RequestParam("phone") String phone, @RequestParam("nickname") String nickname,
			@RequestParam("field_code") char field_code, @RequestParam("service_code") String service_code, 
			@RequestParam("thumbnail") MultipartFile thumbnail, HttpSession session) throws IOException { // 3. 폼에서 수정할 정보 입력후 제출했을 때
		// 서비스 코드 값 정형화
		char service_code2 = '0';
		if(service_code.equals("1,2")) { service_code2 = '3'; }
		else if(service_code.equals("1")) { service_code2 = '1'; }
		else if(service_code.equals("2")) { service_code2 = '2'; }

		else {
			System.out.println("잘못된 서비스 코드입니다.");
		}

		String thumbnail_url = "";
		int idx = (int) session.getAttribute("idx");
		String original_thumbnail = memberService.findByIdx(idx).getThumbnail();

		// 1. 파일 정보를 가져옴
		String original_file_name = thumbnail.getOriginalFilename();	// 사용자가 첨부한 원본 파일 이름
		String extension = original_file_name.substring(original_file_name.indexOf('.') + 1);
		String file_name = memberService.getUniqFileName(original_file_name);
		int file_size = (int) thumbnail.getSize();	// type casting 더 알아볼것. long으로 할지?

		if(original_file_name != null && original_file_name.length() > 0) {	// 썸네일이 첨부되었으면
			if(memberService.checkFileName(original_file_name) == false) { // 파일명 체크
				return "redirect:/mypage";
			} else {
				if(original_thumbnail != null && original_thumbnail.length() > 0) {	// 기존에 썸네일이 설정된 유저이면
					String filename = thumbnailService.find(thumbnailService.getIdx(idx, '1')).getServer_filename();	// DB에서 서버에 저장된 파일명을 가져옴
					// 2. aws s3에 upload 전 기존 파일 삭제
					amazonS3Service.deleteThumbnail("littleone", filename);		// 찾은 파일명으로 aws s3에서 해당 파일을 지움
				}

				InputStream input = null;
				BufferedImage image = null;
				BufferedImage bufferedImage = null;
				try {
					input = thumbnail.getInputStream();
					image = ImageIO.read(input);
					bufferedImage = Thumbnails.of(image).size(200, 200).asBufferedImage();	// 업로드된 썸네일 이미지 크롭
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if(input != null) {
						input.close();
						input = null;
					}
					if(image != null) {
						image.flush();
						image = null;
					}
					if(bufferedImage != null) {
						bufferedImage.flush();
						bufferedImage = null;
					}
				}

				// 3. aws s3에 thumbnail object upload.
				amazonS3Service.putThumbnailBI("thumbnail", file_name, extension, bufferedImage);	// put object to amazon s3

				// s3에 정상적으로 업로드된 경우에만 db update 할것
				Thumbnail tn = thumbnailService.findByCategory(idx, '1');
				thumbnailService.update(tn.getIdx(), original_file_name, file_name, file_size);		// thumbnail db_update
				thumbnail_url = "https://s3.ap-northeast-2.amazonaws.com/littleone/thumbnail/" + file_name;
			}

		} else {	// 썸네일이 첨부되지 않았으면
			thumbnail_url = original_thumbnail;
		}

		// 첫단계에서 비밀번호 입력으로 인증이 성공한 상태이므로, DB에 회원정보 수정
		int result = memberService.update_info(idx, name, nickname, birth, gender, phone, thumbnail_url);
		int result2 = corpService.update_info(idx, field_code, service_code2);
		if(result == 1 && result2 == 1) {
			// 주소정보가 null이 아니라 update 되었다면, Shipping_address에 기본 주소로 컬럼 추가.
			Member member = memberService.findByIdx(idx);
			if(member.getAddress1() != null && member.getAddress1().length() > 0 && member.getZipcode() != null && member.getZipcode().length() > 0) {
				// 조건 추가. 만약 shipping_address 컬럼에 해당 회원에 대한 행이 없으면
				if(addressService.findByMember_idx(idx).size() == 0) {
					Shipping_address address = new Shipping_address();
					address.setMember_idx(idx);
					address.setAddress1(member.getAddress1());
					address.setAddress2(member.getAddress2());
					address.setZipcode(member.getZipcode());
					address.setRecipient_name(member.getName());
					address.setRecipient_phone(member.getPhone());
					address.setAddress_name("기본");
					address.setDefault_check('y');
					address.setMember_info('y');
					addressService.insert(address);
				} else {
					Shipping_address address = addressService.findMember_info_true(idx);
					if(address != null) {
						int result3 = addressService.updateAddress(address.getIdx(), member.getName(), member.getPhone(), "", "기본", member.getAddress1(), member.getAddress2(), member.getZipcode());
						if(result3 != 1) {
							// DB rollback
							return "error";
						}
					}
				}
				return "redirect:/mypage";
			} else {
				// DB rollback
				return "error";
			}
		} else {
			return "error";
		}
	}*/

	@ResponseBody
	@GetMapping("/mypage/check_social")
	public String check_social(HttpSession session) {
		Member member = memberService.findByIdx((int)session.getAttribute("idx"));
		if(member.getSocial_code() == 'x') {
			return "normal";
		} else {
			return "social";
		}
	}
	
	
	@GetMapping("/mypage/modify_password")
	public String get_modify_password(Model model, HttpSession session) {
		// 비밀번호 변경 페이지로 단순 뷰 리턴
		Member member = memberService.findByIdx((int)session.getAttribute("idx"));
		if(member.getSocial_code() == 'x') {
			// 일반 회원인 경우
			pw_renew_date = member.getPw_renew_date();
			model.addAttribute("pw_renew_date", pw_renew_date);
			return "mypage/mypage_pw_change";
		} else {
			// 소셜 회원인 경우
			model.addAttribute("error_message", "접근 불가한 페이지입니다.");
			return "error/404page";
		}
		
	}

	@PostMapping("/mypage/modify_password")
	public String modify_password(@RequestParam("old_password") String old_password, @RequestParam("new_password") String new_password,
			@RequestParam("new_password_config") String new_password_config,
			HttpSession session, Model model) {
		// 1.세션에 저장된 회원의 이메일을 찾은 후, old_password로 인증
		// 2.인증 성공시 해당 회원의 비밀번호를 DB로 업데이트. 비밀번호 갱신 날짜 컬럼도 수정
		String email = (String) session.getAttribute("session_email");
		int idx = (int) session.getAttribute("idx");
		Member member = memberService.auth(email, old_password);
		if(member != null) {
			if(new_password.equals(new_password_config) == false) {
				model.addAttribute("password_same_error", "true");
				return "mypage/mypage_pw_change";	
			}
			
			if(memberService.checkPassword(new_password) == false) {
				model.addAttribute("password_validation_error", "true");
				return "mypage/mypage_pw_change";	
			}
			memberService.update_password(idx, new_password, memberService.set_now_date());
			return "redirect:/";	
		} else {
			// 인증 실패
			model.addAttribute("pw_renew_date", pw_renew_date);
			model.addAttribute("password", old_password);
			model.addAttribute("new_password", new_password);
			model.addAttribute("auth_failed", "true");
			return "mypage/mypage_pw_change";	
		}

	}

	/*@GetMapping("/mypage/member_leave")
	public String member_leave() {
		return "mypage/mypage_leave";
	}*/

	@ResponseBody
	@PostMapping("/mypage/member_leave")
	public String member_leave_confirm(@RequestParam("password") String password, HttpSession session, Model model) {
		// DB에서 삭제
		String email = (String) session.getAttribute("session_email");
		int idx = (int) session.getAttribute("idx");
		Member member = memberService.auth(email, password);		
		if(member != null) {
			try {
				if(member.getMember_type() == 'c') {
					corpService.delete(idx);
				}
				memberService.delete(idx);
			} catch(EmptyResultDataAccessException e) {
				return "error";
			}
			// 세션 삭제. attribute만 삭제하지 말고, 연관된 redis행 자체를 삭제해야함
			session.removeAttribute("session_email");
			session.removeAttribute("session_type");
			session.removeAttribute("idx");
			session.removeAttribute("nickname");
			if(session.getAttribute("session_thumbnail") != null) {
				session.removeAttribute("session_thumbnail");
			}
			if(session.getAttribute("infant_idx") != null) {
				session.removeAttribute("infant_idx");
			}
			if(session.getAttribute("group_idx") != null) {
				session.removeAttribute("group_idx");
			}
			
			String sessionId = session.getId();
			sessionService.delete_session(sessionId);	// 이행만 실행되어도 session.removeAttribute 일일히 해야하는지 체크할것

			return "success";
		} else {
			return "auth_failed";
		}

	} 

	// 소셜회원이 회원 탈퇴 이메일 인증 버튼을 눌렀을 때
	@ResponseBody
	@GetMapping("/mypage/member_leave/social")
	public String social_member_leave_confirm(HttpSession session) {
		String session_email = (String) session.getAttribute("session_email");
		String url_token = memberService.getUniqState() + memberService.getUniqState();
		String auth_url = "https://littleone.kr/mypage/member_leave/social/" + url_token;

		session.setAttribute("member_leave_token", url_token);

		Collection<String> des = new ArrayList<String>();
		des.add(session_email);

		mailService.member_leave_auth_send(des, "smart@littleone.kr", "sns_leave", null, auth_url);
		return "success";
	}

	// 소셜회원이 탈퇴메일에서 인증 URL을 클릭했을 때
	@GetMapping("/mypage/member_leave/social/{url_token}")
	public void social_member_leave_confirm(@PathVariable("url_token") String url_token, HttpSession session, HttpServletResponse response) {
		// DB에서 회원탙퇴, 세션 삭제
		String session_token = (String) session.getAttribute("member_leave_token");
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		PrintWriter out = null;

		if(session_token != null && session_token.equals(url_token)) {
			String session_email = (String) session.getAttribute("session_email");
			Collection<String> des = new ArrayList<String>();
			des.add(session_email);
			
			int idx = (int) session.getAttribute("idx");
			memberService.delete(idx);
			
			// 세션 삭제. attribute만 삭제하지 말고, 연관된 redis행 자체를 삭제해야함
			session.removeAttribute("member_leave_token");
			session.removeAttribute("session_email");
			session.removeAttribute("session_type");
			session.removeAttribute("idx");

			String sessionId = session.getId();
			sessionService.delete_session(sessionId);
			session.invalidate();
			
			mailService.sendMail(des, "smart@littleone.kr", "withdrawal", null);
			
			try {
				out = response.getWriter();
				out.println("<script type='text/javascript' charset='utf-8'>");
				out.println("alert('탈퇴 처리가 완료되었습니다.');");
				out.println("window.location.href='https://littleone.kr';");
				out.println("</script>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
			}

		} else {
			try {
				out = response.getWriter();
				out.println("<script type='text/javascript' charset='utf-8'>");
				out.println("alert('인증되지 않은 URL 주소 입니다.');");
				out.println("window.location.href='https://littleone.kr';");
				out.println("</script>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (out != null) {
					out.flush();
					out.close();
				}
			}
		}
	}

	@ResponseBody
	@PostMapping("/mypage/update_introduction")
	public String update_introduction(@RequestParam("member_idx") int member_idx, @RequestParam("introduction") String introduction, HttpSession session) {
		if((int)session.getAttribute("idx") == member_idx) {
			mngService.update_introduction(member_idx, introduction);
			return "success";
		} else {
			return "failed";
		}
	}

	@GetMapping("/mypage/test")
	public String mypage_test(HttpSession session, Model model) {
		int member_idx = (int)session.getAttribute("idx");
		Member_group group = groupService.findMember_group(member_idx);

		if(group != null) {
			List<Member_group> group_members = groupService.findByGroup_idx(group.getGroup_idx());
			model.addAttribute("group_members", group_members);
		}
		// 그룹정보, 그룹 멤버정보 (썸네일, 실명 또는 닉네임)
		return "";
	}

	@GetMapping("/mypage/setting")
	public String ddff(Model model) {
		return "setting/index";
	}

	@GetMapping("/mypage/setting/{url}")
	public String ddff2(@PathVariable("url") String url, Model model, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		Member m = memberService.findByIdx(member_idx);
		
		if("measure".equals(url)){
			Member_mng mng = mngService.findOne(member_idx);
			model.addAttribute("unit", mng.getUnit());
			return "setting/menu/measure";
		} else if("notification".equals(url)){
			return "setting/menu/notification";
		} else if("withdrawal".equals(url)){
			return "setting/menu/withdrawal";
		} else if("subscribe".equals(url)){
			Notify_email mail = mailService.find_notify_email(m.getEmail());
			if(mail != null) {
				model.addAttribute("subscribe_mail", true);
			} else {
				model.addAttribute("subscribe_mail", false);
			}
			return "setting/menu/subscribe";
		}
		return "서버 오류입니다.";
	}
	
	@ResponseBody
	@GetMapping("/mypage/setting/measure/{unit_type}")
	public String setting_unit(@PathVariable("unit_type") char unit_type, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		if(unit_type == 'c') {
			mngService.update_unit(member_idx, 'c');
		} else if(unit_type == 'f') {
			mngService.update_unit(member_idx, 'f');
		} else {
			return "unit_failed";
		}
		
		return "success";
	}
	
	@ResponseBody
	@PostMapping("/mypage/setting/switch")
	public String setting_type(@RequestParam("optionName") String option, @RequestParam("optionVal") String value, HttpSession session) {
		int member_idx = (int) session.getAttribute("idx");
		
		
		if(!value.equals("true") && !value.equals("false")) {
			return "option_value_failed";
		}
		
		boolean value2 = Boolean.parseBoolean(value);
		
		if(option.equals("option-email-subscribe")) {
			String email = memberService.findByIdx(member_idx).getEmail();
			if(value2) {
				mailService.save_notify_email(email);
			} else {
				Notify_email ne = mailService.find_notify_email(email);
				if(ne != null) {
					mailService.delete_notify_email(email);
				}
			}
			
			return "email_success";
		}
		
		return "success";
		
	}
}
